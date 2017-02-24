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
		
		for(Cache cache: caches){
			if(cache.getCachedVideos().size() > 0){
				writer.write(""+cache.getID());
				
				for(Video video : cache.getCachedVideos()){
					writer.write(" "+video.getId());
				}

				writer.write("\n");
			}
		}
		
		writer.close();
	}
	
}
