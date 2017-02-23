namespace GoogleHashCpde.Object
{
    class Video
    {
        public int Size { get; private set; }
        public int Id { get; private set; }

        public Video(int id, int size)
        {
            Size = size;
            Id = id;
        }

        protected bool Equals(Video other)
        {
            return Id == other.Id;
        }

        public override bool Equals(object obj)
        {
            if (ReferenceEquals(null, obj)) return false;
            if (ReferenceEquals(this, obj)) return true;
            if (obj.GetType() != this.GetType()) return false;
            return Equals((Video) obj);
        }

        public override int GetHashCode()
        {
            return Id;
        }
    }
}
