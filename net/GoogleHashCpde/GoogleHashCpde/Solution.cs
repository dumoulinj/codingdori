using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Numerics;
using System.Text;
using System.Threading.Tasks;
using GoogleHashCpde.Object;
using static System.String;

namespace GoogleHashCpde
{
    class Solution
    {
        public Dictionary<Cache, ISet<Video>> Results { get; private set; }

        private readonly object[][] _optiRes;

        private Configuration _conf;
        private BigInteger denom;
        public Solution(Configuration conf)
        {
            Results = new Dictionary<Cache, ISet<Video>>();
            _conf = conf;
            denom = BigInteger.Zero;
            foreach (var request in _conf.Requests)
            {
                denom += request.Number;
            }
            _optiRes = new object[_conf.NumberCache][];
            for (var i = 0; i < _conf.NumberCache; i++)
            {
                _optiRes[i] = new object[_conf.NumberVideo];
                for (var j = 0; j < _conf.NumberVideo; j++)
                {
                    _optiRes[i][j] = false;
                }
            }
        }

        public Solution(Configuration conf, Dictionary<Cache, ISet<Video>> results)
        {
            _conf = conf;
            Results= new Dictionary<Cache, ISet<Video>>();
            foreach (var r in results)
            {
                var nv = new HashSet<Video>();
                foreach (var v in r.Value)
                {
                    nv.Add(v);
                }
                Results.Add(r.Key,nv);
            }
        }

        public void WriteToFile(string file)
        {
            var res = new List<String>();
            for (var i = 0; i < _conf.NumberCache; i++)
            {
                var idx = new List<String>();
                for (var j = 0; j < _conf.NumberVideo; j++)
                {
                    if ((bool) _optiRes[i][j])
                    {
                        idx.Add(j.ToString());
                    }
                }
                if (idx.Count > 0)
                {
                    res.Add($"{i} {string.Join(" ", idx)}");

                }
            }
            var content = new String[res.Count + 1];

            content[0] = res.Count.ToString();
            Array.Copy(res.ToArray(), 0, content, 1, res.Count);
            File.WriteAllLines(file, content);
        }

        public BigInteger Evaluate()
        {

            var score = BigInteger.Zero;
            foreach (var request in _conf.Requests)
            {
                var endPoint = _conf.EndPoints[request.EndPointId];
                var baseLat = endPoint.Latency;
                var minLat = baseLat;
                foreach (var epclat in endPoint.EPCacheLat)
                {

                    if ((bool) _optiRes[epclat.Cache.Id][request.VideoId])
                    {
                        if (minLat > epclat.Latency)
                        {
                            minLat = epclat.Latency;
                        }
                    }
                  
                }
                BigInteger t = (baseLat - minLat);
                t *= (request.Number * 1000);
                score += t;
            }
            return score / denom;

        }


        public bool PutVideoInCache(Cache cache, Video v)
        {
            if (!cache.PutSize(v.Size)) return false;
            _optiRes[cache.Id][v.Id] = true;
              
            return true;
        }

        public bool IsPlaced(Cache cache, Video v)
        {
            return (bool) _optiRes[cache.Id][v.Id];
        }

        public void RemoveVideoFromCache(Cache cache, Video v)
        {
            if (!(bool) _optiRes[cache.Id][v.Id]) return;
            _optiRes[cache.Id][v.Id] = false;
            cache.Removesize(v.Size);
        }
    }
}
