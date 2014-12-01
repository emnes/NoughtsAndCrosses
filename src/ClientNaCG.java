import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;
/**
 * This class overrides some of the methods in the abstract superclass in order
 * to give more functionality to the client.
 * 
 * @author Mazhar Shar (7987495)
 * @author Alex Dale (6839825)
 * @version Nov 2014
 */

import ocsf.client.*;

public class ClientNaCG extends AbstractClient {

	// INPUT AND OUTPUT STREAMS
	public static Scanner in = new Scanner(System.in);
	public static PrintStream out = System.out;

	/**
	 * Reference to the client UI and game board.
	 */
	private NoughtsAndCrossesGame clientUI;

	/**
	 * Flag to track whether the user is connected.
	 */
	private boolean loggedIn = false;

	/**
	 * Creates the Noughts and Crosses game board. Will exit application if a
	 * connection cannot be established.
	 * 
	 * @param host
	 * @param port
	 */
	public ClientNaCG(String host, int port) {
		super(host, port);
		try {
			openConnection();
		} catch (IOException e) { // Closes application if cannot connected.
			out.println("Cannot connect to server, please check connection and try again");
			System.exit(0);
		}
		clientUI = NoughtsAndCrossesGame.getInstance(this);
		clientUI.setVisible(false);
	}

	/**
	 * Prompts the user to log in with an appropriate used name and sends the
	 * login signal to the server.
	 */
	public void login() {
		String name;

		name = promtForUserName();

		send("#login " + name);
	}

	/**
	 * Initiates the users GUI to once the player has logged in successfully.
	 * 
	 * @param player
	 */
	public void createGameSession(Player player) {
		if (loggedIn)
			return;
		clientUI.setLocalPlayer(player);
		clientUI.updateStats();
		clientUI.setVisible(true);
		loggedIn = true;
	}

