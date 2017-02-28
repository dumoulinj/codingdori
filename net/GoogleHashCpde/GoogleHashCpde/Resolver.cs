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

        public Solution Resolve(int bigGain)
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
            var candidates = new Tuple<int,int>[2];
            var bests = new ulong[2];
            ulong v1=0;
            ulong v2=0;
            ulong v3 = 0;
            ulong same = 0;
            int c;
            int v;
            while (true)
            {
                for (var i = 0; i < bests.Length; i++)
                {
                    bests[i] = 0UL;
                }
                for (var i = 0; i < _conf.NumberCache; i++)
                {
                    for (var j = 0; j < _conf.NumberVideo; j++)
                    {
                        var s1 = gains[i, j] ;//; //(_conf.Caches[i].Size/_conf.Videos[j].Size);/  
                        var s2 = gains[i, j] / (ulong) _conf.Videos[j].Size;
                        if (s1 > bests[0])
                        {
                            bests[0] = s1;
                            candidates[0] = new Tuple<int, int>(i,j);
                        }
                        if (s2 > bests[1])
                        {
                            bests[1] = s2;
                            candidates[1] = new Tuple<int, int>(i,j);
                        }
                    }
                }
                if (bests[0] == 0)
                {
                    break;
                }
                if (candidates[0].Item1 == candidates[1].Item1 && candidates[0].Item2 == candidates[1].Item2)
                {
                    c = candidates[0].Item1;
                    v = candidates[0].Item2;
                    same++;
                }
                else
                {
                    var c1 = _conf.Caches[candidates[0].Item1];
                    if (c1.RemainSize < candidates[0].Item2)
                    {
                        v3++;
                        c = candidates[1].Item1;
                        v = candidates[1].Item2;
                    }
                    else
                    {
                        if (idx<bigGain)
                        {
                            v1++;
                            c = candidates[0].Item1;
                            v = candidates[0].Item2;
                        }
                        else
                        {
                            v2++;
                            c = candidates[1].Item1;
                            v = candidates[1].Item2;
                        }
                        //var g1 = gains[candidates[0].Item1, candidates[0].Item2];
                        //var g2 = gains[candidates[1].Item1, candidates[1].Item2];
                        //var s1 = _conf.Videos[candidates[0].Item2].Size;
                        //var s2 = _conf.Videos[candidates[1].Item2].Size;
                        //if (g1 / g2 > (ulong) (s1 / s2))
                        //{
                            
                        //}
                       
                    }

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
            Console.WriteLine($"v1 :{v1} v2:{v2} v3:{v3} same: {same}");
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