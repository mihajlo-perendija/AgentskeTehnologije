package beans;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;

@Singleton
@LocalBean
public class IdGenerator {

	private static long id = 0;
	
	public IdGenerator() {
		
	}
	
	static public long getNextId() {
		return ++id;
	}
}
