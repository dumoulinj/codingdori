using System;
using System.CodeDom;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using GoogleHashCpde.Object;

namespace GoogleHashCpde
{
    class Configuration
    {
        public int NumberVideo { get; private set; }
        public int NumberEndPoints { get; private set; }
        public int NumberRequest { get; private set; }
        public int NumberCache { get; private set; }
        public int CacheCapacity{ get; private set; }

        public Video[] Videos { get; private set; }
        public EndPoint[] EndPoints { get; private set; }
        public Cache[] Caches { get; private set; }
        public Request[] Requests { get; private set; }


        public Configuration(int numberVideos, int numberEndPoints, int numberRequest, int numberCaches, int cacheCapacity)
        {
            this.NumberVideo = numberVideos;
            this.NumberEndPoints = numberEndPoints;
            this.NumberRequest = numberRequest;
            this.NumberCache = numberCaches;
            this.CacheCapacity = cacheCapacity;
            Videos = new Video[NumberVideo];
            EndPoints = new EndPoint[NumberEndPoints];
            Caches = new Cache[numberCaches];
            Requests=new Request[numberRequest];
        }

        public static Configuration ReadFromFile(string file)
        {
            var content = File.ReadAllLines(file);
            var first = content[0].Split(' ');
            var idx = 1;
            var conf = new Configuration(int.Parse(first[0]), int.Parse(first[1]), int.Parse(first[2]), int.Parse(first[3]),int.Parse(first[4]));
            for (int i = 0; i < conf.NumberCache; i++)
            {
                conf.Caches[i] = new Cache(i,conf.CacheCapacity);
            }
            
            {
                var line = content[idx++];
                for (int i = 0; i < conf.NumberVideo; i++)
                {
                    var splits = line.Split(' ');
                    conf.Videos[i] = new Video(i, int.Parse(splits[i]));
                }
            }
            for (int i = 0; i < conf.NumberEndPoints; i++)
            {
                var line = content[idx];
                var splits = line.Split(' ');
                var lat = int.Parse(splits[0]);
                var cacheNumber = int.Parse(splits[1]);
                var caches = new List<EndPointCacheLatency>();
                idx++;
                for (int j = 0; j < cacheNumber; j++, idx++)
                {
                    var sline = content[idx];
                    var ssplits = sline.Split(' ');
                    var cacheId = int.Parse(ssplits[0]);
                    var slat = int.Parse(ssplits[1]);
                    var epc = new EndPointCacheLatency(conf.Caches[cacheId], slat);
                    caches.Add(epc);
                }
                conf.EndPoints[i] = new EndPoint(i,lat,caches.ToArray());
            }
            for (int i = 0; i < conf.NumberRequest; i++, idx++)
            {
                var line = content[idx];
                var splits = line.Split(' ');
                var vid = int.Parse(splits[0]);
                var eid = int.Parse(splits[1]);
                var req = int.Parse(splits[2]);
                conf.Requests[i] = new Request(conf.Videos[vid],conf.EndPoints[eid], req);
            }
            return conf;
        }
    }
}
