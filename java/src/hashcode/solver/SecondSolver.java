package hashcode.solver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import hashcode.data.Cache;
import hashcode.data.MetaVideoRequest;
import hashcode.data.VideoRequest;

public class SecondSolver implements Solver {

	@Override
	public List<Cache> solve(List<Cache> caches) {

		List<Cache> cachesTemp = new ArrayList<>(caches);
		
		for(final Cache cache : cachesTemp){
			cache.clearCache();
			
			List<MetaVideoRequest> requests = new ArrayList<>(cache.getRequests());
			
			requests.sort(new Comparator<MetaVideoRequest>() {

				@Override
				public int compare(MetaVideoRequest o1, MetaVideoRequest o2) {
					int gain1 = o1.getTimeGain();
					int gain2 = o2.getTimeGain();
					
					if(gain1 != gain2){
						return o1.getVideo().getSize() - o2.getVideo().getSize();
					}
					
					return gain2 - gain1;
				}
			});
			
			for(int i = 0; i < requests.size(); i++){
				MetaVideoRequest request = requests.get(i);
				
				if(request.getVideo().getSize() <= cache.getAvailableSpace()){
					cache.cacheVideo(request.getVideo());
				}
			}
		}
		
		return caches;
	}

}
