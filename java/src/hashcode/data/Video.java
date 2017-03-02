package hashcode.data;

public class Video {

	private final int id;
	private final int size;
	private int totalCaches = 0;
	
	public Video(int id, int size){
		this.id = id;
		this.size = size;
	}
	
	public int getId(){
		return id;
	}
	
	public int getSize(){
		return size;
	}
	
	public void addCache(){
		totalCaches++;
	}
	
	public void removeCache(){
		totalCaches--;
	}
	
	public int possibleCaches(){
		return totalCaches;
	}
	
	public String toString(){
		return "ID : "+id+" Size : "+size;
	}
}
