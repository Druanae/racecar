package client;
/* RaceTrack.java
 * Class to draw the track, and control the movement of the cars.
 */


// Import Libraries
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.*;
import javax.swing.Timer;

/* RaceTrack.class
 * JPanel that handles logic and rendering of all visual components.
 */
public class RaceTrack extends JPanel implements ActionListener
{
    // Declare constants and variables.
	// Position and size constants:
	private final int ORIGIN; // X & Y Coordinates of where the track starts. 
    public final int WIDTH; // Width of the track.
    public final int HEIGHT; // Height of the track.
    
    private final int TRACK_WIDTH; // Width of the road. Cars are 50x50, so 150 gives them 50px to move on.
    
    private final int START_X_POS; // X position of start line
    private final int START_Y_POS; // Y position of start line
    
    private final int GRASS_START; // X & Y Coordinates of where the grass starts.
    private final int GRASS_X_POS; // Far right X position of grass.
    private final int GRASS_Y_POS; // Bottom Y position of grass.
    
    // Positioning information for the player's respective checkpoint marker.
    private final int MARKER_START; // Start position for marker.
    private final int MARKER_X_POS; // Far right X position for the marker.
    private final int MARKER_Y_POS; // Bottom Y position for the marker.
    
    private final int[] MOVEMENT_SPEED = { 0, 2, 4, 6, 8, 10 }; // Distance cars move at different speeds in pixels.
    
    private final int MAX_LAPS; // Total laps before game ends.
    
    private final int ANIMATION_DELAY = 50; // Timer update interval.
    private Timer timer = null;
    
    // JPanel that displays position information to players.
    private InfoDisplay infoDisplay;
    private boolean winStatus; // used to determine when to end the game.
    private String winner; // Name of the winning player.
    
    // Declare colour constants for drawing the track. 
    private final Color GRASS = new Color(0, 196, 0);
    private final Color ROAD = Color.lightGray;
    private final Color BOUNDARY = Color.black;
    private final Color LANE = Color.yellow;
    private final Color START = Color.white;

    // Initialise raceCar objects.
    RaceCar playerOne; // 
    RaceCar playerTwo; //
    RaceCar[] players; //
    
    // Initialise DataHandler to send/receive data from server.
    DataHandler dataHandler;

    public RaceTrack(final int trackWidth, final int origin, final int width, final int height, final int maxLaps) throws UnknownHostException, IOException
    {
    	// Track size and positioning setup:
    	// Set width of road.
    	TRACK_WIDTH = trackWidth;
    	
    	// Set track size.
    	ORIGIN = origin;
    	WIDTH = width;
    	HEIGHT = height;
    	
    	// Determine start line position.
    	START_X_POS = width / 2;
    	START_Y_POS = origin + height - TRACK_WIDTH;
    	
    	// Used to determine where the grass is to slow cars positioned on it.
    	GRASS_START = origin + TRACK_WIDTH;
    	GRASS_X_POS = width - (TRACK_WIDTH * 2);
    	GRASS_Y_POS = height - (TRACK_WIDTH * 2);
    	
    	// Create RaceCar objects.
    	playerOne = new RaceCar('R', START_X_POS - 50, START_Y_POS + (TRACK_WIDTH / 8), 4);
        playerTwo = new RaceCar('B', START_X_POS - 50, START_Y_POS + (TRACK_WIDTH / 2) + (TRACK_WIDTH / 8), 4);
        players = new RaceCar[] { playerOne, playerTwo };
    	
        // Checkpoint marker setup:
        final int markerWidth = playerOne.marker.getIconWidth();
        
    	// In part 3 will only need to assign once for the client.
        MARKER_START = origin + (TRACK_WIDTH / 2) - (markerWidth / 2);
        MARKER_X_POS = width - (TRACK_WIDTH / 2) - (markerWidth / 2);
        MARKER_Y_POS = height - (TRACK_WIDTH / 2) - (markerWidth / 2);
        
        // Misc Setup:
        winStatus = false;
        MAX_LAPS = maxLaps;
        
        // Initialise InfoPanel
        infoDisplay = new InfoDisplay(this, 15, GRASS_START, WIDTH, HEIGHT, TRACK_WIDTH);
        
        // JPanel setup:
        this.setLayout(new BorderLayout());
        setVisible(true);
        
        // Initialise timer to drive animation:
     	if ( timer == null )
     	{
     		timer = new Timer( ANIMATION_DELAY, this );
     	}
     	timer.start();
     	
     	this.dataHandler = new DataHandler();
    }
    
