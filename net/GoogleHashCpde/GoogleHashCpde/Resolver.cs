using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

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
           
            sol.PutVideoInCache(_conf.Caches[0], _conf.Videos[0]);
            return sol;
        }
    }
}
