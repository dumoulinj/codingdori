package hashcode.data;

public class Connection {

	private final int latency;
	private final Cache cache;
	private final Endpoint endpoint;
	
	public Connection(int latency, Cache cache, Endpoint endpoint){
		this.latency = latency;
		this.cache = cache;
		this.endpoint = endpoint;
	}
	
	public int getLatency(){
		return latency;
	}
	
	public Cache getCache(){
		return cache;
	}
	
	public Endpoint getEndpoint(){
		return endpoint;
	}
}
