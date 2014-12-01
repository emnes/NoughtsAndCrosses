//GUI Framework
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

//Utility Framework
import java.sql.Time;
import java.util.*;

/**
 * This class acts as both the system GUI and the system model
 * for the game Noughts and Crosses.  This is the main class for
 * the Client to operate on.
 * 
 * @author Alexander Dale (6839825)
 * @author Maz Shar
 * @version November 2014
 */
@SuppressWarnings("serial")
public class NoughtsAndCrossesGame extends JFrame implements ActionListener {
	
	public static final String NL = System.getProperty("line.separator");
	
	private class ClosingEvent extends WindowAdapter {
		public void windowClosing(WindowEvent evt) {
			client.handleMessageFromClientUI("#quit");
		}
	}
	
	// ------------------------
	// STATIC VARIABLES
	// ------------------------

	private static NoughtsAndCrossesGame theInstance = null;

	// ------------------------
	// MEMBER VARIABLES
	// ------------------------
	
	// Player association
	private Player localPlayer;
	private Player remotePlayer;

	// GUI Attributes
	JPanel  board, scoreBoard, control, bottomPanel;
	JButton control1, control2, quitButton;
	JLabel mainBoard, localStats, remoteStats, gameStats;
	
	// Controller Associations
	ClientNaCG client;

	// NoughtsAndCrossesGame State Machines
	enum Session {
		NoGame, InGame, EndGame
	}

	enum SessionNoGame {
		Null, Idle, Searching
	}

	enum SessionInGame {
		Null, NoTurn, LocalTurn, RemoteTurn
	}

	enum SessionEndGame {
		Null, LocalWin, RemoteWin, Draw
	}
	
	// State Variables
	private Session session;
	private SessionNoGame sessionNoGame;
	private SessionInGame sessionInGame;
	private SessionEndGame sessionEndGame;

	// NoughtsAndCrossesGame Associations
	private List<Square> squares;

	// ------------------------
	// CONSTRUCTOR
	// ------------------------

	private NoughtsAndCrossesGame(ClientNaCG client) {
		this.client = client;
		squares = new ArrayList<Square>();
		startUp();
		setSessionNoGame(SessionNoGame.Null);
		setSessionInGame(SessionInGame.Null);
		setSessionEndGame(SessionEndGame.Null);
		setSession(Session.NoGame);
		setResizable(false);
		addWindowListener(new ClosingEvent());
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setTitle("Noughts and Crosses by Monarch Games (Alex and Maz)");
	}

	public static NoughtsAndCrossesGame getInstance(ClientNaCG client) {
		if (theInstance == null) {
			theInstance = new NoughtsAndCrossesGame(client);
		}
		return theInstance;
	}

	// ------------------------
	// INTERFACE
	// ------------------------

	// *************************************************************************
	//
	// Attribute Getters and Setters
	//
	// *************************************************************************

	public boolean setLocalPlayer(Player aLocalPlayer) {
		boolean wasSet = false;
		localPlayer = aLocalPlayer;
		wasSet = true;
		return wasSet;
	}

	public boolean setRemotePlayer(Player aRemotePlayer) {
		boolean wasSet = false;
		remotePlayer = aRemotePlayer;
		wasSet = true;
		return wasSet;
	}

	public Player getLocalPlayer() {
		return localPlayer;
	}

	public Player getRemotePlayer() {
		return remotePlayer;
	}

	public String getSessionFullName() {
		String answer = session.toString();
		if (sessionNoGame != SessionNoGame.Null) {
			answer += "." + sessionNoGame.toString();
		}
		if (sessionInGame != SessionInGame.Null) {
			answer += "." + sessionInGame.toString();
		}
		if (sessionEndGame != SessionEndGame.Null) {
			answer += "." + sessionEndGame.toString();
		}
		return answer;
	}

	public Session getSession() {
		return session;
	}

	public SessionNoGame getSessionNoGame() {
		return sessionNoGame;
	}

	public SessionInGame getSessionInGame() {
		return sessionInGame;
	}

	public SessionEndGame getSessionEndGame() {
		return sessionEndGame;
	}

	// *************************************************************************
	//
	// State Controls
	//
	// *************************************************************************
	
	/** This is called when another player is connected to the 
	 * current player. While it is searching for other players.
	 * 
	 * @return - returns true if the state was successfully transitioned.
	 */
	public boolean playerConnected() {
		boolean wasEventProcessed = false;

		SessionNoGame aSessionNoGame = sessionNoGame;
		switch (aSessionNoGame) {
		case Searching:
			exitSession();
			setSessionInGame(SessionInGame.NoTurn);
			wasEventProcessed = true;
			break;
		default:
			// Other states do respond to this event
		}

		return wasEventProcessed;
	}
	
