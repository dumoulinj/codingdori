using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GoogleHashCpde
{
    class Solution
    {
        public void WriteToFile(string file)
        {
            var content = new string[10];
            //content[0] = _slices.Count.ToString();
            //var slices = _slices.Select(s => s.ToSolution()).ToArray();
            //Array.Copy(slices, 0, content, 1, slices.Length);
            File.WriteAllLines(file, content);
        }

        public int Evaluate()
        {
            throw new NotImplementedException();
        }
    }
}