    // Public method to change the car's direction
    private void turnCar ( String direction, RaceCar car )
    {
    	if ( direction == "left" ) // Reduce curDir by 1
    	{
    		if ( car.stats.direction == 0 ) { car.stats.direction = 15; }
    		else { car.stats.direction -= 1; }
    	} 
    	else if ( direction == "right" ) // Increase curDir by 1
    	{
    		if ( car.stats.direction == 15 ) { car.stats.direction = 0; }
    		else {car.stats.direction +=1;}
    	}    
    }
    
    private void turnCheck(RaceCar car) 
    {
    	// Check if the left or right key is pressed, then turn redCar accordingly
    	if(car.leftPressed == true && car.rightPressed == false)
    	{
    		turnCar("left", car);
    	}
    	else if(car.leftPressed == false && car.rightPressed == true)
    	{
    		turnCar("right", car);
    	}
    }
    
 // Checks if the cars are touching the edge of the screen.
    private void boundsCheck(RaceCar car)
    {
    	// Check if car is out of bounds on the X axis.    	
    	if ( car.stats.xPos < 0 )
    	{
    		car.speed = 0;
    		car.stats.xPos = 0;
    	}
    	else if (car.stats.xPos > WIDTH - car.carWidth)
    	{
    		car.speed = 0;
    		car.stats.xPos = WIDTH - car.carWidth;
    	}
    	// Check if car is out of bounds on the Y axis.
    	if ( car.stats.yPos < 0 )
    	{
    		car.speed = 0;
    		car.stats.yPos = 0;
    	}
    	else if ( car.stats.yPos > ( HEIGHT - car.carHeight ) ) 
    	{
    		car.speed = 0;
    		car.stats.yPos = ( HEIGHT - car.carHeight );
    	}
    }
    
    private boolean hitCheck(int x, int y, RaceCar car)
    {
    	return ( x > car.stats.xPos && x < (car.stats.xPos + car.carWidth) ) &&
    			( y > car.stats.yPos && y < (car.stats.yPos + car .carHeight));
    } 
    
    private boolean collisionCheck(RaceCar playerOne, RaceCar playerTwo)
    {
    	return  hitCheck(playerOne.stats.xPos, playerOne.stats.yPos, playerTwo) ||
    			hitCheck((playerOne.stats.xPos + playerOne.carWidth), playerOne.stats.yPos, playerTwo) ||
    			hitCheck(playerOne.stats.xPos, (playerOne.stats.yPos + playerOne.carHeight), playerTwo) ||
    			hitCheck((playerOne.stats.xPos + playerOne.carWidth),
    					 (playerOne.stats.yPos + playerOne.carHeight), playerTwo);
    }
    
    private void onHit(RaceCar car)
    {
    	car.stats.xPos = car.xPrev;
		car.stats.yPos = car.yPrev;
		car.speed = 0;
    }
    
