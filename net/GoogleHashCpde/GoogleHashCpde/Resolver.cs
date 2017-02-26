using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http.Headers;
using System.Numerics;
using System.Runtime.CompilerServices;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading.Tasks;
using GoogleHashCpde.Object;

namespace GoogleHashCpde
{
    class Resolver
    {
        private Configuration _conf;

        public Resolver(Configuration conf)
        {
            _conf = conf;
        }

        public Solution Resolve()
        {
            var sol = new Solution(_conf);
            var gains = new long[_conf.NumberCache, _conf.NumberVideo];
            var concernedVideos = new Dictionary<Cache, Dictionary<Video, long>>();
            var removed = new bool[_conf.NumberCache, _conf.NumberVideo];
            foreach (var request in _conf.Requests.OrderByDescending(r => r.Number))
            {
                var v = request.Video;

                foreach (var epCacheL in request.EndPoint.EPCacheLat)
                {
                    gains[epCacheL.Cache.Id, v.Id] += request.Number * (request.EndPoint.Latency - epCacheL.Latency);
                }
            }
            long idx = 0;
            while (true)
            {
                var c = 0;
                var v = 0;
                var best = 0L;
                for (var i = 0; i < _conf.NumberCache; i++)
                {
                    for (var j = 0; j < _conf.NumberVideo; j++)
                    {
                        var s = gains[i, j] / _conf.Videos[j].Size; //(_conf.Caches[i].Size/_conf.Videos[j].Size);
                        if (s <= best) continue;
                        best = s;
                        c = i;
                        v = j;
                    }
                }
                if (best == 0)
                {
                    break;
                }

                if (!sol.IsPlaced(_conf.Caches[c], _conf.Videos[v]))
                {
                    if (sol.PutVideoInCache(_conf.Caches[c], _conf.Videos[v]))
                    {
                        foreach (var r in _conf.Requests.Where(r => r.Video.Id == v))
                        {
                            if (r.EndPoint.EPCacheLat.Any(cachLat => cachLat.Cache.Id == c))
                            {
                                foreach (var epCacheL in r.EndPoint.EPCacheLat.Where(l=>l.Cache.Id != c))
                                {
                                    gains[epCacheL.Cache.Id, v] -= r.Number * (r.EndPoint.Latency - epCacheL.Latency);
                                }
                            }
                        }
                    }
                }
                gains[c, v] = 0;
                if (idx == 10000)
                {
                    Console.WriteLine("Terminated because of 10000");
                    break;
                }
                idx++;
            }
           Console.WriteLine($"Before opti: {sol.Evaluate()}");
            for (var i = 0; i < _conf.NumberCache; i++)
            {
                var rs = _conf.Caches[i].RemainSize;
                foreach (var v in _conf.Videos.Where(v => v.Size < rs))
                {
                    if (!sol.IsPlaced(_conf.Caches[i], v))
                    {
                        sol.PutVideoInCache(_conf.Caches[i], v);
                    }
                }
            }

            Console.WriteLine($"After opti: {sol.Evaluate()}");
            // MaximizeSolution(sol);
            return sol;
        }

        //public Solution ResolveOld()
        //{
        //    var sol = new Solution(_conf);
        //    var videos = _conf.Videos.OrderBy(v => v.Size).ToList();
        //    BigInteger previousScore = BigInteger.Zero;
        //    foreach (var c in _conf.Caches)
        //    {
        //        var scores = new Dictionary<Video, BigInteger>();
        //        foreach (var v in videos)
        //        {
        //            if (sol.PutVideoInCache(c, v))
        //            {
        //                var curScore = sol.Evaluate();
        //                var gain = curScore - previousScore;
        //                previousScore = curScore;
        //                scores.Add(v, gain);
        //                sol.RemoveVideoFromCache(c, v);
        //            }

        //        }
        //        foreach (var pair in scores.OrderByDescending(s => s.Value))
        //        {
        //            sol.PutVideoInCache(c, pair.Key);
        //        }
        //    }
        //    return sol;
        //}


        //public Solution Resolve3()
        //{ 

        //    var sol = new Solution(_conf);

        //    var requests =_conf.Requests.OrderBy(r => r.Number).Select(r=>new {r.Video, r.EndPoint}).ToList();
        //    foreach (var req in requests)
        //    {
        //        var caches = req.EndPoint.EPCacheLat.OrderBy(epc => epc.Latency).Select(e=>e.Cache).ToList();
        //        foreach (var cache in caches)
        //        {
        //            if (sol.PutVideoInCache(cache, req.Video))
        //            {
        //                break;
        //            }
        //        }

        //    }
        //    return sol;
        //}
    }
}