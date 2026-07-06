package system;

import javax.swing.JComponent;
import java.util.List;
import java.security.SecureRandom;
import javax.swing.SwingUtilities;
import characters.Animal;
import characters.ApexPredator;
import characters.Era;
import characters.Food;
import characters.Position;
import characters.Predator;
import characters.Prey;
import screen.GameWindow;

/**
 * The core Game Engine
 */
public class GameMind {
   
	private Era currentEra;
    private int turnCount;
    private int maxRounds;
    private boolean isGameOver = false;
    private JComponent gamePanel; 

    /**
     * Constructor
     * @param era
     * @param maxRounds
     * @param gamePanel
     */
    public GameMind(Era era, int maxRounds, JComponent gamePanel) {
      
    	this.currentEra = era;
        this.maxRounds = maxRounds;
        this.gamePanel = gamePanel;
        this.turnCount = era.getCurrentRound();
        
        if (turnCount == 0) {
            
        	LogFile.startNewSession(); 
            LogFile.log("Game Started: " + era.getName());
            LogFile.log("Number of Rounds: " + maxRounds );
        }
    }

    /**
     * The master method for the game loop
     * @param dx
     * @param dy
     */
    public void playRound(int dx, int dy) {
        
    	if (isGameOver) {
    		
    		return;
    	
    	}
        
        LogFile.log("--- Round " + (turnCount + 1) + " Begins ---");
       
        boolean preyMoving = false;
        
        for (Animal a : currentEra.getAnimals()) {
           
        	if (a instanceof Prey && a.isAlive()) {
               
        		Position oldPosition = new Position(a.getPosition().getX(), a.getPosition().getY());
                a.act(currentEra);
               
                if (!oldPosition.equals(a.getPosition())) {
                	
                	preyMoving = true;
                	LogFile.log("Prey " + "(" + a.getName() + ")" + " moved to " + "(" + a.getPosition().getX() + "," + a.getPosition().getY() + ")");
                
                }
            }
        }
      
        if (preyMoving) {
        	
            delayAndRefresh(200); //Oyunu takip edebilmek için bunu koydum.
       
        }
   
        Animal player = null;
        
        for (Animal a : currentEra.getAnimals()) {
            
        	if (a instanceof Predator && !(a instanceof ApexPredator)) { 
        		player = a;
        		break; 
        		
        	}
        }
        
        if (player != null && player.isAlive()) {
            
        	if (dx == 0 && dy == 0) {
                
        		LogFile.log("Player skipped turn.");
            
        	} 
        	
        	else {
                
        		int oldX = player.getPosition().getX();               
        		int oldY = player.getPosition().getY();               
        		int newX = oldX + dx;              
        		int newY = oldY + dy;
                 
                if (newX >= 0 && newX < currentEra.getGridSize() && newY >= 0 && newY < currentEra.getGridSize()) {
                    
                	player.setPosition(new Position(newX, newY));
                	LogFile.log("Player (" + player.getName() + ") moved. New Location: " + "(" + player.getPosition().getX() +  ","  + player.getPosition().getY() + ")");
                    delayAndRefresh(200); 
                
                }
             }
        }
               
        for (Animal a : currentEra.getAnimals()) {
           
        	if (a instanceof Predator && !(a instanceof ApexPredator) && a != player && a.isAlive()) {
                
        		a.act(currentEra);
           
        	}
        }
  
        boolean apexMoving = false;
        
        for (Animal a : currentEra.getAnimals()) {
           
        	if (a instanceof ApexPredator && a.isAlive()) {
                
        		Position oldPosition = new Position(a.getPosition().getX(), a.getPosition().getY());
                a.act(currentEra);
               
                if (!oldPosition.equals(a.getPosition())) {
                	
                	apexMoving = true;
                	LogFile.log("Apex " + "(" + a.getName() + ")" + " moved to " + "(" + a.getPosition().getX() + "," + a.getPosition().getY() + ")");
              
                }
            }
        }
       
        if (apexMoving) {
           
            delayAndRefresh(200); 
        }
 
        for (Animal a : currentEra.getAnimals()) {
           
        	if (a.isAlive()) {
        		
        		a.reduceCD();
        	
        	}
        }

        interactAndEating();
        delayAndRefresh(50); 

        LogFile.log("--- Round " + (turnCount + 1) + " Ends ---");

        turnCount++;
        currentEra.setCurrentRound(turnCount); 

        if (turnCount >= maxRounds) {
          
        	isGameOver = true;
            logAndAnnounceWinner();
        }
    }
    
    /**
     * Delay to following game easier.
     * @param ms
     */
    private void delayAndRefresh(int ms) {
        
    	if (gamePanel != null) {
           
    		gamePanel.paintImmediately(gamePanel.getBounds());
      
    	}
       
    	try {
           
        	Thread.sleep(ms); 
        } 
        
        catch (InterruptedException e) {
           
        	e.printStackTrace();
       
        }
    }

