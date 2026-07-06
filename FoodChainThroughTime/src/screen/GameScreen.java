package screen;

import javax.swing.*;
import characters.Animal;
import characters.ApexPredator;
import characters.Era;
import characters.Food;
import characters.Predator;
import characters.Prey;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *  GameScreen is the primary rendering engine for the simulation. It visualizes the logic
state maintained by the Era class. It is responsible for drawing the grid, the terrain background,
and all entity sprites (Food, Animals) based on their coordinates.
 */
public class GameScreen extends JPanel {//Genel olayı bu classın resimleri yüklüyorum, gridleri çiziyorum olur da yüklenmezse resimler error olmaması için düz çizilmiş görsel koyuyorum.
   
	private Era currentEra;  
    private int cellWidth = 40;
    private int cellHeight = 40;
       
    private BufferedImage imgBgPast, imgBgPresent, imgBgFuture; //Eraların arka plan fotoğrafları.
    
    //Karakterlerin fotoğrafları:
    
    private BufferedImage imgTrex, imgRaptor, imgTriceratops, imgFerns; //Past Era FoodChain One
    private BufferedImage imgGiga, imgAllo, imgStego, imgCycads; //Past Era FoodChain Two

   
    private BufferedImage imgLion, imgCheetah, imgBunny, imgGrass; //Present Era FoodChain One
    private BufferedImage imgPolarBear, imgWolf, imgDeer, imgShrubs; //Present Era FoodChain Two
    
   
    private BufferedImage imgAlienOverlord, imgAlienHunter, imgHuman, imgCow; //Future Era FoodChain One
    private BufferedImage imgLeviathan, imgCyborg, imgRobot, imgEnergyNode; //Future Era FoodChain Two

    /**
     * Constructor that accepts the current game state (Era). It triggers
the image loading process to prepare all visual assets.
     * @param era
     */
    public GameScreen(Era era) {
        
    	this.currentEra = era;       
        loadImages(); //Resimleri hafızaya yükler.
   
    }
     
    /**
     * Getters
     * @return
     */
    public int getCellWidth() { 
    	
    	return cellWidth; 
    	
    }
   
    public int getCellHeight() { 
    	
    	return cellHeight;
    	
    }
    
    /**
     *  A comprehensive method that attempts to load all sprite assets
into memory using ImageIO.
     */
    private void loadImages() {
        
    	try {
            
    		//Try bloğu ile resimleri yüklüyorum, png formatında yüklüyorum.
            imgBgPast = load("images/past_bg.png");
            imgBgPresent = load("images/present_bg.png");
            imgBgFuture = load("images/future_bg.png");

          
            imgTrex = load("images/trex.png");
            imgRaptor = load("images/velociraptor.png");
            imgTriceratops = load("images/triceratops.png");
            imgFerns = load("images/ferns.png");
            imgGiga = load("images/giganotosaurus.png");
            imgAllo = load("images/allosaurus.png");
            imgStego = load("images/stegosaurus.png");
            imgCycads = load("images/cycads.png");

        
            imgLion = load("images/lion.png");
            imgCheetah = load("images/cheetah.png");
            imgBunny = load("images/bunny.png");
            imgGrass = load("images/grass.png");
            imgPolarBear = load("images/polarbear.png");
            imgWolf = load("images/wolf.png");
            imgDeer = load("images/deer.png");
            imgShrubs = load("images/shrubs.png");
            
         
            imgAlienOverlord = load("images/alienoverlord.png");
            imgAlienHunter = load("images/alienhunter.png");
            imgHuman = load("images/human.png");
            imgCow = load("images/cow.png"); 
            imgLeviathan = load("images/leviathan.png");
            imgCyborg = load("images/cyborg.png");
            imgRobot = load("images/robot.png");
            imgEnergyNode = load("images/energynode.png"); 
            
            System.out.println("All images have been uploaded successfully.");
        
    	} 
    	
    	catch (Exception e) { // Olur da görseller yüklenmezse diye bir hata kontrolü ki kod çalışsın.
           
    		System.out.println("Image upload error: " + e.getMessage());
        
    	}
    }
    
