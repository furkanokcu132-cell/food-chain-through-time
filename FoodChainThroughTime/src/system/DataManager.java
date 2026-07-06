package system;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import characters.Animal;
import characters.ApexPredator;
import characters.Era;
import characters.Food;
import characters.Predator;
import characters.Prey;
import java.security.SecureRandom;
import system.InvalidGameParameterException;

/**
 * his class is responsible for all File I/O operations related to game state persistence.
It handles saving the current game progress and loading data from two distinct sources
 */
public class DataManager {
   
	/**
	 * Serializes the current state of the Era object
into a text file.
	 * @param era
	 * @param filePath
	 */
    public static void saveEraToFile(Era era, String filePath) {
       
    	try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
           
    		bw.write("EraName:" + era.getName()); bw.newLine();
            bw.write("GridSize:" + era.getGridSize()); bw.newLine();
            bw.write("Round:" + era.getCurrentRound()); bw.newLine(); 

            for (Animal a : era.getAnimals()) {
               
            	if (!a.isAlive()) {
            		
            		continue;
            	
            	}
                
            	String type = a.getClass().getSimpleName();
                String line = String.format("ANIMAL:%s,%s,%d,%d,%d,%d",type, a.getName(), a.getPosition().getX(), a.getPosition().getY(),a.getScore(), a.getCooldown());
                bw.write(line);
                bw.newLine();
           
            }
          
            for (Food f : era.getFoods()) {
                
                String line = String.format("FOOD:%s,%d,%d", f.getName(), f.getPosition().getX(), f.getPosition().getY());
                bw.write(line);
                bw.newLine();
           
            }
           
            LogFile.log("SYSTEM: Game saved to " + filePath);
        } 
    	
