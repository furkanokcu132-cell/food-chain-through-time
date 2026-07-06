package characters;

/**
 * Represents the intermediate hunters in the food chain. In this implementation,
the Predator class is primarily designed to be controlled by the player (user).
 */
public class Predator extends Animal {
	
	/**
	 * Constructor.
	 * @param name
	 * @param x
	 * @param y
	 */
    public Predator(String name, int x, int y) {
        
    	super(name, x, y);
       
    }
 
    /**
     * This method is intentionally left empty or minimal because the Predator's
actions are driven by user input events rather than an automated AI loop in this specific
context.
     */
    public void act(Era era) {}
    
    /**
     * Handles manual movement commands.
     * @param dx
     * @param dy
     * @param gridSize
     */
    public void moveByPlayer(int dx, int dy, int gridSize) {
       
    	int newX = this.position.getX() + dx;
        int newY = this.position.getY() + dy;
       
        if (newX >= 0 && newX < gridSize && newY >= 0 && newY < gridSize) {
           
        	this.position = new Position(newX, newY);
       
        }
    }
}
