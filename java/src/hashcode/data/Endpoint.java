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
		if(requests.containsKey(request.getVideo())){
			/*System.out.println("PANIC");
			System.out.println(request.getVideo().getId()+" "+request.getTotal());
			System.out.println(requests.get(request.getVideo()).getVideo().getId()+" "+requests.get(request.getVideo()).getTotal());
			*/
			
			requests.get(request.getVideo()).addRequests(request.getTotal());
		}else{
			requests.put(request.getVideo(), request);
		}
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

	public void removeVideo(Video video, int latency) {
		VideoRequest request = requests.get(video);
		if(request != null){

			//requests.remove(video);
			
			if(latency < request.getBestLatency()){
				int oldScore = MetaVideoRequest.getTimeGain(request.getTotal(),
						getDatacenterLatency(),
						request.getBestLatency());
				
				for(Connection connection : caches){
					int newScore = MetaVideoRequest.getTimeGain(request.getTotal(),
							getDatacenterLatency(),
							latency);
					
					if(newScore > oldScore){
						connection.getCache().removeRequest(video, this, newScore - oldScore);
					}
				}
				
				request.setBestLatency(latency);
			}
			
		}
	}

	public int getTotalRequests() {
		int total = 0;
		for(Video video : requests.keySet()){
			total += requests.get(video).getTotal();
		}
		
		return total;
	}

	public long getTimeSaved() {
		
		long saved = 0;
		
		for(Video video : requests.keySet()){
			
			int bestLatency = getDatacenterLatency();
						
			for(Connection connection : caches){
				if(connection.getLatency() < bestLatency && connection.getCache().isVideoCached(video)){
					
					bestLatency = connection.getLatency();
				}
			}
			
			if(bestLatency != getDatacenterLatency()){
				int savedTime = MetaVideoRequest.getTimeGain(requests.get(video).getTotal(),
						getDatacenterLatency(),
						bestLatency);
				//System.out.println("Saved "+savedTime+" DC : "+getDatacenterLatency()+" "+bestLatency+" "+requests.get(video).getTotal());
				saved += savedTime;
			}
		}
		
		return saved;
	}
	
	
}
