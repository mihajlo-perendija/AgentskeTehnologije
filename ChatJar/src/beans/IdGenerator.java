package beans;

public class IdGenerator {

	private static long id = 0;
	
	public IdGenerator() {
		
	}
	
	static public long getNextId() {
		return ++id;
	}
}
