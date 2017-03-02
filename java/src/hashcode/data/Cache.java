package hashcode.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Cache {

	private final int size;
	private final int id;
	private int usedSpace = 0;
	
	private List<Connection> endpoints = new ArrayList<>();
	private List<MetaVideoRequest> requests = new ArrayList<>();
	
	private Set<Video> cachedVideos = new HashSet();
	
	private Map<Video, Integer> videoScores = new HashMap<>();
	private int remainingVideoSize = 0;
	
	public static double BASE_SCORE = 0;
	public static double SIZE_SCORE = 1;
	public static double SIZE_FACTOR = 1;
	
	public Cache(int id, int size){
		this.size = size;
		this.id = id;
	}
	
	public int getID(){
		return id;
	}
	
	public int getSize(){
		return size;
	}
	
	public void addEndpoint(Connection connection){
		endpoints.add(connection);
	}
	
	public List<Connection> getEndpoints(){
		return endpoints;
	}
	
	public void addRequest(MetaVideoRequest request){
		requests.add(request);
		
		Video video = request.getVideo();
		if(!videoScores.containsKey(video)){
			videoScores.put(video, 0);
			remainingVideoSize += video.getSize();
			video.addCache();
		}
		
		videoScores.put(video, videoScores.get(video) + request.getTimeGain() );
		remainingCache = -1;
		
		totalSize = -1;
		
		bestVideo = null;
	}
	
	private Video bestVideo = null;
	
	public Video getBestVideo(){
		if(bestVideo != null){
			return bestVideo;
		}
		
		if(videoScores.size() > 2 && videoScores.size() < 20 && getAvailableSpace() < getTotalRequestSize()){
			bestVideo = getBestVideoBackpack();
		}else{
			double best = 0;
			Video video = null;
			
			final int availableSpace = getAvailableSpace();
			
			for(final Video temp : videoScores.keySet()){
				if(temp.getSize() <= availableSpace){
					final double score = getVideoScore(temp);//(int)(10 * videoScores.get(temp) * (double)temp.getSize() / getAvailableSpace());
					
					if(video == null){
						video = temp;
						best = score;
					}else if(score >= best){
						if(score > best || video.getSize() > temp.getSize()){
							video = temp;
							best = score;
						}
					}
				}
			}
			
			bestVideo = video;
		}
		
		return bestVideo;
	}
	
	private Video getBestVideoBackpack(){
		Set<Video> combination = getBestCombiation();
		
		//System.out.println(combination.size() +" "+videoScores.size());
		
		Video best = null;
		for(Video video : combination){
			if(best == null || getVideoScore(video) > getVideoScore(best)){
				best = video;
			}
		}
		
		return best;
	}
	
	public void solveCache(){
		Set<Video> best = getBestCombiation();
		
		for(Video video : best){
			cacheVideo(video);
		}
	}
	
	private Set<Video> getBestCombiation(){
		int remainingVideos = videoScores.keySet().size();
		
		List<Video> videos = new ArrayList<>(videoScores.keySet());
		
		long bestSolution = 0;
		int bestScore = 0;
		
		for(long i = 1; i < Math.pow(2, remainingVideos); i++){
			int size = 0;
			int score = 0;
			
			for(int v = 0; v < videos.size(); v++){
				if ((i & (1L << v)) != 0)
				{
					int tempSize = videos.get(v).getSize();
					
					if(size + tempSize < getAvailableSpace()){
					   size += tempSize;
					   score += videoScores.get(videos.get(v));
					}else{

						size = -1;
						break;
					}
				}
			}
			
			if(score > bestScore){
				bestSolution = i;
				bestScore = score;
			}
		}
		
		Set<Video> solution = new HashSet<>();
		
		for(int v = 0; v < videos.size(); v++){
			if ((bestSolution & (1L << v)) != 0)
			{
				solution.add(videos.get(v));
			}
		}
		
		return solution;
	}
	
	public double getVideoScore(Video video){
		double score = videoScores.get(video);
		return (( BASE_SCORE*(score / (video.getSize() * (1 + Math.log(video.possibleCaches()))) ) + (SIZE_SCORE * score) / (video.getSize())) );//(int)(10 * videoScores.get(video) * (double)video.getSize() / getAvailableSpace());
	}
	
	public double getVideoScoreRaw(Video video){
		return videoScores.get(video);
	}
	
	public List<MetaVideoRequest> getRequests(){
		return requests;
	}
	
	public void removeRequest(Video video, Endpoint endpoint, int scoreToRemove){
		/*for(int i = 0; i < requests.size(); i++){
			MetaVideoRequest temp = requests.get(i);
			if(temp.getRequest().getVideo() == video &&
					temp.getRequest().getEnpoint() == endpoint){
				requests.remove(i);
				break;
			}
		}*/
		
		if(videoScores.containsKey(video)){
			int score = videoScores.get(video);
			
			int newScore = score - scoreToRemove;
			
			if(newScore <= 0){
				videoScores.remove(video);
				remainingVideoSize -= video.getSize();
				video.removeCache();
				totalSize = -1;
			}else{
				videoScores.put(video, newScore);
			}
			
			if(video == bestVideo){
				bestVideo = null;
			}
		}
		remainingCache = -1;
	}
	
	
	
	private List<MetaVideoRequest> cachedRequests = null;
	
	public List<MetaVideoRequest> getRequestsFromEndpoints(boolean mustFit){
		
		if(!mustFit){
			return requests;
		}
		
		List<MetaVideoRequest> requestsSlim = new ArrayList<>();
		
		for(MetaVideoRequest request : requests){
			if(request.getVideo().getSize() <= getAvailableSpace()){
				requestsSlim.add(request);
			}
		}
		
		
		return requestsSlim;
	}
	
	public boolean hasViableVideo(){
		for(MetaVideoRequest request : requests){
			if(request.getVideo().getSize() <= getAvailableSpace()){
				return true;
			}
		}
		
		return false;
	}
	
	public void invalidCachedStuff(){
		cachedRequests = null;
	}
	
	public int getUniqueVideosFromEndpoints(boolean mustFit){
		Set<Video> videos = new HashSet<>();
		
		for(MetaVideoRequest request : getRequestsFromEndpoints(mustFit)){
			videos.add(request.getVideo());
		}
		
		return videos.size();
	}
	
	public int getMaxCacheNeed(boolean mustFit){
		Set<Video> videos = new HashSet<>();
		
		for(MetaVideoRequest request : getRequestsFromEndpoints(mustFit)){
			videos.add(request.getVideo());
		}
		
		int size = 0;
		
		for(Video video : videos){
			size += video.getSize();
		}
		return size;
	}
	
	public int getMaxScore(boolean mustFit){
		int total = 0;
		
		for(MetaVideoRequest request : getRequestsFromEndpoints(mustFit)){
			total += request.getTimeGain();
		}
		
		return total;
	}
	
	public int getRequestsSize(boolean mustFit){
		int total = 0;
		
		for(MetaVideoRequest request : getRequestsFromEndpoints(mustFit)){
			total += request.getVideo().getSize();
		}
		
		return total;
	}
	
	public Set<Video> getCachedVideos(){
		return cachedVideos;
	}
	
	public void cacheVideo(Video video){
		cachedVideos.add(video);
		
		usedSpace += video.getSize();
		
		videoScores.remove(video);
		remainingVideoSize -= video.getSize();
		video.removeCache();
		
		if(video == bestVideo){
			bestVideo = null;
		}
		
		for(Connection connection : endpoints){
			if(!connection.isBadQuality()){
				connection.getEndpoint().removeVideo(video, connection.getLatency());
			}
		}
		
		Set<Video> keys = new HashSet<>(videoScores.keySet());
		
		for(Video key : keys){
			if(key.getSize() > getAvailableSpace()){
				videoScores.remove(key);
				remainingVideoSize -= key.getSize();
				key.removeCache();
			}
		}
		
		totalSize = -1;
	}
	
	public int getCacheSize(){		
		return usedSpace;
	}
	
	public int getAvailableSpace(){
		return getSize() - getCacheSize();
	}
	
	public void clearCache(){
		usedSpace = 0;
		cachedVideos.clear();
	}

	public int getLatency(VideoRequest request) {
		
		for(Connection connection : endpoints){
			
			for(VideoRequest req : connection.getEndpoint().getRequests()){
				if(req == request){
					return connection.getLatency();
				}
			}
		}
		return Integer.MAX_VALUE;
	}
	
	int totalSize = -1;
	
	public int getTotalRequestSize(){
		return remainingVideoSize;
	}
	
	public Set<Video> getRemainingVideos(){
		return videoScores.keySet();
	}
	
	int remainingCache = -1;
	
	public int getRemainingScore(){
		if(remainingCache != -1){
			return remainingCache;
		}
		
	    int total = 0;
	    for(Video video : videoScores.keySet()){
	    	if(video.getSize() <= getAvailableSpace()){

		        total += videoScores.get(video);
	    	}
	    }
	    
	    remainingCache = total;
	    
	    return total;
	}

	public boolean isVideoCached(Video video) {
		return cachedVideos.contains(video);
	}
	
	public String toString(){
		return "ID : "+getID();
	}
}
