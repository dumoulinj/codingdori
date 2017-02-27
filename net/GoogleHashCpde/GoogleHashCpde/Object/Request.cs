namespace GoogleHashCpde.Object
{
    class Request
    {

        public int VideoId{ get; private set; }
        public int EndPointId { get; private set; }
        public int Number { get; private set; }

        public int CompLatency { get; set; }

        public Request(int videoId, int endPointId, int number, int latency)
        {
            VideoId = videoId;
            EndPointId = endPointId;
            Number = number;
            CompLatency = latency;
        }
    }
}