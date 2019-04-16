import java.util.HashMap;
import java.util.Map;

public class movieCart {
	private static Map<String, Integer> movieCart;
	
	movieCart() {
		System.out.println("Why...");
		movieCart = new HashMap<String, Integer>();
	}
	
	public void addMovie(String movieId) {
    	if(movieCart.isEmpty()) {
    		System.out.println("Empty");
    	}
    	//Check if we have the record, add 1 to count, else set it to 1
    	int count = 0;
    	if(movieCart.containsKey(movieId)) {
    		count = movieCart.get(movieId) + 1;
    		movieCart.put(movieId, count + 1);
    	} else {
        	movieCart.put(movieId, 1);
    	}
    }
	
	public static Map<String, Integer> getMovies() { return movieCart; }
}
