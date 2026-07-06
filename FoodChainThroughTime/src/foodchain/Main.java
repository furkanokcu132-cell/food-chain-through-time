package foodchain;

import javax.swing.SwingUtilities;
import screen.GameWindow;

/**
 * This class serves as the execution entry point for the "Food Chain Through Time Game" project.
 */
public class Main {
	/************** Pledge of Honor ******************************************
	I hereby certify that I have completed this programming project on my own without
	any help from anyone else. The effort in the project thus belongs completely to me.
	I did not search for a solution, or I did not consult any program written by others
	or did not copy any program from other sources. I read and followed the guidelines
	provided in the project description.
	READ AND SIGN BY WRITING YOUR NAME SURNAME AND STUDENT ID
	SIGNATURE: <Furkan Okçu, 86791>
	*************************************************************************/
	
	/**
	 * The standard Java entry method. It wraps the initialization of
the GameWindow inside SwingUtilities.invokeLater.
	 * @param args
	 */
	public static void main(String[] args) {
     
		SwingUtilities.invokeLater(() -> {
			
			new GameWindow();
		
		});
	}
}
