package com.mycompany.coit20258assignment2;

/**
 * Launcher class for the THS-Enhanced application.
 * This class is used to launch the JavaFX application from a JAR file.
 * 
 * JavaFX requires a non-modular launcher when running from a shaded JAR.
 * This class simply calls the main method of the App class.
 * 
 * @author Group 6
 * @version 3.0
 */
public class Launcher {
    
    /**
     * Main entry point for the application.
     * Launches the JavaFX App class.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        // Launch the JavaFX application
        App.main(args);
    }
}
