package hashcode.solver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hashcode.data.Cache;
import hashcode.data.MetaVideoRequest;
import hashcode.data.Video;
import hashcode.data.VideoRequest;

public class ThirdSolver implements Solver{

	
	@Override
	public List<Cache> solve(List<Cache> caches) {

		List<Cache> cachesTemp = new ArrayList<>(caches);
		
		//Presolve some stuff
		for(Cache cache : cachesTemp){
		    if(cache.getEndpoints().size() == 1){
		        Video video = null;
		        while((video = cache.getBestVideo()) != null){
		            cache.cacheVideo(video);
		        }
		    }
		}
		
		do{
			
			cachesTemp.sort(new Comparator<Cache>() {

				@Override
				public int compare(Cache o1, Cache o2) {
					
					int videos1 = o1.getMaxCacheNeed(true);
					int videos2 = o2.getMaxCacheNeed(true);
					
					if(videos1 == videos2){
						int size1 = o1.getRequestsSize(true);
						int size2 = o2.getRequestsSize(true);
											
						return size2 - size1;
					}
					
					return videos2 - videos1;
				}
			});
			
			Cache cache = cachesTemp.get(0);
			
			solveCache(cache);
			
			cachesTemp.remove(cache);
		}while(cachesTemp.size() > 0);
		
		return caches;
	}

	private void solveCache(Cache cache) {
		cache.clearCache();
		
		Map<Video, Integer> requestsOverview = new HashMap<>();
		Map<Video, List<VideoRequest>> requestCache = new HashMap<>();
		
		List<VideoRequest> requests = new ArrayList<>();
		for(MetaVideoRequest request : cache.getRequestsFromEndpoints(true)){
			Video video = request.getVideo();
			if(!requestsOverview.containsKey(video)){
				requestsOverview.put(video, 0);
			}
			
			requestsOverview.put(video, requestsOverview.get(video) + request.getTimeGain());
			
			if(!requestCache.containsKey(video)){
				requestCache.put(video, new ArrayList<>());
			}
			
			requestCache.get(video).add(request.getRequest());
			requests.add(request.getRequest());
		}
		
		requests.sort(new Comparator<VideoRequest>() {

			@Override
			public int compare(VideoRequest o1, VideoRequest o2) {
				int gain1 = requestsOverview.get(o1.getVideo());
				int gain2 = requestsOverview.get(o1.getVideo());
				
				if(gain1 != gain2){
					return o1.getVideo().getSize() - o2.getVideo().getSize();
				}
				
				return gain2 - gain1;
			}
		});
		
		for(int i = 0; i < requests.size(); i++){
			VideoRequest request = requests.get(i);
			
			if(request.getVideo().getSize() <= cache.getAvailableSpace()){
				cache.cacheVideo(request.getVideo());
				
				//Remove request from all connected endpoints
				for(VideoRequest requestToRemove : requestCache.get(request.getVideo())){
					requestToRemove.getEnpoint().removeRequest(requestToRemove);
				}
			}
		}
	}
}
