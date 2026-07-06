package characters;
/**
 * A passive entity class representing resources on the map.
 */
public class Food {
	 
	private String name;
    private Position position;

    /**
     * Constructor.
     * @param name
     * @param x
     * @param y
     */
    public Food(String name, int x, int y) { 
       
    	this.name = name;
        this.position = new Position(x, y);
    
    }

    /**
     * Getter and Setters
     * @return
     */
    public Position getPosition() { 
    	
    	return position; 
    	
    }
    
    public void setPosition(Position position) { 
    	
    	this.position = position;
    	
    }
   
    public String getName() {
    	
    	return name; 
    	
    }
}