    public boolean offRoad(RaceCar car)
    {
    	if (( car.stats.xPos > (GRASS_START - ORIGIN ) && car.stats.xPos < (GRASS_START + GRASS_X_POS) - (car.carWidth / 2)) && 
    			(car.stats.yPos > (GRASS_START - ORIGIN) && car.stats.yPos < (GRASS_START + GRASS_Y_POS) - (car.carHeight / 2)))
    	{
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }
    
    private void markerCheck(RaceCar car)
    {
    	// Markers 1 & 2
    	if ( car.stats.xPos > MARKER_X_POS - (TRACK_WIDTH / 2) && car.stats.xPos < MARKER_X_POS + (TRACK_WIDTH / 2) )
    	{
    		if ( ! car.stats.markers[0] && ( car.stats.yPos > MARKER_Y_POS - (TRACK_WIDTH / 2) &&
    				car.stats.yPos < MARKER_Y_POS + (TRACK_WIDTH / 2) ) )
    		{
    			car.stats.markers[0] = true;
    		}
    		else if ( (car.stats.markers[0] && ! car.stats.markers[1] ) &&
    				(car.stats.yPos > MARKER_START - (TRACK_WIDTH / 2) && car.stats.yPos < MARKER_START + (TRACK_WIDTH / 2) ) )
    		{
    			car.stats.markers[1] = true;
    		}
    	}
    	// Markers 3 & 4
    	else if ( car.stats.xPos > MARKER_START - (TRACK_WIDTH / 2) && car.stats.xPos < MARKER_START + (TRACK_WIDTH / 2) )
    	{
    		if ( ( car.stats.markers[1] && ! car.stats.markers[2] ) &&
    				car.stats.yPos > MARKER_START - (TRACK_WIDTH / 2) && car.stats.yPos < MARKER_START + (TRACK_WIDTH / 2) )
    		{
    			car.stats.markers[2] = true;
    		}
    		else if ( ( car.stats.markers[2] && ! car.stats.markers[3] ) && 
    				( car.stats.yPos > MARKER_Y_POS - (TRACK_WIDTH / 2) && car.stats.yPos < MARKER_Y_POS + (TRACK_WIDTH /2) ) )
    		{
    			car.stats.markers[3] = true;
    		}
    			
    	}
    	// Finish Line Marker
    	else if ( car.stats.markers[0] && car.stats.markers[1] && car.stats.markers[2] && car.stats.markers[3] )
    	{
    		if ( car.stats.xPos > START_X_POS - (car.carWidth / 2) && ( car.stats.yPos >= START_Y_POS - (car.carHeight / 2) && car.stats.yPos <= START_Y_POS + TRACK_WIDTH ) )
    		{
    			for ( int i = 0; i < car.stats.markers.length; i++ )
    			{
    				car.stats.markers[i] = false;
    			}
    			car.stats.laps++;
    		}
    	}
    }

    private void moveCar(RaceCar car)
    {
    	// Determine no. of pixels to move car by
    	int moveSpeed = MOVEMENT_SPEED[car.speed];
    	if ( offRoad(car) )
    	{
    		moveSpeed /= 2;
    	}
    	
    	// Move car based on direction facing.
    	if ( car.stats.direction == 0 )
    	{
    		// Update previous position before moving.
    		car.yPrev = car.stats.yPos;
    		
    		// Update to new position.
    		car.stats.yPos -= moveSpeed;
    	}
    	else if ( car.stats.direction == 1 )
    	{
    		// Update previous position before moving.
    		car.yPrev = car.stats.yPos;
    		car.xPrev = car.stats.xPos;
    		
    		// Update to new position.
    		car.stats.xPos += moveSpeed / 2;
    		car.stats.yPos -= moveSpeed;
    	}
    	else if ( car.stats.direction == 2 )
    	{
    		// Update previous position before moving.
    		car.yPrev = car.stats.yPos;
    		car.xPrev = car.stats.xPos;
    		
    		// Update to new position.
    		car.stats.xPos += moveSpeed;
    		car.stats.yPos -= moveSpeed;
    	}
    	else if ( car.stats.direction == 3 )
    	{
    		// Update previous position before moving.
    		car.yPrev = car.stats.yPos;
    		car.xPrev = car.stats.xPos;
    		
    		// Update to new position.
    		car.stats.xPos += moveSpeed;
    		car.stats.yPos -= moveSpeed / 2;
    	}
    	else if ( car.stats.direction == 4 )
    	{
    		// Update previous position before moving.
    		car.xPrev = car.stats.xPos;
    		
    		// Update to new position.
    		car.stats.xPos += moveSpeed;
    	}
    	else if ( car.stats.direction == 5 )
    	{
    		// Update previous position before moving.
    		car.yPrev = car.stats.yPos;
    		car.xPrev = car.stats.xPos;
    		
    		// Update to new position.
    		car.stats.xPos += moveSpeed;
    		car.stats.yPos += moveSpeed / 2;
    	}
    	else if ( car.stats.direction == 6 )
    	{
    		// Update previous position before moving.
    		car.yPrev = car.stats.yPos;
    		car.xPrev = car.stats.xPos;
    		
    		// Update to new position.
    		car.stats.xPos += moveSpeed;
    		car.stats.yPos += moveSpeed;
    	}
    	else if ( car.stats.direction == 7 )
    	{
    		// Update previous position before moving.
    		car.yPrev = car.stats.yPos;
    		car.xPrev = car.stats.xPos;
    		
    		// Update to new position.
    		car.stats.xPos += moveSpeed / 2;
    		car.stats.yPos += moveSpeed;
    	}
    	else if ( car.stats.direction == 8 )
    	{
    		// Update previous position before moving.
    		car.yPrev = car.stats.yPos;
    		
    		// Update to new position.
    		car.stats.yPos += moveSpeed;
    	}
    	else if ( car.stats.direction == 9 )
    	{
    		// Update previous position before moving.
    		car.yPrev = car.stats.yPos;
    		car.xPrev = car.stats.xPos;
    		
    		// Update to new position.
    		car.stats.xPos -= moveSpeed / 2;
    		car.stats.yPos += moveSpeed;
    	}
    	else if ( car.stats.direction == 10 )
    	{
    		// Update previous position before moving.
    		car.yPrev = car.stats.yPos;
    		car.xPrev = car.stats.xPos;
    		
    		// Update to new position.
    		car.stats.xPos -= moveSpeed;
    		car.stats.yPos += moveSpeed;
    	}
    	else if ( car.stats.direction == 11 )
    	{
    		// Update previous position before moving.
    		car.yPrev = car.stats.yPos;
    		car.xPrev = car.stats.xPos;
    		
    		// Update to new position.
    		car.stats.xPos -= moveSpeed;
    		car.stats.yPos += moveSpeed / 2;
    	}
    	else if ( car.stats.direction == 12 )
    	{
    		// Update previous position before moving.
    		car.xPrev = car.stats.xPos;
    		
    		// Update to new position.
    		car.stats.xPos -= moveSpeed;
    	}
    	else if ( car.stats.direction == 13 )
    	{
    		// Update previous position before moving.
    		car.yPrev = car.stats.yPos;
    		car.xPrev = car.stats.xPos;
    		
    		// Update to new position.
    		car.stats.xPos -= moveSpeed;
    		car.stats.yPos -= moveSpeed / 2;
    	}
    	else if ( car.stats.direction == 14 )
    	{
    		// Update previous position before moving.
    		car.yPrev = car.stats.yPos;
    		car.xPrev = car.stats.xPos;
    		
    		// Update to new position.
    		car.stats.xPos -= moveSpeed;
    		car.stats.yPos -= moveSpeed;
    	}
    	else if ( car.stats.direction == 15 )
    	{
    		// Update previous position before moving.
    		car.yPrev = car.stats.yPos;
    		car.xPrev = car.stats.xPos;
    		
    		// Update to new position.
    		car.stats.xPos -= moveSpeed / 2;
    		car.stats.yPos -= moveSpeed;
    	}
    }
    
    
    @Override
    public Dimension getMinimumSize()
    { 
    	return getPreferredSize(); 
    }
    
    @Override
    public Dimension getPreferredSize() 
    { 
    	return new Dimension( WIDTH, HEIGHT ); 
    }
	
    public void drawTrack( Graphics g )
    {
    	// Draw track
    	g.setColor( ROAD );
    	g.fillRect( ORIGIN, ORIGIN, WIDTH, HEIGHT );
    	
    	// Draw grass
    	g.setColor( GRASS );
    	g.fillRect( GRASS_START, GRASS_START, GRASS_X_POS, GRASS_Y_POS );
    	
    	// Draw centre road marking.
    	g.setColor( LANE );
    	g.drawRect(ORIGIN + (TRACK_WIDTH / 2), ORIGIN + (TRACK_WIDTH / 2), WIDTH - TRACK_WIDTH, HEIGHT - TRACK_WIDTH);
    	
    	// Draw start line
    	g.setColor( START );
    	g.drawLine( START_X_POS, START_Y_POS, START_X_POS, START_Y_POS + TRACK_WIDTH );
    	
    	// Draw track boundaries.
    	g.setColor( BOUNDARY );
    	g.drawRect( ORIGIN, ORIGIN, WIDTH, HEIGHT );
    	g.drawRect( ORIGIN + TRACK_WIDTH, ORIGIN + TRACK_WIDTH, WIDTH - (TRACK_WIDTH * 2), HEIGHT - (TRACK_WIDTH * 2) );
    }
    
    public void drawMarker ( Component panel, Graphics g, RaceCar car)
    {
    	if ( ! car.stats.markers[0])
    	{
    		car.marker.paintIcon(panel, g, MARKER_X_POS, MARKER_Y_POS);
    	}
    	else if ( ! car.stats.markers[1] )
    	{
    		car.marker.paintIcon(panel, g, MARKER_X_POS, MARKER_START);
    	}
    	else if ( ! car.stats.markers[2] )
    	{
    		car.marker.paintIcon(panel, g, MARKER_START, MARKER_START);
    	}
    	else if ( ! car.stats.markers[3] )
    	{
    		car.marker.paintIcon(panel, g, MARKER_START, MARKER_Y_POS);
    	}
    	
    	else
    	{
    		car.marker.paintIcon(panel, g, START_X_POS - (car.marker.getIconWidth() / 2), MARKER_Y_POS);
    	}
    }
    
    public void drawCar( Graphics g, RaceCar car )
    {
    	car.carImages[ car.stats.direction ].paintIcon(this, g, car.stats.xPos, car.stats.yPos);
    }
    
    public void movementHandle(RaceCar carOne, RaceCar carTwo)
    {
    	turnCheck(carOne);
    	boundsCheck(carOne);
    	markerCheck(carOne);
    	if( ! collisionCheck(carOne, carTwo) )
    	{
    		moveCar(carOne);
    	}
    	else
    	{
    		onHit(carOne);
    	}
    }
    
    // returns index of 
    private static int markerIndex(boolean value, boolean[] target)
    {
    	for ( int i = 0; i < target.length; i++ )
    	{
    		if ( target[i] == value )
    		{
    			return i;
    		}
    	}
    	// return the index of the last element in the array.
    	return target.length - 1;
    }
    
    // Using Pythagoras theorem we can calculate which player is closest to the next checkpoint.
    public boolean checkPlayerDistance(int markerIndex, RaceCar playerOne, RaceCar playerTwo )
    {
    	// Initialise floats to store variables.
    	float playerOneDist = 0.f;
    	float playerTwoDist = 0.f;
    	float xPos = 0.f;
    	float yPos = 0.f;
    	float xDiff = 0.f;
    	float yDiff = 0.f;
    	
    	// Get the X and Y positions of the target marker.
    	if ( markerIndex == 0 )
    	{
    		xPos = MARKER_X_POS;
    		yPos = MARKER_Y_POS;
    	}
    	else if ( markerIndex == 1 )
    	{
    		xPos = MARKER_X_POS;
    		yPos = MARKER_START;
    	}
    	else if ( markerIndex == 1 )
    	{
    		xPos = MARKER_START;
    		yPos = MARKER_START;
    	}
    	else if ( markerIndex == 1 )
    	{
    		xPos = MARKER_START;
    		yPos = MARKER_Y_POS;
    	}
    	else
    	{
    		xPos = START_X_POS - (playerOne.marker.getIconWidth() / 2);
    		yPos = START_Y_POS - (playerOne.marker.getIconHeight() / 2);
    	}
    	
    	// Calculate distance between playerOne and marker.
    	// Get the difference between the car and marker's x position.
    	xDiff = playerOne.stats.xPos - xPos;
    	if ( xDiff < 0 ) { xDiff += (xDiff * -2); } // if value is negative, set to positive.
    	
    	// Get the difference between the car and marker's y positions.
    	yDiff = playerOne.stats.yPos - yPos;
    	if ( yDiff < 0 ) { yDiff += (yDiff * -2); } // if value is negative, set to positive.
    	// c² = a² + b²
    	playerOneDist = (float)Math.sqrt((xDiff * xDiff) + (yDiff * yDiff));
		
    	//Calculate distance between playerTwo and marker.
    	// Get the difference between the car and marker's x position.
    	xDiff = playerTwo.stats.xPos - xPos;
    	if ( xDiff < 0 ) { xDiff += (xDiff * -2); } // if value is negative, set to positive.
    	// Get the difference between the car and marker's y positions.
    	yDiff = playerOne.stats.yPos;
    	if ( yDiff < 0 ) { yDiff += (yDiff * -2); } // if value is negative, set to positive.
    	// c² = a² + b²
    	playerTwoDist = (float)Math.sqrt((xDiff * xDiff) + (yDiff * yDiff));
    	
    	return (playerOneDist < playerTwoDist);
    }
    
    private void setAhead(RaceCar playerOne, RaceCar playerTwo)
    {
    	if ( playerOne.stats.laps > playerTwo.stats.laps )
    	{
    		players[0] = playerOne;
    		players[1] = playerTwo;
    		return;
    	}
    	else if ( playerOne.stats.laps < playerTwo.stats.laps )
    	{
    		players[0] = playerTwo;
    		players[1] = playerOne;
    		return;
    	}
    	
    	int playerOneMarker = markerIndex(false, playerOne.stats.markers);
    	int playerTwoMarker = markerIndex(false, playerTwo.stats.markers);
    	
    	if ( playerOneMarker > playerTwoMarker )
    	{
    		players[0] = playerOne;
    		players[1] = playerTwo;
    		return;
    	}
    	else if ( playerOneMarker < playerTwoMarker )
    	{
    		players[0] = playerTwo;
    		players[1] = playerOne;
    		return;
    	}
    	
    	else
    	{
    		if ( checkPlayerDistance(playerOneMarker, playerOne, playerTwo) )
    		{
        		players[0] = playerOne;
        		players[1] = playerOne;
    		}
    		else
    		{
        		players[0] = playerTwo;
        		players[1] = playerOne;
    		}
    	}
    	
    }
    
	private boolean checkWinner(RaceCar playerOne, RaceCar playerTwo)
	{
		if ( playerOne.stats.laps == MAX_LAPS && playerTwo.stats.laps < MAX_LAPS )
		{
			winStatus = true;
			winner = playerOne.colour;
			return true;
		}
		else if ( playerOne.stats.laps < MAX_LAPS && playerTwo.stats.laps == MAX_LAPS )
		{
			winStatus = true;
			winner = playerTwo.colour;
			return true;
		}
		else { return false; }
	}
    
	public void sendData(RaceCar player) throws IOException
	{
		dataHandler.sendStats(player.stats);
	}
	
	@Override
	public void paintComponent( Graphics g )
	{
		super.paintComponent(g);
		drawTrack(g);
		drawMarker(this, g, playerOne);
		drawCar(g, playerOne);
		drawCar(g, playerTwo);
	}
	
	@Override
	public void actionPerformed( ActionEvent arg0 )
	{
		if (checkWinner(playerOne, playerTwo))
		{
			infoDisplay.displayGameOver(winStatus, winner);
			playerOne.speed = 0;
		}
		else
		{
			try 
			{
				sendData(playerOne); // sends player one's PlayerStats 
				PlayerStats newStats = dataHandler.receiveStats();
				
				
				playerTwo.stats.xPos = newStats.xPos;
				playerTwo.stats.yPos = newStats.yPos;
				playerTwo.stats.direction = newStats.direction;
				playerTwo.stats.laps = newStats.laps;
				playerTwo.stats.markers = newStats.markers;
				
				
			} catch ( IOException | ClassNotFoundException e) 
			{
				e.printStackTrace();
			}
			setAhead(playerOne, playerTwo);
			movementHandle( playerOne, playerTwo );
		}
		

		
		
		infoDisplay.displayLaps(playerOne, playerTwo, MAX_LAPS);
		infoDisplay.displayPositions(players);
		repaint();
	}

}
