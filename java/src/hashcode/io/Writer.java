package hashcode.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import hashcode.data.Cache;
import hashcode.data.Video;

public class Writer {

	public void save(String file, List<Cache> caches) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		
		int total = 0;
		for(Cache cache: caches){
			if(!cache.getCachedVideos().isEmpty()){
				total++;
			}
		}
		
		writer.write(total+"\n");
		
		int totalVideoSize = 0;
		int totalCacheSIze = 0;
		for(Cache cache: caches){
			totalCacheSIze += cache.getSize();
			
			if(cache.getCachedVideos().size() > 0){
				writer.write(""+cache.getID());
				
				int videoSize = 0;
				for(Video video : cache.getCachedVideos()){
					writer.write(" "+video.getId());
					videoSize += video.getSize();
				}
				
				if(cache.getSize() < videoSize){
					throw new RuntimeException(cache.getSize() +" "+videoSize);
				}
				
				totalVideoSize += videoSize;
				
				//System.out.println(cache.getSize()+" "+totalVideoSize);

				writer.write("\n");
			}
		}
		
		//System.out.println("Cached "+totalVideoSize+" video mem, max : "+totalCacheSIze);
		
		writer.close();
	}
	
}
