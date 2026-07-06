package characters;

/**
 * The ApexPredator represents the top-tier entity in the food chain.
 */
public class ApexPredator extends Animal {

	/**
	 * Constructor that initialize everything.
	 * @param name
	 * @param x
	 * @param y
	 */
    public ApexPredator(String name, int x, int y) {
        
    	super(name, x, y);
       
    }    
     
    /**
     * Overrides the standard behavior to broaden the targeting logic. It scans for
the nearest valid target, which can be either a Prey or a standard Predator. This reflects its
dominance in the hierarchy. This method also facilitates the specific movement patterns
unique to the Apex Predator, often allowing for more flexible movement 
     */
    public void act(Era era) {
        
    	if (!isAlive) {
    		
    		return;
    	
    	}
        
    	String eraName = era.getName();
        
        Animal closestPrey = findClosestAnimal(era, Prey.class);
        Animal closestPredator = findClosestAnimal(era, Predator.class);        
        Animal target = null;
        
        double distPrey = (closestPrey != null) ? getDistance(this.position, closestPrey.getPosition()) : Double.MAX_VALUE;
        double distPredator = (closestPredator != null) ? getDistance(this.position, closestPredator.getPosition()) : Double.MAX_VALUE;

        if (distPrey < distPredator) {
        	
        	target = closestPrey;
       
        }
       
        else {
        	
        	target = closestPredator;
        
        }

        if (target == null) {
            
        	this.position = getRandomValidNeighbor(era.getGridSize());
            return;
        
        }

        if (specialAbilityCD == 0) {
           
        	int dx = target.getPosition().getX() - this.position.getX();
            int dy = target.getPosition().getY() - this.position.getY();
           
            int absDx = Math.abs(dx);
            int absDy = Math.abs(dy);
            
            Position jumpPosition = null;
           
            int cooldownSet = 0;

            if (eraName.contains("Future")) { //Yarıçapı 3 olan bir daire
                
            	if (absDx <= 3 && absDy <= 3) { 
            		
            		jumpPosition = target.getPosition(); 
            		cooldownSet = 4; 
            		
            	}
            } 
            
            else if (eraName.contains("Present")) { //3 hücre hareket çapraz dahil.
                
            	jumpPosition = new Position(this.position.getX() + (Integer.compare(dx,0)*3), this.position.getY() + (Integer.compare(dy,0)*3));
                cooldownSet = 4;
            } 
            
            else { // 2 hücre hareket çapraz dahil. L yok.
               
                int sx = Integer.compare(dx, 0); 
                int sy = Integer.compare(dy, 0);
                
                jumpPosition = new Position(this.position.getX() + (sx * 2), this.position.getY() + (sy * 2));
                cooldownSet = 3;
            
            }

            if (jumpPosition != null && jumpPosition.getX() >= 0 && jumpPosition.getX() < era.getGridSize() && jumpPosition.getY() >= 0 && jumpPosition.getY() < era.getGridSize()) {
               
            	this.position = jumpPosition;
                this.specialAbilityCD = cooldownSet;
                return;
            }
        }
       
        this.position = getCleverStep(target.getPosition(), era.getGridSize(), false);
   
    }
}