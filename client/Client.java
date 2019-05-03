package client;
/* DrawTrack.java
 * Spawns the userInterface.raceTrack
 */
// Import Libraries
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;

public class Client extends JFrame implements KeyListener
{
	// Initialise new UserInterface
	private RaceTrack raceTrack; 

    public Client() throws UnknownHostException, IOException
    {
        super("Racing Game");
        
        setTitle("Racing Game");
        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        
        raceTrack = new RaceTrack(150, 0, 850, 600, 2);
        container.add(raceTrack, BorderLayout.CENTER);
        
        addKeyListener(this);
        
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.pack();
    }
    
    @Override
    public void keyPressed( KeyEvent e )
    {
        int key = e.getKeyCode();
        
        // If 'A' key pressed, turn red car left.
        if (key == KeyEvent.VK_A)
        {
        	raceTrack.playerOne.leftPressed = true;
        }
        // If 'D' key pressed, turn red car right.
        else if (key == KeyEvent.VK_D )
        {
        	raceTrack.playerOne.rightPressed = true;
        }
    }
    public void keyReleased( KeyEvent e )
    {
        int key = e.getKeyCode();
        
        // On key release, stop turning cars.
        if (key == KeyEvent.VK_A )
        {
        	raceTrack.playerOne.leftPressed = false;
        }
        else if (key == KeyEvent.VK_D )
        {
        	raceTrack.playerOne.rightPressed = false;
        }
        
        // On key release, change speed of car.
        if ( key == KeyEvent.VK_W && raceTrack.playerOne.speed < 5 )
        {
        	raceTrack.playerOne.speed++;
        }
        else if ( key == KeyEvent.VK_S && raceTrack.playerOne.speed > 0 )
        {
        	raceTrack.playerOne.speed--;
        }
    }
    
    @Override
    public void keyTyped( KeyEvent e )
    {
    	// Do nothing.
    	return;
    }


    public static void main( String[] args )
    {
    	Thread.currentThread().setPriority((int)(Thread.MAX_PRIORITY*0.8));
        SwingUtilities.invokeLater(new Runnable()
        		{
        			@Override
        			public void run()
        			{
        				Client _track;
						try 
						{
							_track = new Client();
							_track.setVisible( true );
						} 
						catch (IOException e) 
						{
							e.printStackTrace();
						}
        			}
        		});
    	
    }
}
