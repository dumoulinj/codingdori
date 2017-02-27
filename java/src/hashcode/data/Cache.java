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
	
	private double BASE_SCORE = 0.8;
	private double SIZE_SCORE = 0.2;
	
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
		}
		
		videoScores.put(video, videoScores.get(video) + request.getTimeGain() );
		
		bestVideo = null;
	}
	
	private Video bestVideo = null;
	
	public Video getBestVideo(){
		if(bestVideo != null){
//			System.out.println("Use cached best");
			return bestVideo;
		}
//		System.out.println("Recalc best");
		
		int best = 0;
		Video video = null;
		
		/*long totalScore = 0;
		for(int score : videoScores.values()){
			totalScore += score;
		}*/
		
		for(final Video temp : videoScores.keySet()){
			if(temp.getSize() <= getAvailableSpace()){
				final int score = getVideoScore(temp);//(int)(10 * videoScores.get(temp) * (double)temp.getSize() / getAvailableSpace());
				
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
		
		return video;
	}
	
	public int getVideoScore(Video video){
		int score = videoScores.get(video);
		return (int)( BASE_SCORE*score + SIZE_SCORE * score / video.getSize());//(int)(10 * videoScores.get(video) * (double)video.getSize() / getAvailableSpace());
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
			}else{
				videoScores.put(video, newScore);
			}
			
			if(video == bestVideo){
				bestVideo = null;
			}
		}
	}
	
	private List<MetaVideoRequest> cachedRequests = null;
	
	public List<MetaVideoRequest> getRequestsFromEndpoints(boolean mustFit){
		/*if(cachedRequests != null){
			return cachedRequests;
		}
		
		List<MetaVideoRequest> requests = new ArrayList<>();
		for(Connection connection : endpoints){
			for(VideoRequest request : connection.getEndpoint().getRequests()){
				if(!mustFit || request.getVideo().getSize() <= getAvailableSpace()){
					requests.add(new MetaVideoRequest(request, connection.getLatency()));
				}
			}
		}
		
		cachedRequests = requests;*/
		
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
		
		if(video == bestVideo){
			bestVideo = null;
		}
		
		for(Connection endpoint : endpoints){
			endpoint.getEndpoint().removeVideo(video);
		}
		
		Set<Video> keys = new HashSet<>(videoScores.keySet());
		
		for(Video key : keys){
			if(key.getSize() > getAvailableSpace()){
				videoScores.remove(key);
			}
		}
		
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
}
