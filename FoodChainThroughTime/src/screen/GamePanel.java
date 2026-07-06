package screen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter; 
import java.awt.event.MouseEvent;   
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * GamePanel represents the main menu and configuration screen of the application.
 */
public class GamePanel extends JPanel {
   
	private GameWindow mainFrame;
    private JComboBox<String> comboGridSize; 
    private JSpinner spinRounds;
    private BufferedImage backgroundImage;

    /**
     * The constructor initializes the menu interface
     * @param frame
     */
    public GamePanel(GameWindow frame) {
       
    	this.mainFrame = frame;
        loadBackgroundImage();
        
        this.setLayout(new GridBagLayout()); 
        this.setBackground(Color.BLACK); 

        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER; 
        gbc.gridy = 0;
       
        this.add(Box.createVerticalStrut(210), gbc);

        String[] gridOptions = {"10x10", "15x15", "20x20"};
        comboGridSize = new JComboBox<>(gridOptions);
        ((JLabel)comboGridSize.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
        comboGridSize.setPreferredSize(new Dimension(80, 25));
        comboGridSize.setSelectedIndex(1); 

        JPanel pnlGrid = OvalPanelCreating("Grid Size", comboGridSize);
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 5, 0); 
        this.add(pnlGrid, gbc);
      
        spinRounds = new JSpinner(new SpinnerNumberModel(20, 10, 100, 1)); //Default 20, max 100, min 10
        spinRounds.setPreferredSize(new Dimension(60, 25));
        JComponent editor = spinRounds.getEditor();
       
        if (editor instanceof JSpinner.DefaultEditor) {
           
        	((JSpinner.DefaultEditor) editor).getTextField().setHorizontalAlignment(JTextField.CENTER);
       
        }

        JPanel pnlRounds = OvalPanelCreating("Total Rounds", spinRounds);
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 0, 20, 0); 
        this.add(pnlRounds, gbc);
 
        JLabel lblSelect = new JLabel("<html><span style='text-shadow: 1px 1px #000000;'>Select Game Mode (Era)</span></html>");
        lblSelect.setForeground(Color.WHITE);
        lblSelect.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 0, 10, 0); 
        this.add(lblSelect, gbc);

        Dimension buttonSize = new Dimension(220, 45);

        RoundedButton buttonPast = new RoundedButton("Past Era", new Color(139, 69, 19));    
        buttonPast.setPreferredSize(buttonSize);
        
        RoundedButton buttonPresent = new RoundedButton("Present Era", new Color(34, 139, 34)); 
        buttonPresent.setPreferredSize(buttonSize);
        
        RoundedButton buttonFuture = new RoundedButton("Future Era", new Color(70, 130, 180));  
        buttonFuture.setPreferredSize(buttonSize);
        
        RoundedButton buttonLoad = new RoundedButton("Load Saved Game", new Color(80, 80, 80)); 
        buttonLoad.setPreferredSize(buttonSize);


        buttonPast.addActionListener(e -> startGame("past.txt"));
        buttonPresent.addActionListener(e -> startGame("present.txt"));
        buttonFuture.addActionListener(e -> startGame("future.txt"));
        
        buttonLoad.addActionListener(e -> {
          
        	int rounds = (int) spinRounds.getValue();
            mainFrame.startGame("saved_game.txt", 0, rounds);
      
        });

        gbc.insets = new Insets(5, 0, 5, 0); 
        gbc.gridy = 4; this.add(buttonPast, gbc);
        gbc.gridy = 5; this.add(buttonPresent, gbc);
        gbc.gridy = 6; this.add(buttonFuture, gbc);
  
        JSeparator sepBot = new JSeparator();
        sepBot.setForeground(new Color(200, 200, 200, 100));
        sepBot.setPreferredSize(new Dimension(150, 1));
        gbc.gridy = 7;
        gbc.insets = new Insets(15, 0, 15, 0);
        this.add(sepBot, gbc);

        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, 20, 0); 
        this.add(buttonLoad, gbc);
   
    }
    
    /**
     * A helper method designed to encapsulate input fields.
     * @param labelText
     * @param inputComponent
     * @return
     */
    private JPanel OvalPanelCreating(String labelText, JComponent inputComponent) {
      
    	RoundedPanel panel = new RoundedPanel(40); 
        panel.setBackground(new Color(0, 0, 0, 180)); 
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 8)); 
        
        JLabel lbl = new JLabel(labelText);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        panel.add(lbl);
        panel.add(inputComponent);
        return panel;
    }
    
    /**
     * Handles the I/O operation to read the menu_bg.png file from
the disk.
     */
    private void loadBackgroundImage() {
        
    	try {
           
    		backgroundImage = ImageIO.read(new File("images/menu_bg.png"));
        } 
    	
    	catch (IOException e) {}
    }

    /**
     *Overrides the standard painting mechanism to draw the
loaded backgroundImageacross the entire panel
     */
    protected void paintComponent(Graphics g) {
      
    	super.paintComponent(g);
       
    	if (backgroundImage != null) {
           
    		g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    /**
     * Collects the user's configuration choices (Grid Size and
Round count) from the UI components.
     * @param filename
     */
    private void startGame(String filename) {
       
    	String selectedSize = (String) comboGridSize.getSelectedItem(); 
        String sizeValue = selectedSize.split("x")[0]; 
       
        int gridSize = Integer.parseInt(sizeValue);         
        int rounds = (int) spinRounds.getValue();
        
        mainFrame.startGame(filename, gridSize, rounds);
    }

    
     /**
      * A custom Swing
component that draws a semi-transparent background with rounded corners and a white
border
      */
     
    class RoundedPanel extends JPanel {
       
    	private int radius;
       
    	public RoundedPanel(int radius) {
           
    		this.radius = radius;
            setOpaque(false);
        }
    
    	
        protected void paintComponent(Graphics g) {
           
        	Graphics2D dim2 = (Graphics2D) g;
            dim2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            dim2.setColor(getBackground());
            dim2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            dim2.setColor(new Color(255, 255, 255, 50));
            dim2.setStroke(new BasicStroke(1));
            dim2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
           
            super.paintComponent(g);
       
        }
    }
 
    /**
     * A custom button implementation.
     */
    class RoundedButton extends JButton {
       
    	private Color baseColor;
        private Color originalColor; 

        public RoundedButton(String text, Color color) {
         
        	super(text);
            this.baseColor = color;
            this.originalColor = color;
            
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 15));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setText("<html><span style='text-shadow: 1px 1px #000000;'>" + text + "</span></html>");
          
            addMouseListener(new MouseAdapter() {
               
                public void EnterMouse(MouseEvent e) {
                  
                	baseColor = originalColor.brighter(); 
                    repaint();
               
                }
                
                public void ExitMouse(MouseEvent e) {
                   
                	baseColor = originalColor; 
                    repaint();
                }
            });
        }
       
        protected void paintComponent(Graphics g) {
          
        	Graphics2D dim2 = (Graphics2D) g.create();
            dim2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            dim2.setColor(baseColor);
            dim2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
            
            dim2.setColor(new Color(255,255,255,100));
            dim2.setStroke(new BasicStroke(2));
            dim2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 40, 40);

            super.paintComponent(g); 
            dim2.dispose();
        }
    }
}