	/**Signifies that the local player has quit a game.  This
	 * will end the current game and set the local player as
	 * a loss if it was during the game.
	 * 
	 * @return
	 */
	public boolean localQuit() {
		boolean wasEventProcessed = false;

		Session aSession = session;
		switch (aSession) {
		case InGame:
			exitSession();
			setLocalLoss();
			setSessionNoGame(SessionNoGame.Idle);
			wasEventProcessed = true;
			break;
		case EndGame:
			exitSession();
			setSessionNoGame(SessionNoGame.Idle);
			wasEventProcessed = true;
			break;
		default:
			// Other states do respond to this event
		}

		return wasEventProcessed;
	}
	
	/**Signifies that the remote player has quit a game.  This
	 * will end the current game and set the remote player as
	 * a loss if it was during the game.
	 * 
	 * @return true if the remote user quitting has an effect on program
	 */
	public boolean remoteQuit() {
		boolean wasEventProcessed = false;

		Session aSession = session;
		switch (aSession) {
		case InGame:
			exitSession();
			setLocalWin();
			setSessionNoGame(SessionNoGame.Idle);
			wasEventProcessed = true;
			break;
		case EndGame:
			exitSession();
			setSessionNoGame(SessionNoGame.Idle);
			wasEventProcessed = true;
			break;
		default:
			// Other states do respond to this event
		}

		return wasEventProcessed;
	}
	
	
	/**Signifies that both users have agreed to a re-match an will re-initiate 
	 * a round of Noughts and Crosses.
	 * 
	 * @return true if the game was successfully initated
	 */
	public boolean rematch() {
		boolean wasEventProcessed = false;

		Session aSession = session;
		switch (aSession) {
		case EndGame:
			exitSession();
			setSessionInGame(SessionInGame.NoTurn);
			wasEventProcessed = true;
			break;
		default:
			// Other states do respond to this event
		}

		return wasEventProcessed;
	}
	
	/**
	 * Stores whether or not the local user will
	 * start the game.
	 */
	boolean localStartsFlag = false;

	/**Indicates whether or not the local user will
	 * start the next game.
	 * 
	 * @return
	 */
	public boolean localStarts() {
		return localStartsFlag;
	}
	
	/**Sets whether the local user will start the next game
	 * 
	 * @param value
	 */
	public void setLocalStarts(boolean value) {
		localStartsFlag = value;
	}
	
	
	//NO FUNCTION, GENERATED BY UMPLE
	@SuppressWarnings("unused")
	private boolean enterNoGame() {
		boolean wasEventProcessed = false;

		SessionNoGame aSessionNoGame = sessionNoGame;
		switch (aSessionNoGame) {
		case Null:
			setSessionNoGame(SessionNoGame.Idle);
			wasEventProcessed = true;
			break;
		default:
			// Other states do respond to this event
		}

		return wasEventProcessed;
	}

	//UMPLE GENERATED STATE CLEAN UP
	private boolean exitNoGame() {
		boolean wasEventProcessed = false;

		SessionNoGame aSessionNoGame = sessionNoGame;
		switch (aSessionNoGame) {
		case Idle:
			setSessionNoGame(SessionNoGame.Null);
			wasEventProcessed = true;
			break;
		case Searching:
			setSessionNoGame(SessionNoGame.Null);
			wasEventProcessed = true;
			break;
		default:
			// Other states do respond to this event
		}

		return wasEventProcessed;
	}
	
	/**Puts the client in search mode for another player
	 * to player against.
	 * 
	 * @return true if the system successfully went into
	 * 			search mode.
	 */
	public boolean startSearch() {
		boolean wasEventProcessed = false;

		SessionNoGame aSessionNoGame = sessionNoGame;
		switch (aSessionNoGame) {
		case Idle:
			setSessionNoGame(SessionNoGame.Searching);
			wasEventProcessed = true;
			break;
		default:
			// Other states do respond to this event
		}

		return wasEventProcessed;
	}
	
	/**Cancels the search for other players.
	 * 
	 * @return true if successfully ended search
	 */
	public boolean stopSearch() {
		boolean wasEventProcessed = false;

		SessionNoGame aSessionNoGame = sessionNoGame;
		switch (aSessionNoGame) {
		case Searching:
			setSessionNoGame(SessionNoGame.Idle);
			wasEventProcessed = true;
			break;
		default:
			// Other states do respond to this event
		}

		return wasEventProcessed;
	}
	
	//UMPLE GENERATED STATE CLEAN UP
	@SuppressWarnings("unused")
	private boolean enterInGame() {
		boolean wasEventProcessed = false;

		SessionInGame aSessionInGame = sessionInGame;
		switch (aSessionInGame) {
		case Null:
			setSessionInGame(SessionInGame.NoTurn);
			wasEventProcessed = true;
			break;
		default:
			// Other states do respond to this event
		}

		return wasEventProcessed;
	}
	
	//UMPLE GENERATED STATE CLEAN UP
	private boolean exitInGame() {
		boolean wasEventProcessed = false;

		SessionInGame aSessionInGame = sessionInGame;
		switch (aSessionInGame) {
		case NoTurn:
			setSessionInGame(SessionInGame.Null);
			wasEventProcessed = true;
			break;
		case LocalTurn:
			setSessionInGame(SessionInGame.Null);
			wasEventProcessed = true;
			break;
		case RemoteTurn:
			setSessionInGame(SessionInGame.Null);
			wasEventProcessed = true;
			break;
		default:
			// Other states do respond to this event
		}

		return wasEventProcessed;
	}
	
