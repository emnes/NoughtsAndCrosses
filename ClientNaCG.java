import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import com.lloseng.ocsf.client.*;

public class ClientNaCG extends AbstractClient {
	
	public static Scanner in = new Scanner(System.in);
	public static PrintStream out = System.out;
	
	private NoughtsAndCrossesGame clientUI;
	private boolean loggedIn = false;

	public ClientNaCG(String host, int port) {
		super(host, port);
		clientUI = NoughtsAndCrossesGame.getInstance(this);
		clientUI.setVisible(false);
	}
	
	public void login() {
		String name;
		
		name = promtForUserName();
		
		send("#login " + name);
	}
	
	public void createGame(Player player) {
		clientUI.setLocalPlayer(player);
		clientUI.updateStats();
		clientUI.setVisible(true);
		loggedIn = true;
	}

	@Override
	protected void handleMessageFromServer(Object msg) {
		if (msg instanceof String) {
			handleCommandFromServer((String) msg);
			return;
		} else if (msg instanceof Action) {
			handlePlayerActionFromServer((Action) msg);
		}
		
		if (!loggedIn) {
			if (msg instanceof Player) {
				createGame((Player) msg);
			}
		} else {
			if (msg instanceof Player) {
				if (clientUI.getRemotePlayer() == null && clientUI.getSession() == NoughtsAndCrossesGame.Session.NoGame) {
					clientUI.setLocalPlayer((Player) msg);
					clientUI.playerConnected();
				}
			}
		}
	}
	
	public void handleMessageFromClientUI(Object msg) {
		
	}
	
	public void handleCommandFromServer(String msg) {
		String tokens[] = msg.split("\\s+");
		if (msg.contains("#update")) {
			send(clientUI.getSessionFullName());
		} else if (msg.contains("#login_fail")) {
			if (tokens.length >= 2) out.println("SRVR> " + tokens[1]);
			login();
		} else if (msg.contains("#start_game")) {
			if (tokens.length >= 2 && clientUI.getRemotePlayer() != null) {
				clientUI.setLocalStarts(clientUI.getLocalPlayer().getPlayerId() == Integer.valueOf(tokens[1]));
				clientUI.startTurn();
			} else {
				send("#bad_start");
			}
		} else if (msg.contains("")) {
			
		} else if (msg.contains("")) {
			
		} else if (msg.contains("")) {
			
		} else if (msg.contains("")) {
			
		} else if (msg.contains("")) {
			
		}
	}
	
	public void handlePlayerActionFromServer(Action msg) {
		if (clientUI.getSession() == NoughtsAndCrossesGame.Session.InGame) {
			
		}
		
	}
	
	public void send(Object msg) {
		try {
			sendToServer(msg);
		} catch (IOException e) {
			out.println("Failed to send " + msg);
		}
		
	}
	
	public void quit() {
		try {
			sendToServer("#quit");
			this.closeConnection();
		} catch (IOException e) {
			
		}
		System.exit(0);
	}
	
	
	
	public String promtForUserName() {
		String name;
		out.println("Please enter a username...\nMust be atleast 6 characters, begin with a letter, and contain no whitespaces.\nType \"quit\" to exit");
		do {			
			System.out.print("USR> ");
			name = in.nextLine();
			if (name.toLowerCase().equals("quit")) System.exit(0);
		} while (!validName(name));
		return name;
	}
	
	
	public static boolean validName(String name) {
		boolean valid = false;
		
		if (name.length() < 6) {
			out.println("Username must be atleast 6 characters long");
		} else if (!('A' <= name.charAt(0) && 'Z' >= name.charAt(0)) && !('a' <= name.charAt(0) && 'z' >= name.charAt(0))) {
			out.println("Username must begin with a letter");
		} else if (name.contains(" ") || name.contains(System.getProperty("line.separator")) || name.contains("\t")) {
			out.println("Username must not contain whitespaces (space, tab, new line)");
		} else {
			return true;
		}
		
		return valid;
	}
	
	
	public static void main(String args[]) {
		NoughtsAndCrossesGame clientUI = NoughtsAndCrossesGame.getInstance(null);
		Player player = new Player("Alex", 1337, 1000, 1, 1);
		new Token(Token.CROSS, player);
		clientUI.setLocalPlayer(player);
		clientUI.setVisible(true);
	}
}
