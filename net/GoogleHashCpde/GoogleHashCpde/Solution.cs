﻿using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using GoogleHashCpde.Object;
using static System.String;

namespace GoogleHashCpde
{
    class Solution
    {
        public Dictionary<Cache, ISet<Video>> Results { get; private set; }
        private Configuration _conf;

        public Solution(Configuration conf)
        {
            Results = new Dictionary<Cache, ISet<Video>>();
            _conf = conf;
        }

        public void WriteToFile(string file)
        {
            var content = new string[Results.Count+1];
            var fRes = Results.Where(r => r.Value.Count > 0).ToDictionary(k=>k.Key,k=>k.Value);
            content[0] = fRes.Count.ToString();
            var vCache = fRes.Select(s =>
            {
                var val = Join(" ", s.Value.Select(v => v.Id.ToString()).ToList());
                return $"{s.Key.Id} {val}";
            }).ToArray();
            Array.Copy(vCache, 0, content, 1, vCache.Length);
            File.WriteAllLines(file, content);
        }

        public int Evaluate()
        {
            var score = 0;
            var denom = 0;
            foreach (var request in _conf.Requests)
            {
                var baseLat = request.EndPoint.Latency;
                var minLat = baseLat;
                foreach (var epclat in request.EndPoint.EPCacheLat)
                {
                    ISet<Video> videos;
                    if (Results.TryGetValue(epclat.Cache, out videos))
                    {
                        if (videos.Contains(request.Video))
                        {
                            if (epclat.Latency < minLat)
                            {
                                minLat = epclat.Latency;
                            }
                        }
                    }
                }
                score += ((baseLat - minLat) * request.Number *1000);
                denom += request.Number;

            }
            return score/denom;
        }
        

        public bool PutVideoInCache(Cache cache, Video v)
        {  
            if (cache.PutSize(v.Size))
            {
                ISet<Video> videos;
                if (!Results.TryGetValue(cache, out videos))
                {
                    videos = new HashSet<Video>();
                    Results.Add(cache, videos);
                }
                videos.Add(v);
                return true;
            }
            return false;
        }

        public void RemoveVideoFromCache(Cache cache, Video v)
        {
            ISet<Video> videos;
            if (Results.TryGetValue(cache, out videos))
            {
                videos.Remove(v);
            }
            cache.Removesize(v.Size);
        }

    }
}