	/**Transition the game board to an In-Game state.  will also appropriately set
	 * the token images to the players.  X for the starting player, O for the other.
	 * 
	 * @return
	 */
	public boolean startTurn() {
		boolean wasEventProcessed = false;

		SessionInGame aSessionInGame = sessionInGame;
		switch (aSessionInGame) {
		case NoTurn:
			if (localStarts()) {
				if (getLocalPlayer().getToken() != null) {
					getLocalPlayer().getToken().setImageUrl(Token.CROSS);
				} else {
					new Token(Token.CROSS, getLocalPlayer());
				}				
				
				if (getRemotePlayer().getToken() != null) {
					getRemotePlayer().getToken().setImageUrl(Token.NOUGHT);
				} else {
					new Token(Token.NOUGHT, getRemotePlayer());
				}
				
				setSessionInGame(SessionInGame.LocalTurn);
				wasEventProcessed = true;
				break;
			}
			if (!localStarts()) {
				if (getLocalPlayer().getToken() != null) {
					getLocalPlayer().getToken().setImageUrl(Token.NOUGHT);
				} else {
					new Token(Token.NOUGHT, getLocalPlayer());
				}				
				
				if (getRemotePlayer().getToken() != null) {
					getRemotePlayer().getToken().setImageUrl(Token.CROSS);
				} else {
					new Token(Token.CROSS, getRemotePlayer());
				}
				
				setSessionInGame(SessionInGame.RemoteTurn);
				wasEventProcessed = true;
				break;
			}
			break;
		default:
			// Other states do respond to this event
		}

		return wasEventProcessed;
	}
	
	/**Indicates that the local player has placed a token.  The system will first
	 * check if the local player has won the match or if it is possible for the 
	 * next player to make a move.  The if the local player has not won the 
	 * match and there are still free spaces left on the board, then the 
	 * remote player will be able to move.
	 * 
	 * @return
	 */
	public boolean localActed() {
		boolean wasEventProcessed = false;

		SessionInGame aSessionInGame = sessionInGame;
		switch (aSessionInGame) {
		case LocalTurn:
			if (!isGameOver() && isFreeSquare()) {
				setSessionInGame(SessionInGame.RemoteTurn);
				wasEventProcessed = true;
				break;
			}
			if (!isGameOver() && !isFreeSquare()) {
				drawStats();
				exitSession();
				setSessionEndGame(SessionEndGame.Draw);
				wasEventProcessed = true;
				break;
			}
			if (isGameOver()) {
				winStats(getLocalPlayer());
				exitSession();
				setSessionEndGame(SessionEndGame.LocalWin);
				wasEventProcessed = true;
				break;
			}
			break;
		default:
			// Other states do respond to this event
		}

		return wasEventProcessed;
	}
	
	/**Indicates that the remote player has placed a token.  The system will first
	 * check if the remote player has won the match or if it is possible for the 
	 * next player to make a move.  The if the remote player has not won the 
	 * match and there are still free spaces left on the board, then the 
	 * local player will be able to move.
	 * 
	 * @return
	 */
	public boolean remoteActed() {
		boolean wasEventProcessed = false;

		SessionInGame aSessionInGame = sessionInGame;
		switch (aSessionInGame) {
		case RemoteTurn:
			if (!isGameOver() && isFreeSquare()) {
				setSessionInGame(SessionInGame.LocalTurn);
				wasEventProcessed = true;
				break;
			}
			if (!isGameOver() && !isFreeSquare()) {
				drawStats();
				exitSession();
				setSessionEndGame(SessionEndGame.Draw);
				wasEventProcessed = true;
				break;
			}
			if (isGameOver()) {
				winStats(getRemotePlayer());
				exitSession();
				setSessionEndGame(SessionEndGame.RemoteWin);
				wasEventProcessed = true;
				break;
			}
			break;
		default:
			// Other states do respond to this event
		}

		return wasEventProcessed;
	}
	
	//NO FUNCTION, GENERATED BY UMPLE
	@SuppressWarnings("unused")
	private boolean enterEndGame() {
		boolean wasEventProcessed = false;

		SessionEndGame aSessionEndGame = sessionEndGame;
		switch (aSessionEndGame) {
		case Null:
			setSessionEndGame(SessionEndGame.LocalWin);
			wasEventProcessed = true;
			break;
		default:
			// Other states do respond to this event
		}

		return wasEventProcessed;
	}
	
	//UMPLE GENERATED STATE CLEAN UP
	private boolean exitEndGame() {
		boolean wasEventProcessed = false;

		SessionEndGame aSessionEndGame = sessionEndGame;
		switch (aSessionEndGame) {
		case LocalWin:
			resetSquares();
			setSessionEndGame(SessionEndGame.Null);
			wasEventProcessed = true;
			break;
		case RemoteWin:
			resetSquares();
			setSessionEndGame(SessionEndGame.Null);
			wasEventProcessed = true;
			break;
		case Draw:
			resetSquares();
			setSessionEndGame(SessionEndGame.Null);
			wasEventProcessed = true;
			break;
		default:
			// Other states do respond to this event
		}

		return wasEventProcessed;
	}
	
