package hashcode.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Endpoint {

	private final int latencyDatacenter;
	
	private List<Connection> caches = new ArrayList<>();
	
	private Map<Video, VideoRequest> requests = new HashMap<>();
	
	public Endpoint(final int latencyDatacenter){
		this.latencyDatacenter = latencyDatacenter;
	}
	
	public List<Connection> getCaches(){
		return caches;
	}
	
	public void addCache(Connection connection){
		caches.add(connection);
	}
	
	public int getDatacenterLatency(){
		return latencyDatacenter;
	}
	
	public void addRequest(VideoRequest request){
		requests.put(request.getVideo(), request);
	}
	
	public List<VideoRequest> getRequests(){
		return new ArrayList<>(requests.values());
	}

	public void removeRequest(VideoRequest requestToRemove) {
		requests.remove(requestToRemove);
		
		for(Connection connection : caches){
			connection.getCache().invalidCachedStuff();
		}
	}

	public void removeVideo(Video video) {
		VideoRequest request = requests.get(video);
		if(request != null){

			requests.remove(video);
			
			for(Connection connection : caches){
				connection.getCache().removeRequest(video, this, MetaVideoRequest.getTimeGain(request.getTotal(),
						getDatacenterLatency(),
						connection.getLatency()));
			}
		}
	}
}