    	catch (IOException e) { 
    		
    		e.printStackTrace(); }
   
    }
   
    /**
     * The counterpart to the save method.
     * @param filePath
     * @return
     */
    public static Era loadSavedGame(String filePath) {
       
    	try {
           
    		File f = new File(filePath);
           
    		if (!f.exists()) {
    			
    			return null;
    		
    		}

            List<String> lines = Files.readAllLines(Paths.get(filePath));
            String eraName = "Saved Game";
           
            int gridSize = 15;
            int round = 0;

            for (String line : lines) {
               
            	if (line.startsWith("EraName:")) eraName = line.split(":")[1].trim();
                if (line.startsWith("GridSize:")) gridSize = Integer.parseInt(line.split(":")[1].trim());
                if (line.startsWith("Round:")) {
                    
                	try { 
                		
                		round = Integer.parseInt(line.split(":")[1].trim()); 
                		
                	} 
                	
                	catch (Exception e) { 
                		
                		round = 0; 
                		
                	}
                }
            }
            
            Era era = null;
            
            try {
            	
            	era = new Era(eraName, gridSize);
            	
            }
            
            catch(InvalidGameParameterException e) {
            	
            	System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
            	           	
            }
            	
            era.setCurrentRound(round);

            for (String line : lines) {
                
            	try {
                    
            		if (line.startsWith("ANIMAL:")) {
                       
            			String[] data = line.substring(7).split(",");
                        
            			if (data.length < 6) {
            				
            				continue;
            		
            			}

                        String type = data[0].trim();
                        String name = data[1].trim();
                      
                        int x = Integer.parseInt(data[2].trim());
                        int y = Integer.parseInt(data[3].trim());
                        int score = Integer.parseInt(data[4].trim());
                        int cooldown = Integer.parseInt(data[5].trim());

                        Animal a = AnimalFactory.createAnimal(type, name, x, y);
                       
                        if (a != null) {
                           
                        	a.addScore(score);
                            a.setSpecialAbilityCD(cooldown);
                            era.addAnimal(a);
                        }
                    } 
                   
                    else if (line.startsWith("FOOD:")) {
                       
                    	String[] data = line.substring(5).split(",");
                        
                        if (data.length < 3) {
                        	
                        	continue; 
                        
                        }
                        
                        String foodName = data[0].trim(); 
                        int x = Integer.parseInt(data[1].trim());
                        int y = Integer.parseInt(data[2].trim());
                                                
                        era.addFood(new Food(foodName, x, y));
                   
                    }
                } 
            	
            	catch (Exception e) {}
           
            }
            
            LogFile.log("SYSTEM: Game loaded from " + filePath);
            return era;

        }
    	
    	catch (IOException e) {
           
    		e.printStackTrace();
            return null;
        }
    }


    /**
     * Initializes a new game. 
     * @param filePath
     * @param gridSize
     * @param startRound
     * @return
     */
    public static Era loadEraFromListFile(String filePath, int gridSize, int startRound) {
        
    	String eraName = "New Era";
        
    	if(filePath.contains("past")) {
    		
    		eraName = "Past Era";
    	
    	}
       
    	else if(filePath.contains("present")) {
    		
    		eraName = "Present Era";
    	
    	}
       
    	else if(filePath.contains("future")) {
    		
    		eraName = "Future Era";
    	
    	}
    	
    	Era era = null; 
    	
    	try {
        
    		era = new Era(eraName, gridSize);
    		
    	}
    	
    	catch (InvalidGameParameterException e) {
    		
    		System.err.println("Error: " + e.getMessage());
    	    e.printStackTrace();
    	
    	}
    
        era.setCurrentRound(startRound);
        
        File file = new File(filePath);
        SecureRandom random = new SecureRandom();

        if (!file.exists()) {
        	
        	return null; 
       
        }

        List<String> foodChainLines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
           
        	String line;
            
        	while ((line = br.readLine()) != null) {
               
        		if (line.trim().startsWith("Food Chain")) {
                  
        			foodChainLines.add(line);
                
        		}
            }
            
            if (!foodChainLines.isEmpty()) {
                
            	String selectedChain = foodChainLines.get(random.nextInt(foodChainLines.size()));
                parseAndAddEntities(era, selectedChain, gridSize, random);
                return era; 
            } 
            
            else {
                
            	return null; 
            }

        } 
        
        catch (IOException e) { 
           
        	e.printStackTrace(); 
            return null; 
        }
    }

    /**
     * A private helper that splits a "Food Chain" into individual names.
     * @param era
     * @param line
     * @param gridSize
     * @param random
     */
    private static void parseAndAddEntities(Era era, String line, int gridSize, SecureRandom random) {
        
    	try {
          
    		String content = line.split(":")[1].trim(); 
            String[] names = content.split(",");
            
            if (names.length >= 4) {
               
            	addSingleEntity(era, "ApexPredator", names[0].trim(), gridSize, random);
                addSingleEntity(era, "Predator", names[1].trim(), gridSize, random);
                addSingleEntity(era, "Prey", names[2].trim(), gridSize, random);
                addSingleFood(era, names[3].trim(), gridSize, random);
            }
        } 
    	
    	catch (Exception e) {
    		
    		e.printStackTrace();
    		
    	}
    }

    /**
     * A
private helper that attempts to place a specific animal on the grid. 
     * @param era
     * @param role
     * @param name
     * @param size
     * @param rnd
     */
    private static void addSingleEntity(Era era, String role, String name, int size, SecureRandom rnd) {
        
    	int x, y, tries=0;
        do { x = rnd.nextInt(size); y = rnd.nextInt(size); tries++; } while(isOccupied(era, x, y) && tries<100);
        Animal a = AnimalFactory.createAnimal(role, name, x, y);
        if(a!=null) era.addAnimal(a);
    }

    /**
     * Similar to addSingleEntity, but specifically handles the instantiation and placement of Food objects.
     * @param era
     * @param name
     * @param size
     * @param rnd
     */
    private static void addSingleFood(Era era, String name, int size, SecureRandom rnd) {
      
    	int x, y, tries=0;
        do { x = rnd.nextInt(size); y = rnd.nextInt(size); tries++; } while(isOccupied(era, x, y) && tries<100);
        era.addFood(new Food(name, x, y));
    }

    /**
     * A collision detection utility.
     * @param era
     * @param x
     * @param y
     * @return
     */
    private static boolean isOccupied(Era era, int x, int y) {
        
    	for(Animal a : era.getAnimals()) {
        	
        	if(a.getPosition().getX()==x && a.getPosition().getY()==y) {
        		
        		return true;
        	}
        }
        for(Food f : era.getFoods()) {
        	
        	if(f.getPosition().getX()==x && f.getPosition().getY()==y) {
        		
        		return true;
        	}
        }
       
        return false;
    }
}

/**
 * This class implements the Factory Design Pattern. It is responsible for
creating concrete instances of the Animal hierarchy based on string inputs
 */
class AnimalFactory {
   
	/**
	 * Accepts a type string and initialization data.
	 * @param type
	 * @param name
	 * @param x
	 * @param y
	 * @return
	 */
	public static Animal createAnimal(String type, String name, int x, int y) {
        
		if (type.equalsIgnoreCase("Predator")) {
			
			return new Predator(name, x, y);
		}
      
		if (type.equalsIgnoreCase("ApexPredator") || type.equalsIgnoreCase("Apex")) {
			
			return new ApexPredator(name, x, y);
		}
       
		if (type.equalsIgnoreCase("Prey")) {
			
			return new Prey(name, x, y);
		}
      
		return null;
    }
}