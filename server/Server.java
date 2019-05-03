package server;

import java.net.*;
import java.io.*;

/* Server Class
 * Starts a server that listens for players on port 4000;
 */
public class Server {
	
	/* Player class
	 * SubClass of Server
	 * Handles the recieving and sending of data.
	 */
	protected class Player extends Thread
	{
		private char colour;
		private Player playerTwo;
		private PlayerStats stats;
		
		private Socket socket;
		private ObjectOutputStream out;
		private ObjectInputStream in;
		
		PrintWriter textOut;
		
		protected Player(Socket socket, char colour)
		{
			this.socket = socket;
			this.colour = colour;
			this.stats = new PlayerStats(this.colour);
			try
			{
				out = new ObjectOutputStream(this.socket.getOutputStream());
				in = new ObjectInputStream(this.socket.getInputStream());
				
				textOut = new PrintWriter(this.socket.getOutputStream(), true);
				textOut.println("Player: " + this.colour);
				textOut.println("Waiting for Second Player To Connect...");
			}
			catch ( IOException e )
			{
				System.out.println(e);
			}
		}

		public void setPlayerTwo(Player playerTwo)
		{
			this.playerTwo = playerTwo;			
		}
		
		public synchronized void run()
		{
			try
			{
				System.out.println("Both Players Present");
				System.out.println("Starting Game.");
				
				while(true)
				{
					// send player two data
					out.writeUnshared(playerTwo.stats);
					// receive player one data
					stats = (PlayerStats) in.readObject();
				}
			}
			catch(IOException e)
			{
				System.out.println(e);
			}
			catch(ClassNotFoundException e)
			{
				System.out.println(e);
			}
		}
		
	}
	
	// Server port to use.
	public static final int PORT = 4000;
		
	public static void main(String[] args) throws IOException
	{
		new Server().runServer();
	}
	
	public void runServer() throws IOException
	{
		ServerSocket serverSocket = new ServerSocket(PORT);
		System.out.println("Server Up");
		
		try
		{
			Player redPlayer = new Player(serverSocket.accept(), 'R');
			Player bluePlayer = new Player(serverSocket.accept(), 'B');
			
			redPlayer.setPlayerTwo(bluePlayer);
			bluePlayer.setPlayerTwo(redPlayer);
			
			redPlayer.start();
			bluePlayer.start();
		}
		finally
		{
			serverSocket.close();
		}
	}
}
