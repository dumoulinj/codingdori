package hashcode.solver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import hashcode.data.Cache;

public class TestSolver implements Solver{

	@Override
	public List<Cache> solve(List<Cache> caches) throws InterruptedException {
		
		List<Cache> temp = new ArrayList<>(caches);
		
		do{
			Cache best = null;
			
			for(Cache cache : temp){
				if(best == null || best.getRemainingScore() < cache.getRemainingScore()){
					best = cache;
				}
			}
			
			best.solveCache();
			
			temp.remove(best);
			
		}while(temp.size() > 0);
		

		return caches;
	}

}
