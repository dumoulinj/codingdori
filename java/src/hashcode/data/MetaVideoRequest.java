package hashcode.data;

public class MetaVideoRequest {

	private final int latency;
	private final VideoRequest request;
	private final boolean badQuality;
	
	public MetaVideoRequest(VideoRequest request, int latency, boolean badQuality){
		this.latency = latency;
		this.request = request;
		this.badQuality = badQuality;
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
		int score = getTimeGain(request.getTotal(),request.getEnpoint().getDatacenterLatency(), latency,request.getEnpoint().getCaches().size());
		if(badQuality){
			score *= 0.4;
		}
		return score;
	}
	
	public static int getTimeGain(int requests, int dataCenterLatency, int cacheLatency, int modifier){
		return (int)(requests / (1 + Math.log(modifier)))* (dataCenterLatency - cacheLatency);
	}
	
}