	//UMPLE GENERATED STATE CLEAN UP
	private void exitSession() {
		switch (session) {
		case NoGame:
			exitNoGame();
			break;
		case InGame:
			exitInGame();
			break;
		case EndGame:
			exitEndGame();
			break;
		}
	}

	// *************************************************************************
	//
	// State Setters
	//
	// *************************************************************************
	
	/**Session state machine state setter.
	 * 
	 * @param aSession
	 */
	private void setSession(Session aSession) {
		session = aSession;

		// entry actions and do activities
		switch (session) {
		case NoGame:
			// line 13 "model.ump"
			resetSquares();
			setRemotePlayer(null);
			updateStats();
			lockSquares();
			localStats.setForeground(Color.WHITE);
			remoteStats.setForeground(Color.WHITE);
			if (sessionNoGame == SessionNoGame.Null) {
				setSessionNoGame(SessionNoGame.Idle);
			}
			break;
		case InGame:
			// line 31 "model.ump"
			resetSquares();
			lockSquares();
			updateStats();
			updateControl();
			if (sessionInGame == SessionInGame.Null) {
				setSessionInGame(SessionInGame.NoTurn);
			}
			break;
		case EndGame:
			// line 59 "model.ump"
			lockSquares();
			updateControl();
			client.handleMessageFromClientUI("#game_over");
			if (sessionEndGame == SessionEndGame.Null) {
				setSessionEndGame(SessionEndGame.LocalWin);
			}
			break;
		}
	}
	
	/**Session.NoGame state machine states setter
	 * 
	 * @param aSessionNoGame
	 */
	private void setSessionNoGame(SessionNoGame aSessionNoGame) {
		sessionNoGame = aSessionNoGame;
		if (session != Session.NoGame && aSessionNoGame != SessionNoGame.Null) {
			setSession(Session.NoGame);
		}

		// entry actions and do activities
		switch (sessionNoGame) {
		case Idle:
			// line 17 "model.ump"
			updateControl();
			break;
		case Searching:
			// line 23 "model.ump"
			updateControl();
			break;
		default:
			break;
		}
	}
	
	/**Session.InGame state machine states setter
	 * 
	 * @param aSessionNoGame
	 */
	private void setSessionInGame(SessionInGame aSessionInGame) {
		sessionInGame = aSessionInGame;
		if (session != Session.InGame && aSessionInGame != SessionInGame.Null) {
			setSession(Session.InGame);
		}

		// entry actions and do activities
		switch (sessionInGame) {
		case LocalTurn:
			// line 45 "model.ump"
			localStats.setForeground(Color.GREEN);
			remoteStats.setForeground(Color.WHITE);
			unlockSquares();
			break;
		case RemoteTurn:
			localStats.setForeground(Color.WHITE);
			remoteStats.setForeground(Color.GREEN);
			lockSquares();
			break;
		default:
			break;
		}
	}
	
	/**Session.EndGame state machine states setter
	 * 
	 * @param aSessionNoGame
	 */
	private void setSessionEndGame(SessionEndGame aSessionEndGame) {
		sessionEndGame = aSessionEndGame;
		if (session != Session.EndGame
				&& aSessionEndGame != SessionEndGame.Null) {
			setSession(Session.EndGame);
		}

		// entry actions and do activities
		switch (sessionEndGame) {
		case LocalWin:
			localStats.setForeground(Color.WHITE);
			remoteStats.setForeground(Color.WHITE);
			setLocalWin();
			break;
		case RemoteWin:
			localStats.setForeground(Color.WHITE);
			remoteStats.setForeground(Color.WHITE);
			setLocalLoss();
			break;
		case Draw:
			localStats.setForeground(Color.WHITE);
			remoteStats.setForeground(Color.WHITE);
			setLocalDraw();
			break;
		default:
			break;
		}
	}

	// END OF STATE SETTERS

	// *************************************************************************
	//
	// Associations Getters and Setters
	//
	// *************************************************************************
	
	/**Returns a reference to the object stored at the indicated
	 * index of the Squares list.
	 * 
	 * @param index
	 * @return
	 */
	public Square getSquare(int index) {
		Square aSquare = squares.get(index);
		return aSquare;
	}
	
	/**Returns a reference to the Square stored at the indicated
	 * board coordinates.
	 * 
	 * @param x Value from 1 to 3 indicates the column left to right
	 * @param y Value from 1 to 3 indicates the row top to bottom
	 * @return
	 */
	public Square getSquare(int x, int y) {
		Square aSquare = squares.get(x + 3*y - 4);
		return aSquare;
	}
	
	/**Returns a list of all squares that are currently store on the
	 * board.
	 * 
	 * @return
	 */
	public List<Square> getSquares() {
		List<Square> newSquares = Collections.unmodifiableList(squares);
		return newSquares;
	}
	
