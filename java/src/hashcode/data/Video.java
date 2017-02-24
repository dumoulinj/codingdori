package hashcode.data;

public class Video {

	private final int id;
	private final int size;
	
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
}
