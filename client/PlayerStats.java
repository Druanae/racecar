package client;
import java.io.Serializable;

/* PlayerStats.java
 * Class to store information that will be transmitted between clients in part 3.
 */
public class PlayerStats implements Serializable
{
	// ID for Client/Server features to be implemented in part 3.
	private static final long serialVersionUID = 3008965856884276766L;
	
	// Data relating to drawing the cars:
	public int xPos; // Current X position of car.
	public int yPos; // Current Y position of car.
	public int direction; // Direction car is facing.
	
	public final char colour; // Colour of the player (blue or red).
	
	// Score data
	public int laps;
	public boolean[] markers = { false, false, false, false };
	
	// Class constructor
	public PlayerStats(char colour, int xPos, int yPos, int direction)
	{
		this.colour = colour;
		this.xPos = xPos; // X position is middle of track.
		
		// Determine starting Y position based on which car player is.
		if(colour == 'B') {this.yPos = yPos;}  
		else {this.yPos = yPos;}
		
		this.direction = direction; // Set car to point left.
		laps = 0; // Inital lapcount.
	}
}