	/**Returns the number of squares store on the board, should be 9
	 * 
	 * @return
	 */
	public int numberOfSquares() {
		int number = squares.size();
		return number;
	}
	
	/**Indicates whether there are any squares
	 * 
	 * @return
	 */
	public boolean hasSquares() {
		boolean has = squares.size() > 0;
		return has;
	}
	
	/**Finds the index in the Square list of the 
	 * given square.
	 * 
	 * @param aSquare
	 * @return
	 */
	public int indexOfSquare(Square aSquare) {
		int index = squares.indexOf(aSquare);
		return index;
	}
	
	/**The minimum number of squares that the board can have
	 * 
	 * @return
	 */
	public static int minimumNumberOfSquares() {
		return 0;
	}

	/**The maximum number of squares that the board game can have.
	 * 
	 * @return
	 */
	public static int maximumNumberOfSquares() {
		return 9;
	}
	
	/**Adds a square with the given coordinates
	 * 
	 * @param aPosX
	 * @param aPosY
	 * @return
	 */
	private Square addSquare(int aPosX, int aPosY) {
		if (numberOfSquares() >= maximumNumberOfSquares()) {
			return null;
		} else {
			return new Square(aPosX, aPosY, this);
		}
	}
	
	/**Adds the given square to the list of squares.
	 * 
	 * @param aSquare
	 * @return
	 */
	public boolean addSquare(Square aSquare) {
		boolean wasAdded = false;
		if (squares.contains(aSquare)) {
			return false;
		}
		if (numberOfSquares() >= maximumNumberOfSquares()) {
			return wasAdded;
		}

		NoughtsAndCrossesGame existingNoughtsAndCrossesGame = aSquare
				.getNoughtsAndCrossesGame();
		boolean isNewNoughtsAndCrossesGame = existingNoughtsAndCrossesGame != null
				&& !this.equals(existingNoughtsAndCrossesGame);
		if (isNewNoughtsAndCrossesGame) {
			aSquare.setNoughtsAndCrossesGame(this);
		} else {
			squares.add(aSquare);
		}
		wasAdded = true;
		return wasAdded;
	}
	
	/**Removes the square from the list of the squares if it exists.
	 * 
	 * @param aSquare
	 * @return
	 */
	public boolean removeSquare(Square aSquare) {
		boolean wasRemoved = false;
		// Unable to remove aSquare, as it must always have a
		// noughtsAndCrossesGame
		if (!this.equals(aSquare.getNoughtsAndCrossesGame())) {
			squares.remove(aSquare);
			wasRemoved = true;
		}
		return wasRemoved;
	}
	
	/**Adds Square to the square list at the given index.
	 * 
	 * @param aSquare
	 * @param index
	 * @return
	 */
	public boolean addSquareAt(Square aSquare, int index) {
		boolean wasAdded = false;
		if (addSquare(aSquare)) {
			if (index < 0) {
				index = 0;
			}
			if (index > numberOfSquares()) {
				index = numberOfSquares() - 1;
			}
			squares.remove(aSquare);
			squares.add(index, aSquare);
			wasAdded = true;
		}
		return wasAdded;
	}
	
	/**Deletes all the square associations that exist with the board.
	 */
	public void delete() {
		for (int i = squares.size(); i > 0; i--) {
			Square aSquare = squares.get(i - 1);
			aSquare.delete();
		}
	}

	/**Removes all the squares on the board.
	 * 
	 * @return
	 */
	public boolean removeSquares() {
		boolean wasRemoved = false;

		while (numberOfSquares() != 0) {
			removeSquare(getSquare(0));
		}

		wasRemoved = numberOfSquares() != 0;

		return wasRemoved;
	}

	// END OF ASSOCIATION GETTERS AND SETTERS

	// *************************************************************************
	//
	// Board Display Controls
	//
	// *************************************************************************


