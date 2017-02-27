package hashcode.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hashcode.data.Cache;
import hashcode.data.Connection;
import hashcode.data.Endpoint;
import hashcode.data.MetaVideoRequest;
import hashcode.data.Video;
import hashcode.data.VideoRequest;

public class Reader {

	private List<Video> videos;
	private List<Endpoint> endpoints;
	private List<Cache> caches;
	
	public Reader(String file) throws IOException{
		load(file);
	}
	
	public void load(String file) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		String line = reader.readLine();
		String [] parts = line.split(" ");
		int videosCount = Integer.parseInt(parts[0]);
		int endpointsCount = Integer.parseInt(parts[1]);
		int requests = Integer.parseInt(parts[2]);
		int cachesCount = Integer.parseInt(parts[3]);
		int cachesSize = Integer.parseInt(parts[4]);
		
		caches = new ArrayList<>(cachesCount);
		for(int i = 0; i < cachesCount; i++){
			Cache cache = new Cache(i, cachesSize);
			caches.add(cache);
		}
		
		videos = new ArrayList<>(videosCount);
		String videoLine = reader.readLine();
		parts = videoLine.split(" ");
		
		if(parts.length != videosCount){
			throw new RuntimeException("Wrong video count, "+parts.length +" "+ videosCount);
		}
		
		for(int i = 0; i < videosCount; i++){
			Video video = new Video(i, Integer.parseInt(parts[i]));
			
			if(video.getSize() > cachesSize){
				System.out.println("PANIC");
			}
			videos.add(video);
		}
		
		endpoints = new ArrayList<>(endpointsCount);
		for(int i = 0; i < endpointsCount; i++){
			String [] endpointLine = reader.readLine().split(" ");
			
			int cachesCount2 = Integer.parseInt(endpointLine[1]);
			Endpoint endpoint = new Endpoint(Integer.parseInt(endpointLine[0]));
			endpoints.add(endpoint);
			
			List<Connection> tempConnections = new ArrayList<>(cachesCount2);
			
			for(int loop = 0; loop < cachesCount2; loop++){
				String [] endpointCacheLine = reader.readLine().split(" ");
				
				Cache cache = caches.get(Integer.parseInt(endpointCacheLine[0]));
				Connection connection = new Connection(Integer.parseInt(endpointCacheLine[1]), cache, endpoint);
				
				if(connection.getLatency() >= endpoint.getDatacenterLatency()){
					System.out.println("PANIC");
				}
				
				tempConnections.add(connection);
			}
			
			for(Connection connection : tempConnections){
				connection.getCache().addEndpoint(connection);
				endpoint.addCache(connection);
			}
		}
		
		for(int i = 0; i < requests; i++){
			String [] requestLine = reader.readLine().split(" ");
			
			Video video = videos.get(Integer.parseInt(requestLine[0]));
			Endpoint endpoint = endpoints.get(Integer.parseInt(requestLine[1]));
			VideoRequest request = new VideoRequest(Integer.parseInt(requestLine[2]),
					video,
					endpoint);
			
			endpoint.addRequest(request);
			
			
			for(Connection cache: endpoint.getCaches()){
				cache.getCache().addRequest(new MetaVideoRequest(request,cache.getLatency()));
			}
			
		}
		
		reader.close();
	}
	
	public List<Video> getVideos(){
		return videos;
	}
	
	public List<Endpoint> getEndpoints(){
		return endpoints;
	}

	public List<Cache> getCaches(){
		return caches;
	}
}