    /**
     * A utility wrapper around ImageIO.read to streamline file loading and
exception handling for individual images
     * @param path
     * @return
     */
    private BufferedImage load(String path) { //Resimleri okuma diyebilirim özetle.
       
    	try { 
    		
    		return ImageIO.read(new File(path)); 
    		
    	} 
        
    	catch (IOException e) { 
    		
    		System.out.println("Image loading error: " + path);  		
    		return null; 
    		
    	}
    }

    /**
     * The master rendering loop. It clears the screen,
recalculates cell dimensions dynamically , and calls specific
sub-methods in a layered order.
     */
    protected void paintComponent(Graphics g) {
       
    	super.paintComponent(g);//Paneli tertemiz yapar ki üst üste binmesin!
       
    	Graphics2D dim2 = (Graphics2D) g;
       
    	dim2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int gridSize = currentEra.getGridSize();
        
        cellWidth = getWidth() / gridSize;
        cellHeight = getHeight() / gridSize;
        
        if (cellWidth < 1) {
        	
        	cellWidth = 1;
       
        }
        
        if (cellHeight < 1) {
        	
        	cellHeight = 1;
       
        }
        
        //Sıra önemli çünkü bir layering söz konusu.
        drawBackground(dim2);
        drawGrid(dim2, gridSize);
        highlightValidMoves(dim2); 
        drawFoods(dim2);
        drawAnimals(dim2);
    }

    /**
     * Determines which background image to draw based on the current Era's name.
     * @param g
     */
    private void drawBackground(Graphics2D g) {
       
    	String eraName = currentEra.getName();
        BufferedImage backgroundToDraw = null;
        
        if (eraName.contains("Past")) {
        	
        	backgroundToDraw = imgBgPast;
       
        }
        
        else if (eraName.contains("Present")) {
        	
        	backgroundToDraw = imgBgPresent;
       
        }
        
        else if (eraName.contains("Future")) {
        	
        	backgroundToDraw = imgBgFuture;
        
        }
        
        if (backgroundToDraw != null) { // Bu ise gridi stretch ediyor aslında sol üst köşeden.
 
            g.drawImage(backgroundToDraw, 0, 0, getWidth(), getHeight(), null);
       
        } 
        
        else { // Olur da resim yüklenmezse
     
            if (eraName.contains("Past")) {
            	
            	g.setColor(new Color(210, 180, 140)); 
            
            }
            
            else if (eraName.contains("Present")) {
            	
            	g.setColor(new Color(100, 200, 100)); 
           
            }
            
            else if (eraName.contains("Future")) {
            	
            	g.setColor(new Color(20, 20, 50)); 
           
            }
           
            else {
            	
            	g.setColor(Color.WHITE);
           
            }
           
            g.fillRect(0, 0, getWidth(), getHeight());
       
        }
    }

    /**
     * Draws the vertical and horizontal lines constituting the game
board.
     * @param g
     * @param gridSize
     */
    private void drawGrid(Graphics2D g, int gridSize) {
        
    	g.setColor(new Color(0, 0, 0, 50)); 
       
    	if (currentEra.getName().contains("Future")) {//Arka plan koyu olduğu için özel bir grid.
    		
    		g.setColor(new Color(255, 255, 255, 50)); 
    	
    	}
      
    	int i = 0;
    	while (i <= gridSize) {
    	   
    		g.drawLine(i * cellWidth, 0, i * cellWidth, getHeight());
    	    i++;
    	
    	}

    	i = 0;
    	while (i <= gridSize) {
    	   
    		g.drawLine(0, i * cellHeight, getWidth(), i * cellHeight);
    	    i++;
    	
    	}
    }

