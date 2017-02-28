using System;
using System.Collections.Generic;
using System.Configuration;
using System.IO;
using System.Linq;
using System.Numerics;
using System.Text;
using System.Threading.Tasks;

namespace GoogleHashCpde
{
    class Program
    {
        static void Main(string[] args)
        {
            var cdir = Directory.GetCurrentDirectory();
            var baseFolder =Path.Combine(cdir, "DS");
            var result = new Dictionary<string, BigInteger>();
            for (var i = 0; i < int.MaxValue; i++)
            {
                Console.WriteLine($"Starting {i}");

                foreach (var f in Directory.GetFiles(baseFolder, "*.in").OrderBy(Path.GetFileNameWithoutExtension))
                {

                    var name = Path.GetFileNameWithoutExtension(f);
                    Console.WriteLine($"Starting {name}  at {DateTime.Now}");
                    var conf = Configuration.ReadFromFile(f);
                    Console.WriteLine($"Conf read {name} at {DateTime.Now}");
                    var sol = new Resolver(conf).Resolve(i);
                    var eval = sol.Evaluate();
                    Console.WriteLine($"{name} {eval}  at {DateTime.Now}");
                    BigInteger curSol;
                    if (!result.TryGetValue(name, out curSol))
                    {
                        curSol = BigInteger.Zero;
                    }
                    if (eval > curSol)
                    {
                        result[name] = eval;
                        sol.WriteToFile($"{baseFolder}/{name}_{eval}.out");

                    }
                    
                    
                }
            }
            Console.ReadLine();
        }
    }
}