	/**Will generate the GUI for the game. The parts of the GUI include: a game board,
	 * control buttons, player status bar, and a action status bar.
	 * 
	 */
	private void startUp() {
		setSize(700, 450);
		
		//Frame background
		mainBoard = new JLabel(new ImageIcon(NoughtsAndCrossesGame.class.getResource("data\\background.png")));
		mainBoard.setLayout( new FlowLayout());
		add(mainBoard);
		
		//Game board
		board = new JPanel();
		GridLayout boardLayout = new GridLayout(3, 3);
		boardLayout.setHgap(20);
		boardLayout.setVgap(20);
		board.setLayout(boardLayout);
		board.setOpaque(false);
		mainBoard.add(board, BorderLayout.WEST);

		generateSquares();
		
		//Player status bard
		scoreBoard = new JPanel();
		scoreBoard.setOpaque(false);
		mainBoard.add(scoreBoard, BorderLayout.EAST);
		scoreBoard.setLayout(new GridLayout(3, 1));
		
		//Blank space- leaves apace for the title on the background image
		scoreBoard.add(new JLabel());
		
		//Local player status
		localStats = new JLabel();
		localStats.setForeground(Color.WHITE);
		localStats.setPreferredSize(new Dimension(200, 50));
		localStats.setHorizontalAlignment(SwingConstants.CENTER);
		scoreBoard.add(localStats);
		
		//Remote player status
		remoteStats = new JLabel();
		remoteStats.setForeground(Color.WHITE);
		remoteStats.setHorizontalAlignment(SwingConstants.CENTER);
		scoreBoard.add(remoteStats);
		
		
		//Control & Action status panel
		//*These two are put together to prevent the content being rearranged
		//*after each status update.
		bottomPanel = new JPanel();
		bottomPanel.setOpaque(false);
		bottomPanel.setLayout(new GridLayout(3, 1));
		bottomPanel.add(new JLabel());
		mainBoard.add(bottomPanel);
		
		//Control panel
		control = new JPanel();
		control.setLayout(new GridLayout(1, 3));
		bottomPanel.add(control, BorderLayout.SOUTH);
		
		//Action status
		gameStats = new JLabel();
		gameStats.setForeground(Color.WHITE);
		gameStats.setHorizontalAlignment(SwingConstants.CENTER);
		bottomPanel.add(gameStats);
		
		//Control panel buttons
		control1 = new JButton();
		control2 = new JButton();
		quitButton = new JButton();
		
		control1.addActionListener(this);
		control1.setBackground(Color.DARK_GRAY);
		control1.setForeground(Color.WHITE);
		control1.setPreferredSize(new Dimension(120, 25));
		control1.setFocusPainted(false);
		control2.addActionListener(this);
		control2.setBackground(Color.DARK_GRAY);
		control2.setForeground(Color.WHITE);
		control2.setFocusPainted(false);
		quitButton.addActionListener(this);
		quitButton.setBackground(Color.DARK_GRAY);
		quitButton.setForeground(Color.WHITE);
		quitButton.setFocusPainted(false);
		
		control.add(control1);
		control.add(control2);
		control.add(quitButton);
		
		//First status update.
		control1.setText("Find Player");
		control2.setText("Reset");
		quitButton.setText("Quit");
	}


	/** Generates the Squares for the board game
	 * 
	 */
	private void generateSquares() {
		board.removeAll();
		board.setLayout(new GridLayout(3, 3));

		removeSquares();

		for (int y = 1; y <= 3; y++) {
			for (int x = 1; x <= 3; x++) {
				Square temp = addSquare(x, y);
				temp.setBorderPainted(false);
				temp.setBorder(null);
				temp.setOpaque(false);
				temp.setPreferredSize(new Dimension(110, 110));
				temp.setBackground(Color.DARK_GRAY);
				board.add(temp);
			}
		}

		lockSquares();
	}
	
	/**Unlocks all the Squares on the board if
	 * they can be unlocked.
	 * 
	 * @return
	 */
	private boolean unlockSquares() {
		for (int i = 0; i < numberOfSquares(); i++) {
			getSquare(i).unlock();
		}

		return true;
	}
	
	/**Locks all the Squares on the board if they
	 * can be locked
	 * 
	 * @return
	 */
	private boolean lockSquares() {
		for (int i = 0; i < numberOfSquares(); i++) {
			getSquare(i).lock();
		}

		return true;
	}

	/**Resets all the Squares on the board
	 * 
	 * @return
	 */
	private boolean resetSquares() {
		for (int i = 0; i < numberOfSquares(); i++) {
			getSquare(i).reset();
			getSquare(i).setToken(null);
		}

		return true;
	}


	/**Calls the update player status for the local player
	 * and the remote player if there is one.
	 */
	public void updateStats() {
		if (getLocalPlayer() != null) localStats();
		if (getRemotePlayer() != null) remoteStats();
		else remoteStats.setText("");
	}
	
	/**Updates the local players status based on the current
	 * localPlayer attributes. 
	 */
	private void localStats() {
		getLocalPlayer().determineExperienceLevel();
		String stats = "<HTML><center>" + getLocalPlayer().getName() + " (" + getLocalPlayer().getPlayerId() + ")";
		stats += "<br>" + "Exp. Level: " + getLocalPlayer().getExpLvl();
		stats += "<br>" + "Games: " + getLocalPlayer().getWins() + "/" + getLocalPlayer().getDraws() + "/";
		stats += getLocalPlayer().getLosses() + " (Win/Draw/Loss)<br></center></HTML>";
		localStats.setText(stats);
	}
	
	/**Updates the remote player status based on the current
	 * remotePlayer attributes.
	 */
	private void remoteStats() {
		getRemotePlayer().determineExperienceLevel();
		String stats = "<HTML><center><br>" + getRemotePlayer().getName() + " (" + getRemotePlayer().getPlayerId() + ")";
		stats += "<br>" + "Exp. Level: " + getRemotePlayer().getExpLvl();
		stats += "<br>" + "Games: " + getRemotePlayer().getWins() + "/" + getRemotePlayer().getDraws() + "/";
		stats += getRemotePlayer().getLosses() + " (Win/Draw/Loss)" + "</center></HTML>";
		remoteStats.setText(stats);
	}
	
