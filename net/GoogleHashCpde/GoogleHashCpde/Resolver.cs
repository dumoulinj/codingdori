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
            var cacheVideo = new List<Request>[_conf.NumberCache, _conf.NumberVideo];
            var gains = new ulong[_conf.NumberCache, _conf.NumberVideo];

            for (var i = 0; i < _conf.NumberCache; i++)
            {
                
                for (var j = 0; j < _conf.NumberVideo; j++)
                {
                    cacheVideo[i, j] = new List<Request>();
                }
            }

            foreach (var request in _conf.Requests.OrderByDescending(r => r.Number))
            {
                var vId = request.VideoId;
                var endPoint = _conf.EndPoints[request.EndPointId];
                foreach (var epCacheL in endPoint.EPCacheLat)
                {
                    gains[epCacheL.Cache.Id, vId] += ((ulong)request.Number) * ((ulong)(endPoint.Latency - epCacheL.Latency));
                    cacheVideo[epCacheL.Cache.Id, vId].Add(request);
                }
            }
            long idx = 0;
            while (true)
            {
                var c = 0;
                var v = 0;
                ulong best = 0L;
                for (var i = 0; i < _conf.NumberCache; i++)
                {
                    for (var j = 0; j < _conf.NumberVideo; j++)
                    {
                        var s = gains[i, j] /((ulong)_conf.Videos[j].Size); //(_conf.Caches[i].Size/_conf.Videos[j].Size);/  
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
                        var irs = cacheVideo[c, v];
                        foreach (var r in irs)
                        {
                            var choooseLink = _conf.EndPoints[r.EndPointId].EPCacheLat.Single(l => l.Cache.Id == c);
                            foreach (var link in _conf.EndPoints[r.EndPointId].EPCacheLat.Where(l => l.Cache.Id != c))
                            {

                                foreach (var sr in cacheVideo[link.Cache.Id,v].Where(sr=>sr.EndPointId==r.EndPointId))
                                {
                                    sr.CompLatency = choooseLink.Latency;
                                }
                                var d=cacheVideo[link.Cache.Id, v].RemoveAll(sr => sr.EndPointId == r.EndPointId && sr.CompLatency <= link.Latency);
                                gains[link.Cache.Id, v] = cacheVideo[link.Cache.Id, v].Aggregate(0Ul,(a,b) =>
                                {
                                    var ep = _conf.EndPoints[b.EndPointId];

                                    return a+(ulong)b.Number *
                                           (ulong)(b.CompLatency -
                                                   ep.EPCacheLat.Single(l => l.Cache.Id == link.Cache.Id).Latency);
                                });

                            }
                        }
                        foreach (var dVid in _conf.Videos.Where(sv => sv.Size > _conf.Caches[c].RemainSize)
                                .Select(sv => sv.Id))
                        {
                            cacheVideo[c, dVid].Clear();
                            gains[c, dVid] = 0;
                        }
                           

                    }
                }
                else
                {
                    Console.WriteLine("bizarre");
                }
                cacheVideo[c, v].Clear();
                gains[c, v] = 0;
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