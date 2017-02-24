using System;
using System.Collections.Generic;
using System.Linq;
using System.Numerics;
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

            var requests = _conf.Requests.Select(r => new { V = r.Video, EndPoint = r.EndPoint, Number=r.Number }).ToList();
            foreach (var req in requests.OrderByDescending(r => r.EndPoint.Latency))
            {
                var caches = req.EndPoint.EPCacheLat.OrderBy(epc => epc.Latency).Select(e => e.Cache).ToList();
                foreach (var cache in caches)
                {
                    if (sol.PutVideoInCache(cache, req.V))
                    {
                        break;
                    }
                }

            }

            //Randomize video in cache
            //var maxScore = sol.Evaluate();
            //var maxSol = sol.Duplicate();
            //var nTries = 10;
            //for (int i = 0; i < nTries; i++)
            //{
            //    var curSol = sol.Duplicate();
            //    var curScore = curSol.Evaluate();
            //    foreach (var r in curSol.Results)
            //    {
            //        var allVideos = _conf.Videos.Where(v => r.Value.Contains())
            //    }
            //    if (maxScore < curScore)
            //    {
            //        maxScore = curScore;
            //        maxSol = curSol;
            //    }
            //}
            MaximizeSolution(sol);
            return sol;
        }

        private void MaximizeSolution(Solution sol)
        {
            var videos = _conf.Videos.OrderBy(v => v.Size).ToArray();
            foreach (var cache in _conf.Caches)
            {
                var i = 0;
                var cond = true;
                while (cond)
                {
                    if (!sol.PutVideoInCache(cache, videos[i]))
                    {
                        cond = false;
                    }
                    else
                    {
                        i++;
                    }
                }
            }
        }
        public Solution Resolve2()
        {
            var sol = new Solution(_conf);
            var videos = _conf.Videos.OrderBy(v => v.Size).ToList();
            var previousScore = BigInteger.Zero;
            foreach (var c in _conf.Caches)
            {
                var scores = new Dictionary<Video, BigInteger>();
                foreach (var v in videos)
                {
                    if (sol.PutVideoInCache(c, v))
                    {
                        var curScore = sol.Evaluate();
                        var gain = curScore - previousScore;
                        previousScore = curScore;
                        scores.Add(v, gain);
                        sol.RemoveVideoFromCache(c,v);
                    }
                   
                }
                foreach (var pair in scores.OrderByDescending(s=>s.Value))
                {
                    sol.PutVideoInCache(c,pair.Key);
                }
            }

            return sol;
        }

        public Solution Resolve3()
        { 

            var sol = new Solution(_conf);


            var requests =_conf.Requests.OrderBy(r => r.Number).Select(r=>new {V=r.Video, EndPoint=r.EndPoint}).ToList();

            foreach (var req in requests)
            {
                var caches = req.EndPoint.EPCacheLat.OrderBy(epc => epc.Latency).Select(e=>e.Cache).ToList();
                foreach (var cache in caches)
                {
                    if (sol.PutVideoInCache(cache, req.V))
                    {
                        break;
                    }
                }
                
            }

            //Randomize video in cache
            //var maxScore = sol.Evaluate();
            //var maxSol = sol.Duplicate();
            //var nTries = 10;
            //for (int i = 0; i < nTries; i++)
            //{
            //    var curSol = sol.Duplicate();
            //    var curScore = curSol.Evaluate();
            //    foreach (var r in curSol.Results)
            //    {
            //        var allVideos = _conf.Videos.Where(v => r.Value.Contains())
            //    }
            //    if (maxScore < curScore)
            //    {
            //        maxScore = curScore;
            //        maxSol = curSol;
            //    }
            //}
            return sol;
        }
    }
}
