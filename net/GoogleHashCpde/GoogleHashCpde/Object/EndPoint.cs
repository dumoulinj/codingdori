using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.AccessControl;
using System.Text;
using System.Threading.Tasks;

namespace GoogleHashCpde.Object
{
    class EndPoint
    {
        public int Id { get; private set; }
        public int Latency { get; private set; }
        public EndPointCacheLatency[] EPCacheLat { get; private set; }

        public EndPoint(int id, int latency, EndPointCacheLatency[] epCacheLat)
        {
            Id = id;
            Latency = latency;
            EPCacheLat = epCacheLat;
        }
    }
}