    /**
     * Iterates through the entity lists to detect collisions.
     */
    private void interactAndEating() {
       
    	List<Animal> animals = currentEra.getAnimals();
        List<Food> foods = currentEra.getFoods();
        
        int i = 0;
        while (i < animals.size()) {

            Animal a1 = animals.get(i);

            if (!a1.isAlive()) {
               
            	i++;
                continue;
            }

            int j = i + 1;
            while (j < animals.size()) {

                Animal a2 = animals.get(j);

                if (!a2.isAlive() || a1 == a2) {
                   
                	j++;
                    continue;
                }

                if (a1.getPosition().getX() == a2.getPosition().getX() && a1.getPosition().getY() == a2.getPosition().getY()) {

                    handleInteraction(a1, a2);
                    LogFile.log("CHALLENGEE!: " + a1.getName() + ", attacked to " + a2.getName() + "!");
                }

                j++;
            }

            i++;
        }

        
        for (Food f : foods) {
           
        	for (Animal a : animals) {
               
        		if (a.isAlive() && a instanceof Prey && a.getPosition().getX() == f.getPosition().getX() && a.getPosition().getY() == f.getPosition().getY()) {
                   
        			if (((Prey)a).canEat()) {
                       
        				a.addScore(3);
                        respawnFood(f);
                        LogFile.log("FOOOOD!: " + a.getName() + " eats a food. ");
                    
        			}
                }
            }
        }
    }

    /**
     *  Determines the hierarchy of a collision.
     * @param a1
     * @param a2
     */
    private void handleInteraction(Animal a1, Animal a2) {
        
    	if (a1 instanceof ApexPredator && !(a2 instanceof ApexPredator)) {
    		
    		handleEating(a1, a2);
    	
    	}
       
    	else if (a2 instanceof ApexPredator && !(a1 instanceof ApexPredator)) {
        	
        	handleEating(a2, a1);
       
    	}
        
    	else if (a1 instanceof Predator && a2 instanceof Prey) {
        	
        	handleEating(a1, a2);
       
    	}
        
    	else if (a2 instanceof Predator && a1 instanceof Prey) {
        	
        	handleEating(a2, a1);
        }
    	
    	
    }

    /**
     * Executes the "kill".
     * @param hunter
     * @param victim
     */
    private void handleEating(Animal hunter, Animal victim) {
      
    	int points = (hunter instanceof ApexPredator) ? 1 : 3;
       
    	hunter.addScore(points);
        victim.decreaseScore(1); 
       
        
        if (hunter instanceof Predator && !(hunter instanceof ApexPredator)) {
            LogFile.log("SCOREEE: Player (" + hunter.getName() + ") gains " + points + " points! (Total: " + hunter.getScore() + ")");
       }

       if (victim instanceof Predator && !(victim instanceof ApexPredator)) {
            LogFile.log("SCOREEE: Player (" + victim.getName() + ") loses points! (Total: " + victim.getScore() + ")");
       }
        
        
        
        if (!(victim instanceof ApexPredator)) {
        	
        	respawnAnimal(victim);
        
        }
    }

    /**
     * Respawning of Animal and Food.
     * @param a
     */
    private void respawnAnimal(Animal a) {
       
    	SecureRandom rnd = new SecureRandom();
        int size = currentEra.getGridSize();
        a.setPosition(new Position(rnd.nextInt(size), rnd.nextInt(size)));
    
    }

    private void respawnFood(Food f) {
       
    	SecureRandom rnd = new SecureRandom();
        int size = currentEra.getGridSize();
        f.setPosition(new Position(rnd.nextInt(size), rnd.nextInt(size)));
   
    }

    /**
     * Called when maxRounds is reached. It calculates the
highest score, logs the final results to the text file, and triggers the UI popup
     */
    private void logAndAnnounceWinner() {
       
    	int maxScore = Integer.MIN_VALUE;
       
    	for (Animal a : currentEra.getAnimals()) {
           
    		if (a.getScore() > maxScore) {
    			
    			maxScore = a.getScore();
    		
    		}
        }
        
    	StringBuilder winners = new StringBuilder();
       
    	for (Animal a : currentEra.getAnimals()) {
           
    		if (a.getScore() == maxScore) {
               
    			if (winners.length() > 0) {
    				
    				winners.append(", ");
    			
    			}
               
    			winners.append(a.getName());
            }
        }
    	
    	LogFile.log("==========================================");
        LogFile.log("        GAME OVER        ");
        LogFile.log("==========================================");
        LogFile.log("WINNER WINNER : " + winners.toString() + " (Score: " + maxScore + ")");       
        LogFile.log("Final Scores:");
       
        for(Animal a : currentEra.getAnimals()) {
            
        	LogFile.log(" - " + a.getName() + ": " + a.getScore());
        
        }
        
        if (gamePanel != null) {
           
        	java.awt.Window win = SwingUtilities.getWindowAncestor(gamePanel);
           
            if (win instanceof GameWindow) {
               
            	((GameWindow) win).showCustomWinnerNotification(winners.toString());
            }
        }
    }

    /**
     * Getters.
     */
    public boolean isGameOver() {
    	
    	return isGameOver;
    	
    }
    
    public Era getCurrentEra() { 
    	
    	return currentEra; 
    	
    }
   
    public int getTurnCount() { 
    	
    	return turnCount; 
    	
    }
   
    public int getMaxRounds() { 
    	
    	return maxRounds; 
    	
    }
}