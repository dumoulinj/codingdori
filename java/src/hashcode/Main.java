package hashcode;

import java.awt.image.RescaleOp;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import hashcode.data.Cache;
import hashcode.data.Endpoint;
import hashcode.io.Reader;
import hashcode.io.Writer;
import hashcode.solver.ReverseSolver;
import hashcode.solver.Solver;
import hashcode.solver.TestSolver;

public class Main {

	private static String [] inputs = new String[]{"data/me_at_the_zoo.in", "data/videos_worth_spreading.in", "data/trending_today.in", "data/kittens.in"};
	private static String [] inputsAntonio = new String[]{"../antonio/inputs/me_at_the_zoo_.in", "../antonio/inputs/videos_worth_spreading_.in", "../antonio/inputs/trending_today_.in", "../antonio/inputs/kittens_.in"};
	
	private static String [] ouputs = new String[]{"results/me_at_the_zoo.out", "results/videos_worth_spreading.out", "results/trending_today.out", "results/kittens.out"};
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		//testParameters(0);
		//all();
		
		/*one(0);
		one(1);
		one(2);*/
		
		one(3);
		
	}
	
	private static void testParameters(int problem) throws IOException, InterruptedException{
		
		for(int i = 0; i <= 10; i++){
			Cache.BASE_SCORE = i * 0.1;
			Cache.SIZE_SCORE = (1 - Cache.BASE_SCORE);
			
			one(inputs[problem], null);
		}
	}
	
	private static void one(int i) throws IOException, InterruptedException{
		one(inputs[i], ouputs[i]);
	}
	
	private static void one(String input, String output) throws IOException, InterruptedException{

		long start = System.currentTimeMillis();
		Reader reader = new Reader(input);
		//System.out.println("Read data in "+(System.currentTimeMillis() - start));
		
		Solver solver = new ReverseSolver();
		List<Cache> caches = solver.solve(reader.getCaches());
		
		System.out.println("Score : "+getScore(reader.getEndpoints())+" BASE = "+Cache.BASE_SCORE);
		
		if(output != null) {
			Writer writer = new Writer();
			
			writer.save(output, caches);
			System.out.println("Saved in "+output);
		}else{
			System.out.println("Dont save result");
		}
		
	}
	
	private static BigInteger getScore(List<Endpoint> endpoints){
		BigInteger total = BigInteger.ZERO;
		BigInteger totalRequests =  BigInteger.ZERO;
		
		for(Endpoint endpoint : endpoints){
			long savedTime = endpoint.getTimeSaved();
			total = total.add(BigInteger.valueOf(savedTime * 1000));
			
			long allRequests = endpoint.getTotalRequests();
			
			totalRequests = totalRequests.add(BigInteger.valueOf(allRequests));
			
			//System.out.println(savedTime +" "+totalRequests);
		}
		
		return total.divide(totalRequests);
	}
	
	private static void all() throws IOException, InterruptedException{
		for(int i = 0; i < inputs.length; i++){
			long start = System.currentTimeMillis();
			one(i);
			
			System.out.println("Finished "+inputs[i]+" in  "+(System.currentTimeMillis() - start));
		}
	}

}
