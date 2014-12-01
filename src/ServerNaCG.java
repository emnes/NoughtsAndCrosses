import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract superclass in order
 * to give more functionality to the server.
 * 
 * @author Mazhar Shar (7987495)
 * @author Alex Dale (6839825)
 * @version Nov 2014
 */
public class ServerNaCG extends AbstractServer {
	
	private static int lastID = 0;
	private static int getId() {
		return ++lastID;
	}
	
	// ----- CLASS VARIABLES -----\\

	// Default port to listen on.
	final public static int DEFAULT_PORT = 5555;
	public static PrintStream out = System.out;
	public static Scanner in = new Scanner(System.in);

	// ----- INSTANCE VARIABLES -----\\
	
	HashMap<ConnectionToClient, Player> connectionPlayer;
	HashMap<ConnectionToClient, ConnectionToClient> matchedPlayers;
	HashMap<String, Player> existingPlayers;
	Deque<ConnectionToClient> enqueuedPlayers;
	Deque<ConnectionToClient> enqueuedForRematch;
	Deque<String> usedNames;
	
	
	// ----- CONSTRUCTOR ----- \\
	public ServerNaCG(int port) {
		super(port);
		connectionPlayer = new HashMap<ConnectionToClient, Player>();
		matchedPlayers = new HashMap<ConnectionToClient, ConnectionToClient>();
		enqueuedPlayers = new LinkedList<ConnectionToClient>();
		enqueuedForRematch = new LinkedList<ConnectionToClient>();
		usedNames = new LinkedList<String>();
		existingPlayers = new HashMap<String, Player>();
		out.println("Noughts and Crosses server running");
	}

