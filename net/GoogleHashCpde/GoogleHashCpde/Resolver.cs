using System;
using System.Collections.Generic;
using System.Linq;
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
          
            return sol;
        }
    }
}
