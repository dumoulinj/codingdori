namespace GoogleHashCpde.Object
{
    class Request
    {
        public Video Video { get; private set; }
        public EndPoint EndPoint { get; private set; }
        public int Number { get; private set; }

        public Request(Video video, EndPoint endPoint, int number)
        {
            Video = video;
            EndPoint = endPoint;
            Number = number;
        }
    }
}