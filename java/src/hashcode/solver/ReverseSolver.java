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


public class ReverseSolver implements Solver{

	private static class Pair{
		int score;
		Video video;
		List<VideoRequest> endpoints;
	}
	
	@Override
	public List<Cache> solve(List<Cache> caches) {
		
		List<Cache> temp = new ArrayList<>(caches);
		
		do{
			int best = 0;
			Video video = null;
			Cache bestCache = null;
			List<VideoRequest> endpoints = null;
			
			for(Cache cache : temp){
				Pair pair = getBestVideo(cache);
				
				if(pair != null){
					
					if(pair.score > best){
						video = pair.video;
						best = pair.score;
						bestCache = cache; 
						endpoints = pair.endpoints;
					}else if(pair.score == best && video != null &&
							video.getSize() > pair.video.getSize()){
						video = pair.video;
						best = pair.score;
						bestCache = cache; 
						endpoints = pair.endpoints;
					}
				}
			}
			
			//System.out.println("Remove video for "+best+" in cache "+bestCache.getID()+" video: "+video.getId());
			bestCache.cacheVideo(video);
			
			for(VideoRequest requestToRemove : endpoints){
				requestToRemove.getEnpoint().removeRequest(requestToRemove);
			}
			
			//Remove obsolete caches
			for(int i = temp.size() - 1; i >= 0; i--){
				if(temp.get(i).getRequestsFromEndpoints(true).size() <= 0){
					temp.remove(i);
				}
			}
						
		}while(temp.size() > 0);
		
		// TODO Auto-generated method stub
		return caches;
	}
	
	private Pair getBestVideo(Cache cache){
		Map<Integer, Integer> requestsOverview = new HashMap<>();
		Map<Video, List<VideoRequest>> requestCache = new HashMap<>();
		
		List<VideoRequest> requests = new ArrayList<>();
		
		List<MetaVideoRequest> asdf = cache.getRequestsFromEndpoints(true);
		
		if(asdf.size() == 0){
			return null;
		}
		
		for(MetaVideoRequest request : asdf){
			Video video = request.getVideo();
			
			if(!requestsOverview.containsKey(video.getId())){
				requestsOverview.put(video.getId(), 0);
			}
			
			requestsOverview.put(video.getId(), requestsOverview.get(video.getId()) + request.getTimeGain());
			
			if(!requestCache.containsKey(video)){
				requestCache.put(video, new ArrayList<>());
			}
			
			requestCache.get(video).add(request.getRequest());
			requests.add(request.getRequest());
		}
		
		requests.sort(new Comparator<VideoRequest>() {

			@Override
			public int compare(VideoRequest o1, VideoRequest o2) {
				int gain1 = requestsOverview.get(o1.getVideo().getId());
				int gain2 = requestsOverview.get(o1.getVideo().getId());
				
				if(gain1 != gain2){
					return o1.getVideo().getSize() - o2.getVideo().getSize();
				}
				
				return gain2 - gain1;
			}
		});
				
		Pair pair = new Pair();
		
		pair.score = requestsOverview.get(requests.get(0).getVideo().getId());
		pair.video = requests.get(0).getVideo();
		pair.endpoints = requestCache.get(pair.video);
		
		return pair;
	}

}