	/**Will update the action status in the event
	 * of a player winning the game.
	 * 
	 * @param p Player which has won the game.
	 */
	private void winStats(Player p) {
		String msg = p.getName() + " has won!";
		setStats(msg);
	}
	
	/**Will set the action status in the event
	 * of the game ending in a draw.
	 */
	private void drawStats() {
		String msg = "It's a Draw!";
		setStats(msg);
	}
	
	/** Updates the action status based on actions performed
	 * by the indicated player.
	 * 
	 * @param a
	 * @param p
	 */
	private void actionStats(Action a, Player p) {
		if (a instanceof MoveAction) {
			MoveAction ma = (MoveAction) a;
			String msg = p.getName() + " clicked ";
			msg += "(" + ma.getX() + ", " + ma.getY() + ")";
			setStats(msg);
		}
		if (a instanceof MessageAction) {
			MessageAction ma = (MessageAction) a;
			String msg = "";
			
			if (ma.getMessage().equals("#surrender")) {
				msg = p.getName() + " has surrendered!";
			} else if (ma.getMessage().equals("#rematch")) {
				msg = p.getName() + " wants to play again.";
			} else if (ma.getMessage().equals("#end")) {
				msg = p.getName() + " did not want to play again";
			}
			
			setStats(msg);
		}
	}
	
	/**Sets the action status based on the provide string
	 * 
	 * @param msg
	 */
	private void setStats(String msg) {
		gameStats.setText(msg);
	}
	
	/**Updates the information on the 
	 * buttons on the control panel.
	 */
	private void updateControl() {
		control1.setEnabled(true);
		control2.setEnabled(true);
		switch (getSession()) {
		case NoGame:
			switch (getSessionNoGame()) {
			case Idle:
				control1.setText("Find Player");
				control2.setText("Leave Session");
				control2.setEnabled(false);
				break;
			case Searching:
				control1.setText("Stop Search");
				control2.setText("Leave Session");
				control2.setEnabled(false);
				break;
			default:
				break;
			}
			break;
		case InGame:
			control1.setText("Surrender");
			control2.setText("Leave Session");
			control2.setEnabled(false);
			break;
		case EndGame:
			control1.setText("Rematch?");
			control2.setText("Leave Session");
			break;
		}
	}

	// *************************************************************************
	//
	// Game Inspectors
	//
	// *************************************************************************

	/**Checks if a player has won the game.
	 * 
	 * @return
	 */
	private boolean isGameOver() {
		boolean over = false;
		Player p;
		if (getSessionInGame() == SessionInGame.LocalTurn) {
			p = getLocalPlayer();
		} else {
			p = getRemotePlayer();
		}
		
		over = playerTriple(0, 1, 2, p);
		if (!over)
			over = playerTriple(3, 4, 5, p);
		if (!over)
			over = playerTriple(6, 7, 8, p);
		if (!over)
			over = playerTriple(0, 3, 6, p);
		if (!over)
			over = playerTriple(1, 4, 7, p);
		if (!over)
			over = playerTriple(2, 5, 8, p);
		if (!over)
			over = playerTriple(0, 4, 8, p);
		if (!over)
			over = playerTriple(2, 4, 6, p);

		return over;
	}

	/**Helper methods for isGameOver
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @param p
	 * @return
	 */
	
	private boolean playerTriple(int a, int b, int c, Player p) {
		return playerSquare(a, p) && playerSquare(b, p) && playerSquare(c, p);
	}

	/**Helper method for playerTriple()
	 * 
	 * @param index
	 * @param p
	 * @return
	 */
	private boolean playerSquare(int index, Player p) {
		if (getSquare(index) != null && p != null) {
			if (getSquare(index).getToken() == p.getToken()) {
				return true;
			}
		}
		return false;
	}
	
	/**Finds an un-clicked Square on the board.
	 * 
	 * @return
	 */
	public boolean isFreeSquare() {
		boolean res = false;

		for (int i = 0; i < numberOfSquares() && !res; i++) {
			res = getSquare(i).getClickablility() != Square.Clickablility.Clicked;
		}

		return res;
	}

	// *************************************************************************
	//
	// Outcome Handling
	//
	// *************************************************************************

	/**Updates the Players status attributes in the event of the local player winning.
	 * 
	 * @return
	 */
	private boolean setLocalWin() {
		if (getLocalPlayer() == null || getRemotePlayer() == null) return false;
		getLocalPlayer().setWins(getLocalPlayer().getWins() + 1);
		getRemotePlayer().setLosses(getRemotePlayer().getLosses() + 1);
		return true;
	}

	/**Updates the Players status attributes in the event of the remote player winning.
	 * 
	 * @return
	 */
	private boolean setLocalLoss() {
		if (getLocalPlayer() == null || getRemotePlayer() == null) return false;
		getLocalPlayer().setLosses(getLocalPlayer().getLosses() + 1);
		getRemotePlayer().setWins(getRemotePlayer().getWins() + 1);
		return true;
	}

