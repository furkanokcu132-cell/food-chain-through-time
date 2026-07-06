package characters;
/**
 * Represents the primary consumers in the ecosystem. Extending the Animal class, it
implements a Defensive AI that uses a weighted scoring system to make survival decisions.
 */
public class Prey extends Animal {

    private boolean dontEat = true;

    /**
     * Constructor
     * @param name
     * @param x
     * @param y
     */
    public Prey(String name, int x, int y) {
       
    	super(name, x, y);
   
    }

    public boolean canEat() { 
    	
    	return dontEat; 
    	
    }
    
    /**
     * The main decision loop that orchestrates the behavior. It first checks for
immediate threats to trigger specialPrey. If no special move is required, it
calls StrategicMove to navigate the grid intelligently towards food while avoiding danger.
     */
    public void act(Era era) { //Prey Algoritması
        
    	if (!isAlive) {
    		
    		return;
    	
    	}
        
    	this.dontEat = true;

        Animal predator = findClosestAnimal(era, Predator.class);
        Animal apex = findClosestAnimal(era, ApexPredator.class);
        Animal closestThreat = null;
        
        double distPred = (predator != null) ? getDistance(this.position, predator.getPosition()) : Double.MAX_VALUE;
        double distApex = (apex != null) ? getDistance(this.position, apex.getPosition()) : Double.MAX_VALUE;
        
        if (predator != null || apex != null) {
        	
        	closestThreat = (distPred < distApex) ? predator : apex;
        
        }

        if (specialAbilityCD == 0 && closestThreat != null) {
           
        	double rangeToThreat = getDistance(this.position, closestThreat.getPosition());
            
        	if (rangeToThreat < 16) { 
               
        		specialPrey(era, closestThreat);
                return;
            
        	}
        }

        Food closestFood = findClosestFood(era);
        Position idealMove = StrategicMove(era, closestThreat, closestFood);
        
        if (idealMove != null) {
        	
        	this.position = idealMove;
        
        }
    }

    /**
     * Implements a scoring algorithm to determine the optimal next position. 
     * @param era
     * @param threat
     * @param food
     * @return
     */
    private Position StrategicMove(Era era, Animal threat, Food food) { //Kaçma ve Kovalama Mekanizması
        
    	int gridSize = era.getGridSize();
       
    	Position idealPosition = this.position;
       
    	double bestScore = -Double.MAX_VALUE;
        int[][] directions = {{0,0}, {0,1}, {0,-1}, {1,0}, {-1,0}, {1,1}, {-1,-1}, {1,-1}, {-1,1}};

        for (int[] direct : directions) {
            
        	int nx = this.position.getX() + direct[0];
            int ny = this.position.getY() + direct[1];
            
            if (nx < 0 || nx >= gridSize || ny < 0 || ny >= gridSize) {
            	
            	continue;
           
            }

            Position option = new Position(nx, ny);
            
            double score = 0;
            double rangeToThreat = (threat != null) ? getDistance(option, threat.getPosition()) : Double.MAX_VALUE;
            double rangeToFood = (food != null) ? getDistance(option, food.getPosition()) : 0;

            if (rangeToThreat < 9) {
            	
            	score = rangeToThreat * 750; //Prey daha çok kaçmaya odaklansın istediğim için daha önemli yaptım.
           
            }
            
            else {
                
            	score = (1000 - rangeToFood * 10);
                score += (rangeToThreat * 0.5); 
           
            }

            if (score > bestScore) { 
            	
            	bestScore = score; 
            	idealPosition = option; }
        }
        
        return idealPosition;
    
    }

    /**
     * Executes the specific evasion maneuver when the
entity is cornered. It calculates an escape vector based on the threat's relative position and
applies polymorphic movement rules:
     * @param era
     * @param threat
     */
    private void specialPrey(Era era, Animal threat) { //Prey Özel Gücü
        
    	String eraName = era.getName();
        
    	int dx = Integer.compare(this.position.getX(), threat.getPosition().getX());
        int dy = Integer.compare(this.position.getY(), threat.getPosition().getY());
        int moveX = 0, moveY = 0, cooldownSet = 0;

        if (eraName.contains("Future")) { //3 hücre hareket ama yemek yasak.
        	
        	moveX = dx * 3; 
        	moveY = dy * 3; 
        	cooldownSet = 3; 
        	this.dontEat = false;
        	
        } 
       
        else if (eraName.contains("Present")) { // 2 hücre hareket çapraz dahil.
        	
        	moveX = dx * 2;
        	moveY = dy * 2; 
        	cooldownSet = 4; 
        	
        } 
       
        else { // Zigzag 
           
            moveX = dx * 2; 
            
            if (dy != 0) {
               
            	moveY = dy; 
            } 
            
            else {
                
            	moveY = (random.nextBoolean() ? 1 : -1); 
            }
            
            cooldownSet = 3;
       
        }

        int newX = this.position.getX() + moveX;
        int newY = this.position.getY() + moveY;

        if (newX >= 0 && newX < era.getGridSize() && newY >= 0 && newY < era.getGridSize()) {
           
        	this.position = new Position(newX, newY);
            this.specialAbilityCD = cooldownSet;
       
        }
    }
}