using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GoogleHashCpde
{
    class Configuration
    {
        public static Configuration ReadFromFile(string file)
        {
            var content = File.ReadAllLines(file);
            var first = content[0].Split(' ');
            var conf = new Configuration();
            //var conf = new Configuration(int.Parse(first[0]), int.Parse(first[1]), int.Parse(first[2]), int.Parse(first[3]));

            //for (int i = 0; i < conf.Row; i++)
            //{
            //    var line = content[i + 1];
            //    for (int j = 0; j < conf.Column; j++)
            //    {
            //        conf.Pizza[i, j] = line[j];
            //    }
            //}
            return conf;
        }
    }
}