	/**
	 * 
	 * This method handles any messages received from the server-end user. This
	 * handles specific commands that the user inputs and will send all other
	 * inputs to any connected user.
	 * 
	 * @param message
	 *            The message entered by the server-end user
	 */
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		if (msg instanceof String) {
			String message = (String) msg;
			printMessage(message, client);
			if (message.length() == 0)
				return;
			if (message.charAt(0) == '#') {
				String parts[] = message.split("\\s+");
				if (parts[0].equals("#login")) {
					if (parts.length >= 2) {
						login(parts[1], client);				
					} else {
						send("#login_fail no_username", client);
					}
				} else if (!isLoggedIn(client) && parts[0].equals("#quit")) {
					try {
						client.close();
					} catch (IOException e) {
						out.println(e.getMessage());
					}
				} else if (!isLoggedIn(client)) {
					send("#not_logged_in", client);
					
				} else if (parts[0].equals("#search")) {
					search(client);
					
				} else if (parts[0].equals("#stop_search")) {
					if (enqueuedPlayers.contains(client)) stopSearch(client);
					
				} else if (parts[0].equals("#logoff")) {
					
					//TODO
					
				} else if (parts[0].equals("#surrender")) {
					unmatchPlayer(client);
					
				} else if (parts[0].equals("#end")) {
					unmatchPlayer(client);
					
				} else if (parts[0].equals("#rematch")) {
					rematch(client);
					
				} else if (parts[0].equals("#game_over")) {
					gameOver(client);
					
				} else if (parts[0].equals("#quit")) {
					playerQuit(client);
					try {
						client.close();
					} catch (IOException e) {
						out.println(e.getMessage());
					}
				}
			}
		}
		else if (msg instanceof Player) {
			updatePlayer((Player) msg, client);
		} else if (msg instanceof Action) {
			passAction((Action) msg, client);
		}

	}

	// ----- HELPER METHODS -----\\
	
	/**
	 * Sets up the clients connection when they first connect.
	 */
	protected void clientConnected(ConnectionToClient client) {
		try {
			client.forceResetAfterSend();
			out.println("Someone Connected!!");
		} catch (IOException e) {
			out.println("Could not force!");
		}
	}
	
	/**
	 * Properly disconnects the a user from any games and logs them off.
	 */
	protected void clientDisconnected(ConnectionToClient client) {
		if (client != null && isLoggedIn(client)) {
			playerQuit(client);
		}
	}
	
	/**
	 * Properly disconnects the a user from any games and logs them off in
	 * the event of a connection error.
	 */
	protected void clientException(ConnectionToClient client, Throwable t) {
		if (client != null && isLoggedIn(client)) {
			playerQuit(client);
		}
	}
	
	/**Sends a message to the specified client
	 * 
	 * @param msg
	 * @param client
	 */
	private void send(Object msg, ConnectionToClient client) {
		try {
			client.sendToClient(msg);
			printSent(msg, client);
		} catch (IOException e) {
			out.println("Failed to send " + msg);
		}
	}
	
	/**Will print out all outgoing messages to the server console.  Specifiying
	 * who will recieve it.
	 * 
	 * @param msg
	 * @param client
	 */	
	private void printSent(Object msg, ConnectionToClient client) {
		if (connectionPlayer.containsKey(client)) {
			Player p = connectionPlayer.get(client);
			out.println("To " + p.getName() + "> " + msg);
		} else {
			out.println("To UNKNOWN> " + msg);
		}
	}
	
	/**Will log in a user based on their input user name and sets up all the necessary
	 * server data around that client.
	 * 
	 * @param name
	 * @param client
	 */
	private void login(String name, ConnectionToClient client) {
		if (usedNames.contains(name)) {
			send("#login_fail " + "username_taken", client);
		} else if (existingPlayers.containsKey(name)) {
			Player aOldPlayer = existingPlayers.get(name);
			usedNames.add(name);
			connectionPlayer.put(client, aOldPlayer);
			send(aOldPlayer, client);
		} else {
			Player aNewPlayer = new Player(name, getId(), 0, 0, 0);
			existingPlayers.put(name, aNewPlayer);
			usedNames.add(name);
			connectionPlayer.put(client, aNewPlayer);
			send(aNewPlayer, client);
		}
	}
	
	/**Enqueues a client to a queue of players looking for opponents.
	 * If client is second to join this queue, the enqueued clients are matched up. 
	 * Mimics a lobby.
	 * 
	 * @param client
	 */
	private void search(ConnectionToClient client) {
		if (!connectionPlayer.containsKey(client)) return;
		if (enqueuedPlayers.size() >= 1) {
			matchPlayer(client, enqueuedPlayers.removeFirst());
		} else {
			enqueuedPlayers.addLast(client);
		}
	}
	
	/**Removes client from queue of players looking for opponents (lobby).
	 * 
	 * @param client
	 */
	private void stopSearch(ConnectionToClient client) {
		enqueuedPlayers.remove(client);
	}
	
	/**
	 * Matches two clients together, signaling a match to start.
	 * Both clients can only be matched if they have not been matched already.
	 * 
	 * @param c1
	 * @param c2
	 */
	private void matchPlayer(ConnectionToClient c1, ConnectionToClient c2) {
		unmatchPlayer(c1);
		unmatchPlayer(c2);
		matchedPlayers.put(c1, c2);
		matchedPlayers.put(c2, c1);
		send(connectionPlayer.get(c1), c2);
		send(connectionPlayer.get(c2), c1);
		delay(500);
		startMatch(c1, c2);
	}

	 /**
	  * Removes client from hashmap of matched players. 
	  * Sends message to client signaling that it has been unmatched.
	  * @param client
	  */
	private void unmatchPlayer(ConnectionToClient client) {
		ConnectionToClient temp;
		
		if (matchedPlayers.containsKey(client)) {
			temp = matchedPlayers.remove(client);
			if (matchedPlayers.containsKey(temp) && matchedPlayers.get(temp) == client) {
				matchedPlayers.remove(temp);
			}
			if (enqueuedForRematch.contains(client)) enqueuedForRematch.remove(client);
			if (enqueuedForRematch.contains(temp)) enqueuedForRematch.remove(temp);
			send("#unmatched", temp);
		}
		send("#unmatched", client);
	}
	
	/**
	 * Links to clients together to represent that they are
	 * playing against each other.  Allows for users moves
	 * to be correctly transfered to one another.
	 * 
	 * @param c1
	 * @param c2
	 */
	private void startMatch(ConnectionToClient c1, ConnectionToClient c2) {
		Player p1, p2;
		p1 = connectionPlayer.get(c1);
		p2 = connectionPlayer.get(c2);
		int startId = 0;
		if (Math.random() >= 0.5) {
			startId = p1.getPlayerId();
		} else {
			startId = p2.getPlayerId();
		}
		String message = "#start_round " + startId;
		send(message, c1);
		send(message, c2);
	}
	
	/**
	 * Will signify that the a user would like to play
	 * a rematch against their oppenet after a game.  Will
	 * begin the next game if both players have requested
	 * to play again.
	 * 
	 */
	private void rematch(ConnectionToClient c1) {
		ConnectionToClient c2 = matchedPlayers.get(c1);
		if (enqueuedForRematch.contains(c2)) {
			enqueuedForRematch.remove(c2);
			startMatch(c1, c2);
		} else {
			enqueuedForRematch.addLast(c1);
		}
	}
	
	/**
	 * Signifies to the server that a user has quit out of
	 * their program.  This will log off the user an end any game
	 * the user was in.
	 */
	private void playerQuit(ConnectionToClient client) {
		if (enqueuedPlayers.contains(client)) enqueuedPlayers.remove(client);
		unmatchPlayer(client);
		if (connectionPlayer.containsKey(client)) {
			usedNames.remove(connectionPlayer.get(client).getName());
		}
		connectionPlayer.remove(client);
	}
	
	/**
	 * Updates the stored stats of a user's player after each game.
	 * This is expected to be call after the server sends out
	 * <code>#get_player</code> message is sent to the client.
	 * 
	 */
	private void updatePlayer(Player player, ConnectionToClient client) {
		if (connectionPlayer.containsKey(client)) {
			Player oldPlayer = connectionPlayer.get(client);
			if (oldPlayer.equals(player)) {
				connectionPlayer.remove(client);
				connectionPlayer.put(client, player);
				existingPlayers.replace(oldPlayer.getName(), player);
			}
		}
	}
	
	/**
	 * Server will request for an updated Player object from
	 * the clients after a game.
	 */
	private void gameOver(ConnectionToClient client) {
		send("#get_player", client);
	}
	
	/**
	 * Passes the users Actions from one client to another
	 * once an Action is sent.
	 * 
	 */
	private void passAction(Action msg, ConnectionToClient client) {
		if (!isLoggedIn(client)) return;
		if (matchedPlayers.containsKey(client)) {
			send(msg, matchedPlayers.get(client));
		}
	}
	
	/**
	 * Easily pauses a thread for a given number of milliseconds.
	 */
	private void delay(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException ie) {
			
		}
	}
	
	/**
	 * Checks if a client connection is currently logged in
	 * with a user name and an associated Player.
	 */
	private boolean isLoggedIn(ConnectionToClient client) {
		return connectionPlayer.containsKey(client);
	}
	
	/**
	 *	Prints out any givem string message resceived from a client.
	 * This is intended for debuggin purposes.
	 */
	private void printMessage(String message, ConnectionToClient client) {
		if (connectionPlayer.containsKey(client)) {
			Player sender = connectionPlayer.get(client);
			out.println(sender.getName() + "> " + message);
		} else {
			out.println("UNKNOWN> " + message);
		}
	}
	
	/**
	 * Handles messages from the server UI.  This is not currently implemented
	 * as its functionalities were not part of the original design.
	 */
	public void handleMessageFromServerUI(String msg) {
		String parts[] = msg.split("\\s+");
		if (parts.length ==0) return;
		
		if (parts[0].equals("player")) {
			if (parts.length == 1) {
				Iterator<Player> list = connectionPlayer.values().iterator();
				while (list.hasNext()) {
					Player p1, p2 = null;
					p1 = list.next();
					out.print(p1.getName() + "(" + p1.getPlayerId() + ")");
					out.println();
				}
			} else {
				if (existingPlayers.containsKey(parts[1])) {
					ConnectionToClient[] cc = (ConnectionToClient[]) connectionPlayer.keySet().toArray();
					ConnectionToClient client = null;
					for (int i = 0; i < cc.length; i++) {
						client = cc[i];
						if (connectionPlayer.get(client).equals(parts[0])) {
							break;
						} else if (i == cc.length -1) {
							client = null;
						}
						
					}
					if (client == null) {
						out.println("Player not connected");
					} else if (parts.length == 2 && client != null) {
						out.println(connectionPlayer.get(client));
					} else if (parts[2].equals("disconnect")) {
						playerQuit(client);
					}
				}
			}
		}
		
	}
	
	/**
	 * Server user's console input loop.
	 */
	public void cycle() {
		boolean run = true;
		String msg = "";
		
		while (run) {
			out.print("> ");
			msg = in.nextLine();
			if (msg.equals("quit")) {
				handleMessageFromServerUI(msg);
				run = false;
			} else {
				handleMessageFromServerUI(msg);
			}
		}
	}
	
	/**
	 * The main method for the program.  Will create a server and 
	 * begin listening for connection.
	 */
	public static void main(String[] args) {
		ServerNaCG server = new ServerNaCG(3030);
		//server.existingPlayers.put("SuperOxigen", new Player("SuperOxigen", 1337, 25, 3, 8));
		//server.existingPlayers.put("CrazedHitman", new Player("CrazedHitman", 130013, 3, 25, 8));
		try {
			server.listen();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//server.cycle();
		
	}
}
