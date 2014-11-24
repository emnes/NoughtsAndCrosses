
import java.io.IOException;
import java.io.PrintStream;
import java.util.Random;

import com.lloseng.ocsf.server.*;
import com.lloseng.ocsf.server.ConnectionToClient;

/**
 * This class overrides some of the methods in the abstract superclass in order
 * to give more functionality to the server.
 * 
 * @author Mazhar Shar (7987495)
 * @author Alex Dale (number)
 * @version Nov 2014
 */
public class ServerNaCG extends AbstractServer {
	// ----- CLASS VARIABLES -----\\

	// Default port to listen on.
	final public static int DEFAULT_PORT = 5555;
	public static PrintStream out = System.out;
	
	// ----- INSTANCE VARIABLES -----\\

	// ----- CONSTRUCTOR ----- \\
	public ServerNaCG(int port) {
		super(port);
	}

	/**
	 * 
	 * This method handles any messages received from the server-end
	 * user.  This handles specific commands that the user inputs and
	 * will send all other inputs to any connected user.
	 * 
	 * @param message
	 * 				The message entered by the server-end user
	 */
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) 
	{
		if(msg instanceof String){
			String message = (String) msg;
			if(message.length() == 0) return;
			if(message.charAt(0) == '#'){
				String parts[] = message.split("\\s+");
				if(parts[0].equals("#login"))
				{
					if(parts.length >= 2)
					{
						// if username does not already exist
						send("#login_fail" + " " + "Username taken", client);
						Player playerToSend = new Player(parts[1], getNumberOfClients(), 0, 0, 0); // 2nd argument is playerID. What should this be?
						try{
							client.sendToClient(playerToSend);
						}
						catch(IOException e){
							System.err.println(e.getMessage());
							send("#login_fail" + " " + e.getMessage(), client);
						}
					}
					else
					{
						System.out.println("Usage: #login [USERNAME]");
					}
				}
				else if (parts[0].equals("#search"))
				{
					search();
				}
				else if (parts[0].equals("#stop_search"))
				{
					if(isSearching())
						stopSearch();
				}
				else if (parts[0].equals("logoff"))
				{
					//Client has setLocalPlayer(). How do I access it from ConnectionToClient? 
				}
				else if (parts[0].equals("surrender"))
				{
					// client logs off/leaves game
					// update
				}
				else if (parts[0].equals("rematch"))
				{
					// if both players agree
					startGame();
				}
				else if (parts[0].equals("game_over"))
				{
					//some one has won the game - set game state
				}
				else if (parts[0].equals("#quit"))
				{
					// if player is in game surrender then quit
					// client.getInfo(playerInGame);
					try {
						client.close();
					} catch (IOException e) {
						out.println(e.getMessage());
					}
				}
			}
		}
		
	}
	
	// ----- HELPER METHODS -----\\

	protected void serverStarted() {
		while(getNumberOfClients() != 2){};
		stopSearch();
	 }

	protected void serverStopped() {
		if(getNumberOfClients() == 2)
			startGame();
	}
	
	private void send(Object msg, ConnectionToClient client) {
		try {
			client.sendToClient(msg);
		} catch (IOException e) {
			out.println("Failed to send " + msg);
		}
	}
	
	private void search()
	{
		try {
			listen();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void stopSearch()
	{
		stopListening();
	}
	
	private void startGame()
	{
		int playerToStart = (new Random()).nextInt((1 - 0) + 1); // Random number either 0 or 1
		sendToAllClients("#start_game" + " " + playerToStart);
	}
	// ----- ACCESSOR METHODS -----\\
	
	private boolean isSearching()
	{
		return isListening();
	}
}