    /**
     * Visualizes the player's potential moves
     * @param g
     */
    private void highlightValidMoves(Graphics2D g) {
        
    	Animal player = null;
       
    	for (Animal a : currentEra.getAnimals()) {
           
    		if (a instanceof Predator && a.isAlive()) { 
            	
            	player = a; 
            	break; 
            	
    		}
        }

        if (player != null) {
            
        	int px = player.getPosition().getX();
            int py = player.getPosition().getY();
            int gridSize = currentEra.getGridSize();
            
            String eraName = currentEra.getName();
            
            boolean canUseAbility = (player.getCooldown() == 0);

            boolean isApexSide = false;
           
            if (eraName.contains("Present")) {// Burada Apex ile Predator yan yana mı kontrolü yapıyorum.
               
            	for (Animal a : currentEra.getAnimals()) {
                  
            		if (a instanceof ApexPredator && a.isAlive()) {
                       
            			int dx = Math.abs(a.getPosition().getX() - px);
                        int dy = Math.abs(a.getPosition().getY() - py);
                        
                        if (Math.max(dx, dy) == 1) { 
                        	
                        	isApexSide = true; 
                        	break;
                        	
                        }
                    }
                }
            }

            int scanRange = 2; 

            for (int dx = -scanRange; dx <= scanRange; dx++) {
               
            	for (int dy = -scanRange; dy <= scanRange; dy++) {
                   
            		if (dx == 0 && dy == 0) {// Merkez noktası yani kendi için hesaplamayı geç.
            			
            			continue; 
            		
            		}

                    int nx = px + dx;
                    int ny = py + dy;

                    if (nx >= 0 && nx < gridSize && ny >= 0 && ny < gridSize) {
                       
                    	int dist = Math.max(Math.abs(dx), Math.abs(dy)); 
                        int absDx = Math.abs(dx);
                        int absDy = Math.abs(dy);
                     
                        if (dist == 1) {// Özel güç hücrelerini boyamak için.
                            
                        	g.setColor(new Color(255, 255, 255, 120)); 
                            g.fillRect(nx * cellWidth + 1, ny * cellHeight + 1, cellWidth - 1, cellHeight - 1);
                       
                        }
                        
                        else if (dist == 2) {
                           
                        	boolean isValidAbilitySquare = false;
                           
                        	if (eraName.contains("Past")) {
                                
                        		if ((dx == 0 || dy == 0) && canUseAbility) {
                                	
                                	isValidAbilitySquare = true;
                               
                                }
                            } 
                           
                        	else if (eraName.contains("Present")) {
                                
                        		if (isApexSide) {
                                	
                                	isValidAbilitySquare = true;
                                
                                }
                            } 
                            
                        	else if (eraName.contains("Future")) {
                               
                        		boolean isStraight = (dx == 0 || dy == 0);
                                boolean isDiagonal = (absDx == absDy);
                               
                                if ((isStraight || isDiagonal) && canUseAbility) {
                                	
                                	isValidAbilitySquare = true;
                               
                                }
                            }

                            if (isValidAbilitySquare) {
                              
                            	g.setColor(new Color(255, 50, 200, 120)); 
                                g.fillRect(nx * cellWidth + 1, ny * cellHeight + 1, cellWidth - 1, cellHeight - 1);
                                g.setColor(Color.WHITE);
                                g.fillOval(nx * cellWidth + cellWidth/2 - 2, ny * cellHeight + cellHeight/2 - 2, 4, 4);
                          
                            }
                        }
                    }
                }
            }
            
            //Seçilen hücreyi vurgulamak.
            g.setStroke(new BasicStroke(2)); 
            g.setColor(currentEra.getName().contains("Future") ? Color.WHITE : Color.BLACK);
            g.drawRect(px * cellWidth, py * cellHeight, cellWidth, cellHeight);
            g.setStroke(new BasicStroke(1));
        }
    }