	/**
	 * Overwritten method of Abstract Client to handle messages from the server.
	 * Will delegate the message to the appropriate handling method based on the
	 * Object type.
	 */
	protected void handleMessageFromServer(Object msg) {
		if (msg instanceof String) {
			handleCommandFromServer((String) msg);
			return;
		} else if (msg instanceof Action) {
			try {
				handlePlayerActionFromServer((Action) msg);
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}

		if (!loggedIn) {
			if (msg instanceof Player) {
				createGameSession((Player) msg);
			}
		} else {
			if (msg instanceof Player) {
				if (clientUI.getSession() == NoughtsAndCrossesGame.Session.NoGame
						&& clientUI.getSessionNoGame() == NoughtsAndCrossesGame.SessionNoGame.Searching) {
					matched((Player) msg);
				}
			}
		}
	}

	/**
	 * Handles commands that come from the client's UI. Nearly all message are
	 * sent to the server.
	 * 
	 * @param msg
	 */
	public void handleMessageFromClientUI(Object msg) {
		if (msg instanceof Action) {
			send(msg);
		} else if (msg instanceof String) {
			String message = (String) msg;

			if (message.equals("#search")) {
				send(message);
			} else if (message.equals("#stop_search")) {
				send(message);
			} else if (message.equals("#logoff")) {
				// TODO
			} else if (message.equals("#surrender")) {
				send(message);
			} else if (message.equals("#end")) {
				send(message);
			} else if (message.equals("#rematch")) {
				send(message);
			} else if (message.equals("#quit")) {
				quit();
			} else if (message.equals("#game_over")) {
				send(message);
			}
		}
	}

	/**
	 * Handles text commands that were sent from the server to the client.
	 * 
	 * 
	 * @param msg
	 */
	public void handleCommandFromServer(String msg) {
		String tokens[] = msg.split("\\s+");
		//out.println("SRVR> " + msg);
		if (msg.contains("#update")) {
			send(clientUI.getSessionFullName());

		} else if (msg.contains("#login_fail")) {
			if (tokens.length >= 2 && tokens[1].equals("username_taken")) {
				System.out
						.println("The username you have enter has already been taken.");
			}
			login();

		} else if (msg.contains("#start_round")) {
			if (tokens.length >= 2 && clientUI.getRemotePlayer() != null) {
				clientUI.setLocalStarts(clientUI.getLocalPlayer().getPlayerId() == Integer
						.valueOf(tokens[1]));
				if (clientUI.getSession() == NoughtsAndCrossesGame.Session.InGame)
					clientUI.startTurn();
				else if (clientUI.getSession() == NoughtsAndCrossesGame.Session.EndGame) {
					clientUI.rematch();
					clientUI.startTurn();
				}
			} else {
				send("#bad_start");
			}

		} else if (msg.contains("#get_player")) {
			try {
				forceResetAfterSend();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (clientUI.getLocalPlayer() != null)
				send(clientUI.getLocalPlayer());
			else
				send("#no_player");

		} else if (msg.contains("#remote_quit")) {
			unmatched();
		} else if (msg.contains("#unmatched")) {
			unmatched();
		} else if (msg.contains("#not_logged_in")) {
			if (loggedIn) {

			} else {

			}
		} else if (msg.contains("")) {

		}
	}
	
	
	/**
	 * Forwards all Actions from the opposing player sent by the server to the clients UI.
	 * 
	 * @param msg
	 */
	public void handlePlayerActionFromServer(Action msg) {
		if (clientUI.getSession() != NoughtsAndCrossesGame.Session.NoGame) {
			clientUI.remoteActionPerformed(msg);
		}
	}
	
	/**
	 * Sends the given object to the server if there
	 * is a connection.
	 * @param msg
	 */
	public void send(Object msg) {
		try {
			sendToServer(msg);
		} catch (IOException e) {
			e.printStackTrace();
			out.println("Failed to send " + msg);
		}

	}
	
	/**Sends a matched player to the client's UI
	 * and initiates the In-game mode of he board.
	 * 
	 * @param p
	 */
	private void matched(Player p) {
		out.println("Matched with " + p);
		clientUI.setRemotePlayer(p);
		clientUI.playerConnected();
	}
	
	/**
	 * Notifies the client's UI that the opposing player has been
	 * unmatched.
	 */
	private void unmatched() {
		if (clientUI.getSession() == NoughtsAndCrossesGame.Session.InGame) {
			clientUI.remoteQuit();
		} else if (clientUI.getSession() == NoughtsAndCrossesGame.Session.EndGame) {
			clientUI.remoteQuit();
		}

		clientUI.setRemotePlayer(null);
	}
	
	/**
	 * Quits the clients applications and notifies the server.
	 */
	public void quit() {
		try {
			if (isConnected())
				sendToServer("#quit");
			this.closeConnection();
		} catch (IOException e) {

		}
		System.exit(0);
	}
	
	/**
	 * Notifies the client that a connection was successfully closed.
	 */
	protected void connectionClosed() {
		out.println("Connection Closed");
	}
	
	/**
	 * Closes the application if the connection is lost.
	 */
	protected void connectionException(Exception e) {
		out.println("Something wend wrong");
		clientUI.setVisible(false);
		out.println("You have been disconnected");
		quit();
	}
	
	/**
	 * Notifies the client that a connection was successfully made.
	 */
	protected void connectionEstablished() {
		out.println("Connection Established");
	}
	
	/**
	 * Prompts the user to enter a correct user name, and will validate the entered
	 * user name.  Will continue to prompt the user if the entered user name is invalid.
	 * 
	 * @return
	 */
	public String promtForUserName() {
		String name;
		out.println("Please enter a username...\n"
				+ "Must be atleast 6 characters, begin with a letter, and contain no whitespaces.\n"
				+ "Type \"quit\" to exit");
		do {
			out.print("USR> ");
			name = in.nextLine();
			if (name.toLowerCase().equals("quit"))
				System.exit(0);
		} while (!validName(name));
		return name;
	}
	
	/**Checks the entered the String if it is a valid user name.
	 * 
	 * @param name
	 * @return
	 */
	public static boolean validName(String name) {
		boolean valid = false;

		if (name.length() < 6) {
			out.println("Username must be atleast 6 characters long");
		} else if (!('A' <= name.charAt(0) && 'Z' >= name.charAt(0))
				&& !('a' <= name.charAt(0) && 'z' >= name.charAt(0))) {
			out.println("Username must begin with a letter");
		} else if (name.contains(" ")
				|| name.contains(System.getProperty("line.separator"))
				|| name.contains("\t")) {
			out.println("Username must not contain whitespaces (space, tab, new line)");
		} else {
			return true;
		}

		return valid;
	}
	
	/**
	 * Will create a new Client for the Noughts and Crosses game.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
        if(args.length > 1)
            new ClientNaCG(args[0], 3030).login();
        else
            new ClientNaCG("localhost", 3030).login();
	}
}
