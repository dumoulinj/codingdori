package hashcode.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Cache {

	private final int size;
	private final int id;
	private int usedSpace = 0;
	
	private List<Connection> endpoints = new ArrayList<>();
	private List<MetaVideoRequest> requests = new ArrayList<>();
	
	private Set<Video> cachedVideos = new HashSet();
	
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
	}
	
	public List<MetaVideoRequest> getRequests(){
		return requests;
	}
	
	private List<MetaVideoRequest> cachedRequests = null;
	
	public List<MetaVideoRequest> getRequestsFromEndpoints(boolean mustFit){
		if(cachedRequests != null){
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
		
		cachedRequests = requests;
		
		return requests;
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
