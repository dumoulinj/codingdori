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

	public static void main(String[] args) throws IOException {
		
		String [] inputs = new String[]{"data/me_at_the_zoo.in", "data/trending_today.in", "data/videos_worth_spreading.in", "data/kittens.in"};
		String [] ouputs = new String[]{"results/me_at_the_zoo.out", "results/trending_today.out", "results/videos_worth_spreading.out", "results/kittens.out"};
		
		List<Solver> solvers = new ArrayList<>();
		
		solvers.add(new FirstSolver());
		solvers.add(new SecondSolver());
		
		for(int i = 2; i < inputs.length; i++){
			Reader reader = new Reader(inputs[i]);
			
			Solver solver = new ReverseSolver();
			List<Cache> caches = solver.solve(reader.getCaches());
			
			Writer writer = new Writer();
			
			writer.save(ouputs[i], caches);
			
			System.out.println("Finished "+inputs[i]);
		}
		
		
	}

}
