using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GoogleHashCpde
{
    class Program
    {
        static void Main(string[] args)
        {
            var baseFolder = @"c:\temp\";
            foreach (var f in Directory.GetFiles(baseFolder, "*.in").OrderBy(Path.GetFileNameWithoutExtension))
            {
                var name = Path.GetFileNameWithoutExtension(f);
                Console.WriteLine($"Starting {name}  at {DateTime.Now}");
                var conf = Configuration.ReadFromFile(f);
                Console.WriteLine($"Conf read {name} at {DateTime.Now}");
                var sol = new Resolver(conf).Resolve();
                
                Console.WriteLine(name + " " + sol.Evaluate());
                sol.WriteToFile($"{baseFolder}{name}.out");
            }
        }
    }
}
