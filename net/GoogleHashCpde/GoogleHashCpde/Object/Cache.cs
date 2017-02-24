namespace GoogleHashCpde.Object
{
    class Cache
    {
        public int Id { get; private set; }
        public int Size { get; private set; }

        private int _remainSize;
        public Cache(int id, int size)
        {
            Id = id;
            Size = size;
            _remainSize = size;
        }

        public bool PutSize(int size)
        {
            if (_remainSize < size)
            {
                return false;
            }
            _remainSize -= size;
            return true;
        }

        public void Removesize(int size)
        {
            _remainSize += size;
        }
        protected bool Equals(Cache other)
        {
            return Id == other.Id;
        }

        public override bool Equals(object obj)
        {
            if (ReferenceEquals(null, obj)) return false;
            if (ReferenceEquals(this, obj)) return true;
            if (obj.GetType() != this.GetType()) return false;
            return Equals((Cache) obj);
        }

        public override int GetHashCode()
        {
            return Id;
        }
    }
}