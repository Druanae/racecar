package client;
import java.io.*;
import java.net.*;

public class DataHandler {
	
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	public DataHandler() throws UnknownHostException, IOException
	{
		try
		{
			this.socket = new Socket("localhost", 4000);
			this.out = new ObjectOutputStream(this.socket.getOutputStream());
			this.in = new ObjectInputStream(this.socket.getInputStream());
		}
		catch( Exception e )
		{
			System.out.println(e);
		}
	}
	
	public void sendStats(PlayerStats stats) throws IOException
	{
		try
		{
		out.writeObject(stats);
		}
		catch( Exception e )
		{
			System.out.println(e);
		}
	}
	
	public PlayerStats receiveStats() throws ClassNotFoundException, IOException
	{
		try {
			return ( (PlayerStats)in.readObject() );
		}
		catch( Exception e )
		{
			System.out.println(e);
		}
		return null;
	}
}