	/**Updates the Players status attributes in the event of a draw
	 * 
	 * @return
	 */
	private boolean setLocalDraw() {
		if (getLocalPlayer() == null || getRemotePlayer() == null) return false;
		getLocalPlayer().setDraws(getLocalPlayer().getDraws() + 1);
		getRemotePlayer().setDraws(getRemotePlayer().getDraws() + 1);
		return true;
	}

	// *************************************************************************
	//
	// Player action handling
	//
	// *************************************************************************
	
	/**Will handle an action that is given from the remote user
	 * that is sent to the local user.  Can be the location of the
	 * remote players move, or a player choice to quit, rematch, or surrender.
	 * 
	 * @param e
	 */
	public void remoteActionPerformed(Action e) {
		Player remote = getRemotePlayer();
		
		switch (getSession()) {
		case InGame:
			if (e instanceof MoveAction && getSessionInGame() == SessionInGame.RemoteTurn) {
				MoveAction move = (MoveAction) e;
				Square sqr = getSquare(move.getX(), move.getY());
				sqr.setToken(remote.getToken());
				sqr.unlock();
				sqr.click();
				remote.addAction(move);
				actionStats(e, remote);
				remoteActed();
			} else if (e instanceof MessageAction) {
				MessageAction msg = (MessageAction) e;
				if (msg.equals("#surrender")) {
					actionStats(e, remote);
					remoteQuit();
				}
			} else {
				//NOTHING
			}
			break;
		case EndGame:
			if (e instanceof MessageAction) {
				actionStats(e, remote);
			}
			default:
				break;
		}
	}
	
	/**Handles any action made by the local player from a click
	 * on one of the game buttons.  All actions are processed, and
	 * sent to the remote player in the form of a Action.
	 * 
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source instanceof Square) {
			Square sqr = (Square) source;
			
			switch (getSession()) {
			case InGame:
				switch (getSessionInGame()) {
				case LocalTurn:
					if (sqr.getClickablility() == Square.Clickablility.Clickable) {
						sqr.setToken(getLocalPlayer().getToken());
						sqr.click();
						MoveAction ma = new MoveAction(new Time(
								System.currentTimeMillis()), sqr.getPosX(), sqr.getPosY());
						getLocalPlayer().addAction(ma);
						client.handleMessageFromClientUI(ma);
						actionStats(ma, getLocalPlayer());
						localActed();
					}
					break;
				default:
					break;
				}
				break;
			default:
				break;
			}
		} else if (source instanceof JButton) {
			JButton btn = (JButton) source;
			
			switch (getSession()) {
			case NoGame:
				switch (getSessionNoGame()) {
				case Idle:
					if (btn == control1) {
						client.handleMessageFromClientUI("#search");
						startSearch();
					}
					break;
				case Searching:
					if (btn == control1) {
						client.handleMessageFromClientUI("#stop_search");
						stopSearch();
					}
					break;
					default:
						break;
				}
				
				if (btn == control2) {
					client.handleMessageFromClientUI("#logoff");
					setSessionNoGame(SessionNoGame.Idle);
				}
				break;
			case InGame:
				if (btn == control1) {
					MessageAction ma = new MessageAction(new Time(System.currentTimeMillis()), "#surrender");
					getLocalPlayer().addAction(ma);
					client.handleMessageFromClientUI(ma);
					client.handleMessageFromClientUI("#surrender");
					actionStats(ma, getLocalPlayer());
					localQuit();
				}
				break;
			case EndGame:
				if (btn == control1) {
					MessageAction ma = new MessageAction(new Time(System.currentTimeMillis()), "#rematch");
					getLocalPlayer().addAction(ma);
					client.handleMessageFromClientUI(ma);
					client.handleMessageFromClientUI("#rematch");	
					actionStats(ma, getLocalPlayer());
					btn.setEnabled(false);
				} else if (btn == control2) {
					MessageAction ma = new MessageAction(new Time(System.currentTimeMillis()), "#end");
					getLocalPlayer().addAction(ma);
					client.handleMessageFromClientUI(ma);
					client.handleMessageFromClientUI("#end");
					actionStats(ma, getLocalPlayer());
					localQuit();
				}
				break;
			default:
				break;
			}
			
			if (btn == quitButton) {
				client.handleMessageFromClientUI("#quit");
			}
		}
	}

	public String toString() {
		String outputString = "";
		return super.toString()
				+ "  "
				+ "localPlayer"
				+ "="
				+ (getLocalPlayer() != null ? !getLocalPlayer().equals(this) ? getLocalPlayer()
						.toString().replaceAll("  ", "    ") : "this"
						: "null")
				+ System.getProperties().getProperty("line.separator")
				+ "  "
				+ "remotePlayer"
				+ "="
				+ (getRemotePlayer() != null ? !getRemotePlayer().equals(this) ? getRemotePlayer()
						.toString().replaceAll("  ", "    ") : "this"
						: "null") + outputString;
	}
}