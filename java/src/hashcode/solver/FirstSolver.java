package hashcode.solver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import hashcode.data.Cache;
import hashcode.data.MetaVideoRequest;
import hashcode.data.Video;
import hashcode.data.VideoRequest;

public class FirstSolver implements Solver{

	@Override
	public List<Cache> solve(List<Cache> caches) {
		
		for(Cache cache : caches){
			cache.clearCache();
			
			List<MetaVideoRequest> requests = new ArrayList<>(cache.getRequests());
			
			requests.sort(new Comparator<MetaVideoRequest>() {

				@Override
				public int compare(MetaVideoRequest o1, MetaVideoRequest o2) {
					return o1.getRequest().getVideo().getSize() - o2.getRequest().getVideo().getSize();
				}
			});
			
			for(int i = 0; i < requests.size(); i++){
				VideoRequest request = requests.get(i).getRequest();
				
				if(request.getVideo().getSize() <= cache.getAvailableSpace()){
					cache.cacheVideo(request.getVideo());
				}
			}
		}
		
		return caches;
	}

}
