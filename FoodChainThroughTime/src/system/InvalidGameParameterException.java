package system; 

/**
 * Ensures that the game configuration remains within valid logical names.
 */
public class InvalidGameParameterException extends Exception {
   
	public InvalidGameParameterException(String message) {
       
		super(message);
    
	}
}