package characters;

import java.util.ArrayList;
import java.util.List;
import system.InvalidGameParameterException;

/**
 * The Era class acts as the container for the game environment.
 */
public class Era {
   
	private String name;
    private int gridSize;
    private List<Animal> animals;
    private List<Food> foods;
    private int currentRound = 0;   
    
    /**
     * Checks for the invalid game parameters.
     * @param name
     * @param gridSize
     * @throws InvalidGameParameterException
     */
    public Era(String name, int gridSize) throws InvalidGameParameterException{
        
    	
    	if (!name.contains("Past") && !name.contains("Present") && !name.contains("Future")) {
           
    		throw new InvalidGameParameterException("Invalid period name! It can only be Past, Present, or Future. Entered: "+ name);
       
    	}
        
        
    	this.name = name;
        this.gridSize = gridSize;
        this.animals = new ArrayList<>();
        this.foods = new ArrayList<>();
   
    }
    
    /**
     * Overall getter and setter with some adding methods.
     * @param animal
     */
    public void addAnimal(Animal animal) { 
    	
    	animals.add(animal);
    	
    }
    
    public void addFood(Food food) { 
    	
    	foods.add(food);
    	
    }
    
    public String getName() { 
    	
    	 return name;
    	 
    }
   
    public int getGridSize() { 
    	
    	return gridSize; 
    	
    }
    
    public List<Animal> getAnimals() { 
    	
    	return animals; 
    	
    }
    
    public List<Food> getFoods() { 
    	
    	return foods; 
    	
    }
    
    public int getCurrentRound() { 
    	
    	return currentRound; 
    	
    }
    
    public void setCurrentRound(int round) { 
    	
    	this.currentRound = round; 
    	
    }
}