    /**
     *  Iterates through the list of Food objects. It matches the food
name to its corresponding loaded image and draws it centered
within the grid cell.
     * @param g
     */
    private void drawFoods(Graphics2D g) {//Foodları görsellerle ekliyorum, hücrenin ortasında dursun diye de -4 ve +2 ifadeleri var.
        
    	for (Food f : currentEra.getFoods()) {
          
    		int x = f.getPosition().getX() * cellWidth;
            int y = f.getPosition().getY() * cellHeight;
           
            String foodName = f.getName().toLowerCase();
            
            
            if (foodName.contains("fern") && imgFerns != null) {
            	
            	g.drawImage(imgFerns, x+2, y+2, cellWidth-4, cellHeight-4, null);
            
            }
           
            else if (foodName.contains("cycad") && imgCycads != null) {
            	
            	g.drawImage(imgCycads, x+2, y+2, cellWidth-4, cellHeight-4, null);
          
            }
          
            else if (foodName.contains("grass") && imgGrass != null) {
            	
            	g.drawImage(imgGrass, x+2, y+2, cellWidth-4, cellHeight-4, null);
           
            }
           
            else if (foodName.contains("shrub") && imgShrubs != null) {
            	
            	g.drawImage(imgShrubs, x+2, y+2, cellWidth-4, cellHeight-4, null);
           
            }
         
            else if ((foodName.contains("energy") || foodName.contains("node") || foodName.contains("orb")) && imgEnergyNode != null) {
               
            	g.drawImage(imgEnergyNode, x+2, y+2, cellWidth-4, cellHeight-4, null);
           
            }
           
            else if (foodName.equals("cow") && imgCow != null) { 
               
            	g.drawImage(imgCow, x+2, y+2, cellWidth-4, cellHeight-4, null);
            
            }
           
            else { //Yine aynı şekilde olur da görsel yüklenmezse turuncu kare çıksın diye.
                
            	g.setColor(new Color(255, 140, 0)); 
                g.fillRect(x + cellWidth/4, y + cellHeight/4, cellWidth/2, cellHeight/2);
            }
            
            //Yemek hücresini yeşil boyuyorum.
            g.setColor(new Color(0, 200, 0)); 
            g.setStroke(new BasicStroke(3)); 
            g.drawRect(x, y, cellWidth, cellHeight);
            g.setStroke(new BasicStroke(1)); 
       
    	}
    }

