package hashcode.data;

public class VideoRequest {

	private final int total;
	private final Video video;
	private final Endpoint endpoint;
	
	public VideoRequest(int total, Video video, Endpoint endpoint){
		this.total = total;
		this.video = video;
		this.endpoint = endpoint;
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
}
