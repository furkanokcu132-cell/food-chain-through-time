package screen;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import characters.Animal;
import characters.ApexPredator;
import characters.Era;
import characters.Predator;
import characters.Prey;
import system.DataManager;
import system.GameMind;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * GameWindow acts as the main container and controller for the application
 */
public class GameWindow extends JFrame {
    
    private CardLayout screenLayout;
    private JPanel screenContainer;     
    private GameMind gameEngine;
    private GameScreen gamePanel;
    private JLabel lblRound, lblEra;
    private JLabel lblApexName, lblApexScore, lblApexCD;
    private JLabel lblPredName, lblPredScore, lblPredCD;
    private JLabel lblPreyName, lblPreyScore, lblPreyCD;

    /**
     * Constructor
     */
    public GameWindow() {
       
    	this.setTitle("Food Chain Through Time Game");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 700); 
        this.setLocationRelativeTo(null);
        this.setJMenuBar(createMenuBar());

        screenLayout = new CardLayout();
        screenContainer = new JPanel(screenLayout);

        GamePanel menuPanel = new GamePanel(this);
        screenContainer.add(menuPanel, "MENU");

        this.add(screenContainer);
       
        screenLayout.show(screenContainer, "MENU");
       
        this.setVisible(true);
   
    }

    /**
     * Constructs the top navigation bar containing options to "Save the
Game," return to the "Main Menu," or "Exit." 
     * @return
     */
    private JMenuBar createMenuBar() {
       
    	JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Options");
        JMenuItem saveItem = new JMenuItem("Save the Game");
    
        //Oynanan oyunları kaydetmek için 
        saveItem.addActionListener(e -> {
           
        	if (gameEngine != null) {
               
        		DataManager.saveEraToFile(gameEngine.getCurrentEra(), "saved_game.txt");
                showCustomSaveNotification(); 
           
        	}
        });

        JMenuItem menuItem = new JMenuItem("Back to Main Menu");
        menuItem.addActionListener(e -> screenLayout.show(screenContainer, "MENU"));

        JMenuItem exitItem = new JMenuItem("Exit from Game");
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(saveItem);
        fileMenu.add(menuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        return menuBar;
    }


    /**
     * Pop ups off winning the game and saving the game.
     */
    private void showCustomSaveNotification() { //1.5 sn dursun Game Saved görseli ardından da gitsin.
        
    	showCustomPopup("images/game_save.png", null, 1500);
    
    }
    

    public void showCustomWinnerNotification(String winnersList) { //Bu da 3 sn dursun ki kazananı okuyabilelim.
       
    	showCustomPopup("images/game_winner.png", winnersList, 3000);
    
    }

    /**
     * Creates borderless JDialog.
     * @param imagePath
     * @param overlayText
     * @param duration
     */
    private void showCustomPopup(String imagePath, String overlayText, int duration) {
        
    	try {
           
    		File imageFile = new File(imagePath);
           
    		if (!imageFile.exists()) {
                
    			String msg = (overlayText != null) ? "Winners: " + overlayText : "Process completed!";
                JOptionPane.showMessageDialog(this, msg);
                return;
           
    		}

            BufferedImage rawImage = ImageIO.read(imageFile);
            
            //Ölçekleme yapıyorum.
            int targetWidth = 500; 
            int targetHeight = (int) ((double)rawImage.getHeight() / rawImage.getWidth() * targetWidth);
            Image scaledImage = rawImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon(scaledImage);
            
            JDialog dialog = new JDialog(this);         
            dialog.setUndecorated(true);
            dialog.setBackground(new Color(0, 0, 0, 0));
            
            //Resmi ekliyorum.
            JLabel label = new JLabel(icon);         
            label.setLayout(new BorderLayout()); 
            label.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        
            if (overlayText != null) {//Burada resim üstüne kazananları yazdırıyorum ama yazı tipinden dolayı çirkin duruyor.
               
                JLabel textLbl = new JLabel("<html><div style='text-align: right;'>" + overlayText.toUpperCase() + "</div></html>", SwingConstants.RIGHT);
                
                textLbl.setFont(new Font("Monospaced", Font.BOLD, 28));
                textLbl.setForeground(Color.BLACK); 
                
                
                textLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 30)); 
                
                label.add(textLbl, BorderLayout.SOUTH); 
            }

            dialog.add(label);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

            new javax.swing.Timer(duration, e -> dialog.dispose()).start();

        } 
    	
    	catch (IOException ex) {
           
    		ex.printStackTrace();
        }
    }

    /**
     *  The critical method that
bridges the UI and the Logic. It loads the Era, initializes the GameScreen and GameMind, and attaches
a MouseAdapter. 
     * @param filePath
     * @param gridSize
     * @param maxRounds
     */
    public void startGame(String filePath, int gridSize, int maxRounds) {
      
    	Era era;
       
    	if (filePath.equals("saved_game.txt")) {
           
    		era = DataManager.loadSavedGame(filePath);
          
        } 
    	
    	else {
           
    		era = DataManager.loadEraFromListFile(filePath, gridSize, 0);
       
    	}

        if (era == null) {
        	
        	return;
        
        }

        gamePanel = new GameScreen(era);
        gameEngine = new GameMind(era, maxRounds, gamePanel);
        gamePanel.addMouseListener(new java.awt.event.MouseAdapter() {
        
            public void mouseClicked(java.awt.event.MouseEvent e) { //Mouse ile tıkladığımda izin verip vermeyeceğini hesaplıyor.
                
            	if (gameEngine.isGameOver()) {
            		
            		return;
            
            	}
                
                int cw = gamePanel.getCellWidth();
                int ch = gamePanel.getCellHeight();
                
                if (cw == 0) {
                	
                	cw = 1; 
                }
                
                if (ch == 0) {
                	
                	ch = 1;
                }

                int clickedX = e.getX() / cw;
                int clickedY = e.getY() / ch;
                
                Animal player = findPlayer();
                
                if (player != null && player.isAlive()) {
                  
                	int px = player.getPosition().getX();
                    int py = player.getPosition().getY();
                    
                    int dx = clickedX - px;
                    int dy = clickedY - py;
                    int dist = Math.max(Math.abs(dx), Math.abs(dy)); 
                   
                    String eraName = gameEngine.getCurrentEra().getName();

                    boolean moveOffered = false;
                    int CDPenalty = 0;

                    if (dist <= 1) {
                        
                    	moveOffered = true;
                    
                    } 
                    
                    else {
                        
                    	boolean isValidMove = false;
                        
                    	if (eraName.contains("Past") && ((dx==0 || dy==0) && dist<=2)) { 
                    		
                    		isValidMove=true;
                    		CDPenalty=3; 
                    		
                    	}
                       
                    	else if (eraName.contains("Future") && ((dx==0 || dy==0 || Math.abs(dx)==Math.abs(dy)) && dist<=2)) { 
                    		
                    		isValidMove=true; 
                    		CDPenalty=2; 
                    		
                    	}
                        
                    	else if (eraName.contains("Present")) {
                            
                    		boolean isNearApex = false;
                           
                    		for (Animal a : gameEngine.getCurrentEra().getAnimals()) {
                                
                    			if (a instanceof ApexPredator && a.isAlive()) {
                                    
                    				if (Math.max(Math.abs(a.getPosition().getX()-px), Math.abs(a.getPosition().getY()-py)) == 1) {
                    					
                    					isNearApex=true;
                    				
                    				}
                                 }
                             }
                            
                    		if (isNearApex && dist<=2) { 
                    			
                    			isValidMove=true; 
                    			CDPenalty=0; 
                    			
                    		}
                        }

                        if (isValidMove && player.getCooldown() == 0) {
                            
                        	moveOffered = true;
                            
                        	if(CDPenalty > 0) {
                        		
                        		player.setSpecialAbilityCD(CDPenalty);
                        	
                        	}
                        }
                    }

                    if (moveOffered) {
                        
                    	new Thread(() -> {
                           
                    		gameEngine.playRound(dx, dy);
                            SwingUtilities.invokeLater(() -> updateUI());
                       
                    	}).start();
                    }
                }
            }
        });
        
        JPanel gameContainer = new JPanel(new BorderLayout());
        gameContainer.add(gamePanel, BorderLayout.CENTER);
        
        JPanel sidebar = createSidebar();
        gameContainer.add(sidebar, BorderLayout.EAST);
        
        screenContainer.add(gameContainer, "GAME");
        screenLayout.show(screenContainer, "GAME");
        
        updateUI(); 
    }

    /**
     * Constructs the right-hand UI panel.
     */
    private JPanel createSidebar() {
        
    	JPanel infoBar = new JPanel();
     
    	infoBar.setLayout(new BoxLayout(infoBar, BoxLayout.Y_AXIS));
        infoBar.setPreferredSize(new Dimension(250, 0)); 
        infoBar.setBackground(Color.LIGHT_GRAY);
        infoBar.setBorder(new EmptyBorder(10, 10, 10, 10)); 

     
        JPanel infoBox = createStatBox(Color.WHITE, "Game Information");
        lblRound = new JLabel("Round: 0 / 0");
        lblEra = new JLabel("Era : -");
        addToBox(infoBox, lblRound, lblEra);
        infoBar.add(infoBox);
        infoBar.add(Box.createRigidArea(new Dimension(0, 15))); 

      
        JPanel apexBox = createStatBox(new Color(205, 92, 92), "Apex Predator"); 
        lblApexName = new JLabel("Name : -");
        lblApexScore = new JLabel("Score : 0");
        lblApexCD = new JLabel("Ability Cooldown : 0");
       
        addToBox(apexBox, lblApexName, lblApexScore, lblApexCD);
        infoBar.add(apexBox);
        infoBar.add(Box.createRigidArea(new Dimension(0, 10)));

   
        JPanel predatorBox = createStatBox(new Color(222, 184, 135), "Predator (Player)"); 
        lblPredName = new JLabel("Name : -");
        lblPredScore = new JLabel("Score : 0");
        lblPredCD = new JLabel("Ability Cooldown : 0");
        
        addToBox(predatorBox, lblPredName, lblPredScore, lblPredCD);
        infoBar.add(predatorBox);
        infoBar.add(Box.createRigidArea(new Dimension(0, 10)));

      
        JPanel preyBox = createStatBox(new Color(176, 196, 222), "Prey"); 
        lblPreyName = new JLabel("Name : -");
        lblPreyScore = new JLabel("Score : 0");
        lblPreyCD = new JLabel("Ability Cooldown : 0");
       
        addToBox(preyBox, lblPreyName, lblPreyScore, lblPreyCD);
        infoBar.add(preyBox);
        infoBar.add(Box.createVerticalGlue());

        return infoBar;
    }

    /**
     * A UI helper that creates a styled, bordered panel
with a specific background color and title for the sidebar sections.
     * @param bg
     * @param title
     * @return
     */
    private JPanel createStatBox(Color bg, String title) {
       
    	JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(bg);
        panel.setBorder(BorderFactory.createCompoundBorder(
               
        		new LineBorder(Color.BLACK, 2),
                new EmptyBorder(10, 10, 10, 10)
        ));
        
        panel.setMaximumSize(new Dimension(230, 120));        
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Arial", Font.BOLD, 14));
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLbl);
        panel.add(new JSeparator());
       
        return panel;
    }

    /**
     * A utility method to add
multiple JLabel components to a specific sidebar panel with proper alignment and
spacing.
     * @param panel
     * @param labels
     */
    private void addToBox(JPanel panel, JLabel... labels) {
       
    	for (JLabel l : labels) {
           
    		l.setFont(new Font("Arial", Font.PLAIN, 12));
            l.setAlignmentX(Component.CENTER_ALIGNMENT);
           
            panel.add(l);
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
        
    	}
    }

    /**
     * Updating the new infos and user interfaces.
     */
    private void updateUI() {
       
    	gamePanel.repaint();
        updateSidebarInfo(); 
   
    }

    private void updateSidebarInfo() {
        
    	if (gameEngine == null) {
    		
    		return;
    	
    	}

        int normalCurrent = gameEngine.getTurnCount() + 1;
        int maxRounds = gameEngine.getMaxRounds();
        int showCurrent = Math.min(normalCurrent, maxRounds);

        lblRound.setText("Round: " + showCurrent + "/" + maxRounds);
        lblEra.setText("Era: " + gameEngine.getCurrentEra().getName());

        resetLabels();

        for (Animal a : gameEngine.getCurrentEra().getAnimals()) {
            
        	if (a instanceof ApexPredator) {
               
        		lblApexName.setText("Name: " + a.getName());
                lblApexScore.setText("Score: " + a.getScore());
                lblApexCD.setText("Ability Cooldown: " + a.getCooldown());
            }
        	
        	else if (a instanceof Predator) {
                
        		lblPredName.setText("Name: " + a.getName());
                lblPredScore.setText("Score: " + a.getScore());
                lblPredCD.setText("Ability Cooldown: " + a.getCooldown());
            } 
        	
        	else if (a instanceof Prey) {
               
        		lblPreyName.setText("Name: " + a.getName());
                lblPreyScore.setText("Score: " + a.getScore());
                lblPreyCD.setText("Ability Cooldown: " + a.getCooldown());
            }
        }
    }
    
    /**
     * Reseting to the default values.
     */
    private void resetLabels() {
        
    	lblApexName.setText("Name: (Dead)"); lblApexScore.setText("Score: -"); lblApexCD.setText("-");
        lblPredName.setText("Name: (Dead)"); lblPredScore.setText("Score: -"); lblPredCD.setText("-");
        lblPreyName.setText("Name: (Dead)"); lblPreyScore.setText("Score: -"); lblPreyCD.setText("-");
    }

    /**
     * A helper method that iterates through the animals in the current Era to
return the reference to the Predator object 
     * @return
     */
    private Animal findPlayer() {
        
    	for (Animal a : gameEngine.getCurrentEra().getAnimals()) {
            
    		if (a instanceof Predator) {
    			
    			return a;
    		
    		}
        }
       
    	return null;
    }
}