    /**
     * Iterates through the list of Animal objects. It matches the
animal name to the correct sprite and draws it.
     * @param g
     */
    private void drawAnimals(Graphics2D g) {
        
    	String eraName = currentEra.getName();
        
        for (Animal a : currentEra.getAnimals()) {
            if (!a.isAlive()) {
            	
            	continue;
           
            }
           
            int x = a.getPosition().getX() * cellWidth;
            int y = a.getPosition().getY() * cellHeight;
            
            String name = a.getName();
           
            boolean imageDrawn = false;
    
            if (name.contains("T-Rex") && imgTrex != null) { 
            	
            	g.drawImage(imgTrex, x+2, y+2, cellWidth-4, cellHeight-4, null); 
            	imageDrawn = true;
            	
            } 
            
            else if (name.contains("Giganotosaurus") && imgGiga != null) { 
            	
            	g.drawImage(imgGiga, x+2, y+2, cellWidth-4, cellHeight-4, null); 
            	imageDrawn = true;
            	
            }
           
            else if ((name.contains("Velociraptor") || name.contains("Raptor")) && imgRaptor != null) {
            	
            	g.drawImage(imgRaptor, x+2, y+2, cellWidth-4, cellHeight-4, null);
            	imageDrawn = true;
            	
            }
           
            else if (name.contains("Allosaurus") && imgAllo != null) { 
            	
            	g.drawImage(imgAllo, x+2, y+2, cellWidth-4, cellHeight-4, null); 
            	imageDrawn = true; 
            	
            }
           
            else if (name.contains("Triceratops") && imgTriceratops != null) { 
            	
            	g.drawImage(imgTriceratops, x+2, y+2, cellWidth-4, cellHeight-4, null);
            	imageDrawn = true; 
            	
            }
           
            else if (name.contains("Stegosaurus") && imgStego != null) { 
            	
            	g.drawImage(imgStego, x+2, y+2, cellWidth-4, cellHeight-4, null); 
            	imageDrawn = true; 
            	
            }
        
            else if (name.contains("Lion") && imgLion != null) { 
            	
            	g.drawImage(imgLion, x+2, y+2, cellWidth-4, cellHeight-4, null); 
            	imageDrawn = true; 
            	
            }
            
            else if (name.contains("Polar") && imgPolarBear != null) { 
            	
            	g.drawImage(imgPolarBear, x+2, y+2, cellWidth-4, cellHeight-4, null); 
            	imageDrawn = true;
            	
            }
           
            else if (name.contains("Cheetah") && imgCheetah != null) { 
            	
            	g.drawImage(imgCheetah, x+2, y+2, cellWidth-4, cellHeight-4, null);
            	imageDrawn = true; 
            	
            }
            
            else if (name.contains("Wolf") && imgWolf != null) { 
            	
            	g.drawImage(imgWolf, x+2, y+2, cellWidth-4, cellHeight-4, null); 
            	imageDrawn = true;
            	
            }
               
            else if ((name.contains("Bunny") || name.contains("Rabbit")) && imgBunny != null) { 
            	
            	g.drawImage(imgBunny, x+2, y+2, cellWidth-4, cellHeight-4, null); 
            	imageDrawn = true; 
            	
            }
            
            else if (name.contains("Deer") && imgDeer != null) { 
            	
            	g.drawImage(imgDeer, x+2, y+2, cellWidth-4, cellHeight-4, null);
            	imageDrawn = true; 
            	
            }
     
            else if (name.equals("Alien Overlord") && imgAlienOverlord != null) { 
              
            	g.drawImage(imgAlienOverlord, x+2, y+2, cellWidth-4, cellHeight-4, null);
            	imageDrawn = true; 
           
            }
           
            else if (name.equals("Leviathan") && imgLeviathan != null) { 
               
            	g.drawImage(imgLeviathan, x+2, y+2, cellWidth-4, cellHeight-4, null); 
                imageDrawn = true; 
           
            }
            else if (name.equals("Alien Hunter") && imgAlienHunter != null) { 
               
            	g.drawImage(imgAlienHunter, x+2, y+2, cellWidth-4, cellHeight-4, null);
            	imageDrawn = true; 
           
            }
           
            else if (name.equals("Cyborg") && imgCyborg != null) { 
              
            	g.drawImage(imgCyborg, x+2, y+2, cellWidth-4, cellHeight-4, null); 
                imageDrawn = true; 
           
            }
           
            else if (name.equals("Human") && imgHuman != null) { 
                
            	g.drawImage(imgHuman, x+2, y+2, cellWidth-4, cellHeight-4, null); 
            	imageDrawn = true; 
           
            }
           
            else if (name.equals("Robot") && imgRobot != null) { 
                
            	g.drawImage(imgRobot, x+2, y+2, cellWidth-4, cellHeight-4, null); 
                imageDrawn = true; 
          
            }
           
            if (imageDrawn == false) {
               
            	Color bodyColor;
                if (a instanceof ApexPredator) {
                	
                	bodyColor = new Color(139, 0, 0); 
               
                }
               
                else if (a instanceof Predator) {
                	
                	bodyColor = new Color(30, 144, 255); 
               
                }
               
                else {
                	
                	bodyColor = new Color(34, 139, 34); 
                }

                g.setColor(bodyColor);
               
                if (eraName.contains("Past")) {
                	
                	g.fillRect(x + 5, y + 5, cellWidth - 10, cellHeight - 10);
               
                }
               
                else if (eraName.contains("Future")) {
                   
                	g.fillRoundRect(x + 5, y + 5, cellWidth - 10, cellHeight - 10, 15, 15);
                    g.setColor(Color.WHITE); g.fillOval(x + cellWidth/2 - 3, y + cellHeight/2 - 3, 6, 6);
                
                } 
                
                else {
                	
                	g.fillOval(x + 5, y + 5, cellWidth - 10, cellHeight - 10);
                }
            }

          
            g.setStroke(new BasicStroke(3)); 
           
            if (a instanceof ApexPredator) {
               
            	g.setColor(Color.RED);
                g.drawRect(x, y, cellWidth, cellHeight);
           
            } 
            else if (a instanceof Prey) {
               
            	g.setColor(Color.BLUE);
                g.drawRect(x, y, cellWidth, cellHeight);
           
            }
           
            g.setStroke(new BasicStroke(1)); 
       
        }
    }
}