package hashcode.data;

public class VideoRequest {

	private int total;
	private final Video video;
	private final Endpoint endpoint;
	private int bestLatency;
	
	public VideoRequest(int total, Video video, Endpoint endpoint, int latency){
		this.total = total;
		this.video = video;
		this.endpoint = endpoint;
		this.bestLatency = latency;
	}
	
	public int getTotal(){
		return total;
	}
	
	public Video getVideo(){
		return video;
	}
	
	public Endpoint getEnpoint(){
		return endpoint;
	}

	public int getBestLatency() {
		return bestLatency;
	}

	public void setBestLatency(int bestLatency) {
		this.bestLatency = bestLatency;
	}

	public void addRequests(int total) {
		total += total;
	}
	
	
}
