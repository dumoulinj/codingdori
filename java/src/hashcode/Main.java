package hashcode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hashcode.data.Cache;
import hashcode.io.Reader;
import hashcode.io.Writer;
import hashcode.solver.FirstSolver;
import hashcode.solver.FourthSolver;
import hashcode.solver.ReverseSolver;
import hashcode.solver.SecondSolver;
import hashcode.solver.Solver;
import hashcode.solver.ThirdSolver;

public class Main {

	private static String [] inputs = new String[]{"data/me_at_the_zoo.in", "data/trending_today.in", "data/videos_worth_spreading.in", "data/kittens.in"};
	private static String [] ouputs = new String[]{"results/me_at_the_zoo.out", "results/trending_today.out", "results/videos_worth_spreading.out", "results/kittens.out"};
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		all();
		
		//one(3);
		
		//one(2);
		
	}
	
	private static void one(int i) throws IOException, InterruptedException{
		long start = System.currentTimeMillis();
		
		Reader reader = new Reader(inputs[i]);
		System.out.println("Read data in "+(System.currentTimeMillis() - start));
		
		start = System.currentTimeMillis();
		Solver solver = new ReverseSolver();
		List<Cache> caches = solver.solve(reader.getCaches());
		
		Writer writer = new Writer();
		
		writer.save(ouputs[i], caches);
		
		System.out.println("Finished "+inputs[i]+" in  "+(System.currentTimeMillis() - start));
	}
	
	private static void all() throws IOException, InterruptedException{
		for(int i = 0; i < inputs.length; i++){
			one(i);
		}
	}

}
