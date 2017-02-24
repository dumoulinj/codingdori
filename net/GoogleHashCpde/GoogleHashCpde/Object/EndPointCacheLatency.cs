namespace GoogleHashCpde.Object
{
    class EndPointCacheLatency
    {
        public Cache Cache { get; private set; }
        public int Latency { get; private set; }

        public EndPointCacheLatency(Cache cache, int latency)
        {
            Cache = cache;
            Latency = latency;
        }
    }
}