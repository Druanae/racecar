package server;
import java.io.Serializable;
public class PlayerStats implements Serializable
{
	private static final long serialVersionUID = 3008965856884276766L;
	public int xPos = 0;
	public int yPos = 0;
	public int direction = 0;
	public final char colour;
	public int laps = 0;
	public boolean[] markers = { false, false, false, false };
	
	public PlayerStats(char colour, int xPos, int yPos, int direction)
	{
		this.colour = colour;
		this.xPos = xPos;
		
		if(colour == 'B') {this.yPos = yPos;}  
		else {this.yPos = yPos;}
		
		this.direction = direction;
	}
	public PlayerStats(char colour)
	{
		this.colour = colour;
	}
}
