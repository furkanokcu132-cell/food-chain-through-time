package characters;
/**
 * A helper class designed to encapsulate the X and Y coordinates into a single object.
 */
public class Position {
    
	private int x;
    private int y;

    /**
     * Constructor
     * @param x
     * @param y
     */
    public Position(int x, int y) {
       
    	this.x = x;
        this.y = y;
   
    }

    /**
     * Getters
     * @return
     */
    public int getX() { 
    	
    	return x;
    	
    }

    public int getY() { 
    	
    	return y;
    	
    }
     
    /**
     * Overrides the default equality check to compare two positions based
on their X and Y values rather than their memory references.
     */
    public boolean equals(Object obj) {
        
    	if (this == obj) {
    		
    		return true;
    	}
        
    	if (obj == null || getClass() != obj.getClass()) {
    		
    		return false;
    	
    	}
       
    	Position position = (Position) obj;
        
    	return x == position.x && y == position.y;
   
    }
}