package client;
/* RaceCar.java
 * Class to store information about racecar objects.
 */


// Import Libraries
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class RaceCar
{
    // Private constants.
    private final int TOTAL_DIRECTIONS = 16; // Total number of images.
    public final int carWidth, carHeight; // width & height of the images in px.

    // Declare public variables.
    // Stores current direction the car is facing, i.e. the current image to display
    public int speed = 0; // Stores current speed of the car.
    public int xPrev = 0; // previous horizontal position.
    public int yPrev = 0; // previous vertical position.
    public String colour; // Name of the car. Determines file names to load.
    public boolean leftPressed = false; // is left key pressed?
    public boolean rightPressed = false; // is right key pressed?
    public boolean onGrass; // Used to slow the car if it's on the grass.
    
    // Images associated with car
    public ImageIcon carImages[]; // Array to store images.
    public ImageIcon marker;
        
    // Initialise PlayerStats object to hold data to send between clients.
    public PlayerStats stats;

    // Class constructor.
    public RaceCar(final char colour, final int xStart, final int yStart, final int startDir)
    {
        // Initialise a new imageIcon array to store the images.
        carImages = new ImageIcon [ TOTAL_DIRECTIONS ];

        String carName = "RED";
        if ( colour == 'R' )
        {
        	carName = "RED";
        }
        else        
        {
        	carName = "BLUE";
        }
        
        // Load sprite images into sprite array.
        for ( int count = 0; count < carImages.length; count++ )
        {
            carImages[ count ] = new ImageIcon(
                    getClass().getResource("./sprites/" + carName + count +".png"));
        }

        // Set image width & height.
        carWidth = carImages[0].getIconWidth();
        carHeight = carImages[0].getIconHeight();
        
        // Set checkpoint marker.
        marker = new ImageIcon(getClass().getResource("./sprites/" + carName + "Flag.png"));

        // Set car colour.
        this.colour = carName;
        
        // Set initial car position.
        xPrev = xStart;
        yPrev = yStart;
        
        onGrass = false;
        
        // Initialise player stats with default values.
        stats = new PlayerStats(colour, xStart, yStart, startDir);
    }
}
