package hashcode.data;

public class MetaVideoRequest {

	private final int latency;
	private final VideoRequest request;
	
	public MetaVideoRequest(VideoRequest request, int latency){
		this.latency = latency;
		this.request = request;
	}
	
	public int getLatency(){
		return latency;
	}
	
	public VideoRequest getRequest(){
		return request;
	}

	public Video getVideo() {
		return request.getVideo();
	}
	
	public int getTimeGain(){
		return request.getTotal() * (request.getEnpoint().getDatacenterLatency() - latency);
	} 
	
}
