package characters;

import java.security.SecureRandom;
/**
 * Animal is the abstract base class for all dynamic entities in the game. It serves as the
root of the hierarchy, ensuring that all specific animal types share
a common structure. 
 */
public abstract class Animal {
    
	protected String name;
    protected Position position;
    protected int score;
    protected boolean isAlive;
    protected int specialAbilityCD;
    protected SecureRandom random;
    
    /**
     * It is the constructor that initialize everything.
     * @param name
     * @param x
     * @param y
     */
    public Animal(String name, int x, int y) {
      
    	this.name = name;
        this.position = new Position(x, y);
        this.score = 0;
        this.isAlive = true;
        this.specialAbilityCD = 0;
        this.random = new SecureRandom();
    }

    /**
     * This abstract method is the entry point for the entity's turn logic.
     * @param era
     */
    public abstract void act(Era era); 

    /**
     *Decrements the specialAbilityCooldown counter at the end of each turn,
managing the availability of special abilities.
     */
    public void reduceCD() {
        
    	if (this.specialAbilityCD > 0) {
    		
    		this.specialAbilityCD--;
    	
    	}
    }

    /**
     * Setter
     * @param value
     */
    public void setSpecialAbilityCD(int value) {
        
    	this.specialAbilityCD = value;
   
    }
    
    /**
     * Calculating distance.
     * @param p1
     * @param p2
     * @return
     */
    protected double getDistance(Position p1, Position p2) {
       
    	return Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2);
   
    }

    /**
     * Acts as the sensory system
for the AI. It iterates through the list of active entities to identify the nearest target of a
specific class type.
     * @param era
     * @param targetType
     * @return
     */
    protected Animal findClosestAnimal(Era era, Class<? extends Animal> targetType) {
       
    	Animal closest = null;
        double minDist = Double.MAX_VALUE;
        
        for (Animal a : era.getAnimals()) {
           
        	if (!a.isAlive() || a == this) {
        		
        		continue;
        	
        	}
            if (targetType.isInstance(a)) {
                
            	double dist = getDistance(this.position, a.getPosition());
                
            	if (dist < minDist) { 
            		
            		minDist = dist;
            		closest = a; 
            	
            	}
            }
        }
      
        return closest;
    }

    /**
     * Similar to the animal search, this method scans
available resources to allow Prey entities to identify and navigate toward the nearest
nutrition source.
     * @param era
     * @return
     */
    protected Food findClosestFood(Era era) {
       
    	Food closest = null;
        double minDist = Double.MAX_VALUE;
        
        for (Food f : era.getFoods()) {
           
        	double dist = getDistance(this.position, f.getPosition());
           
        	if (dist < minDist) { 
        		
        		minDist = dist; closest = f; 
        		
        	}
        }
       
        return closest;
    }
    
    /**
     * Implements a deterministic pathfinding logic (Greedy
Best-First approach).
     * @param target
     * @param gridSize
     * @param runAway
     * @return
     */
    protected Position getCleverStep(Position target, int gridSize, boolean runAway) {
       
    	int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {-1, -1}, {1, -1}, {-1, 1}};
        Position idealPosition = this.position;
        double idealDistance = runAway ? -1 : Double.MAX_VALUE;

        for (int[] direct : directions) {
           
        	int newX = this.position.getX() + direct[0];
            int newY = this.position.getY() + direct[1];
           
            if (newX >= 0 && newX < gridSize && newY >= 0 && newY < gridSize) {
               
            	Position option = new Position(newX, newY);
                double rangeToTarget = getDistance(option, target);
                
                if (runAway) {
                   
                	if (rangeToTarget > idealDistance) { 
                		
                		idealDistance = rangeToTarget; idealPosition = option; }
                } 
                
                else {
                   
                	if (rangeToTarget < idealDistance) { 
                		
                		idealDistance = rangeToTarget; idealPosition = option; 
                		
                	}
                }
            }
        }
        
        return idealPosition;
    }

    /**
     * A fallback movement mechanism. If the AI
cannot find a target or is blocked, this method returns a random valid adjacent coordinate
to prevent the entity from remaining static.
     * @param gridSize
     * @return
     */
    protected Position getRandomValidNeighbor(int gridSize) {
        
    	int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {-1, -1}, {1, -1}, {-1, 1}};
        
    	for (int i = 0; i < 8; i++) {
            
    		int r = random.nextInt(directions.length);
            int newX = position.getX() + directions[r][0];
            int newY = position.getY() + directions[r][1];
            
            if (newX >= 0 && newX < gridSize && newY >= 0 && newY < gridSize) {
               
            	return new Position(newX, newY);
            }
        }
       
    	return position;
    }
   
    /**
     * Moving.
     * @param target
     * @param gridSize
     */
    public void move(Position target, int gridSize) { 
    	
    	this.position = target; 
    	
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
   
    public int getScore() { 
    	
    	return score; 
    	
    }
  
    public void addScore(int points) { 
    	
    	this.score += points; 
    	
    }
   
    public void decreaseScore(int points) { 
    	
    	this.score -= points;
    	
    }
   
    public boolean isAlive() { 
    	
    	return isAlive; 
    	
    }
  
    public void setAlive(boolean alive) { 
    	
    	isAlive = alive; 
    	
    }
  
    public String getName() { 
    	
    	return name; 
    	
    }
   
    public int getCooldown() { 
    	
    	return specialAbilityCD; 
    	
    }
}