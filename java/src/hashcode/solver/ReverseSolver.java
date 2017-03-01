package hashcode.solver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;

import hashcode.data.Cache;
import hashcode.data.MetaVideoRequest;
import hashcode.data.Video;
import hashcode.data.VideoRequest;


public class ReverseSolver implements Solver{

	private static class Pair{
		int score;
		Video video;
	}
	
	@Override
	public List<Cache> solve(List<Cache> caches) throws InterruptedException {
		
		List<Cache> temp = new ArrayList<>(caches);
		
		ExecutorService service = Executors.newFixedThreadPool(4);
		final List<Pair> pairs = new ArrayList<>(temp.size());

		for(int i = 0; i < temp.size(); i++){
			pairs.add(null);
		}
		
		int last = -1;
		do{
			int best = 0;
			Video video = null;
			Cache bestCache = null;
			
			final Semaphore sem = new Semaphore(0);
			
			for(int i = 0; i < temp.size(); i++){
				
				final int index = i;
				service.execute(new Runnable() {
					
					@Override
					public void run() {
						Pair pair = getBestVideo(temp.get(index));
						
						pairs.set(index, pair);
						sem.release();
					}
				});
				
			}
			
			for(int i = 0; i < temp.size(); i++){
				sem.acquire(1);
			}
			
			for(int i = 0; i < temp.size(); i++){
				Cache cache = temp.get(i);
				Pair pair = pairs.get(i);
				
				if(pair != null){
					if(pair.score > best){
						video = pair.video;
						best = pair.score;
						bestCache = cache; 
					}else if(pair.score == best){
						if(cache.getAvailableSpace() == pair.video.getSize()){
							video = pair.video;
							best = pair.score;
							bestCache = cache;
						}if(video != null && video.getSize() > pair.video.getSize()){
							video = pair.video;
							best = pair.score;
							bestCache = cache; 
						}else if(pair.score == best && video != null &&
								video.getSize() == pair.video.getSize() &&
								bestCache.getAvailableSpace() < cache.getAvailableSpace()
								){
							video = pair.video;
							best = pair.score;
							bestCache = cache; 
						}
					}
				}
			}
			
			//System.out.println("Remove video for "+best+" in cache "+bestCache.getID()+" video: "+video.getId());
			if(bestCache == null){
				//Should not happen
				break;
			}
			bestCache.cacheVideo(video);
			
			/*for(VideoRequest requestToRemove : endpoints){
				requestToRemove.getEnpoint().removeRequest(requestToRemove);
			}*/
			
			//Remove obsolete caches
			for(int i = temp.size() - 1; i >= 0; i--){
				if(!temp.get(i).hasViableVideo()){
					temp.remove(i);
				}
			}
			
			if(last != temp.size()){
				//System.out.println(temp.size()+" "+ best);
				last = temp.size();
			}
			
			
			
		}while(temp.size() > 0);
		
		service.shutdown();
		
		// TODO Auto-generated method stub
		return caches;
	}
	
	private Pair getBestVideo(Cache cache){
		Video video = cache.getBestVideo();
		
		if(video== null){
			return null;
		}
		
		Pair pair = new Pair();
		
		pair.score = cache.getVideoScore(video);
		pair.video = video;
		
		return pair;
	}

}
