package system;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A static utility class designed to handle system logging.
 */
public class LogFile {
   
	private static final String LOG_FILE = "log.txt";
 
	/**
	 *  Accepts a string message, prints it to the console, and appends it
to log.txt with a precise timestamp.
	 * @param message
	 */
    public static void log(String message) {
        
        System.out.println(message);
              
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
           
        	String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            bw.write("[" + time + "] " + message);
            bw.newLine();
        
        }
       
        catch (IOException e) {
           
        	System.err.println("Failed to write to the log file: " + e.getMessage());
        
        }
    }
    
    /**
     * Writes a stylized visual separator and a "NEW GAME SESSION STARTED" header to the log file.
     */
    public static void startNewSession() {
       
    	log("\n==========================================");
        log("        NEW GAME SESSION STARTED           ");
        log("==========================================\n");
   
    }
}