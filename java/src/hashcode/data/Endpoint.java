package hashcode.data;

import java.util.ArrayList;
import java.util.List;

public class Endpoint {

	private final int latencyDatacenter;
	
	private List<Connection> caches = new ArrayList<>();
	private List<VideoRequest> requests = new ArrayList<>();
	
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
		requests.add(request);
	}
	
	public List<VideoRequest> getRequests(){
		return requests;
	}

	public void removeRequest(VideoRequest requestToRemove) {
		requests.remove(requestToRemove);
		
		for(Connection connection : caches){
			connection.getCache().invalidCachedStuff();
		}
	}
}
