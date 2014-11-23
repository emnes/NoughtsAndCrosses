import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Time;
import java.util.*;

import javax.swing.*;

// line 2 "model.ump"
// line 379 "model.ump"
@SuppressWarnings("serial")
public class NoughtsAndCrossesGame extends JFrame implements ActionListener {
	
	public static boolean TESTMODE = true;
	
	// ------------------------
	// STATIC VARIABLES
	// ------------------------

	private static NoughtsAndCrossesGame theInstance = null;

	// ------------------------
	// MEMBER VARIABLES
	// ------------------------

	// NoughtsAndCrossesGame Attributes
	private String connectionIP;
	private int portNumber;
	private Player localPlayer;
	private Player remotePlayer;

	// GUI Attributes
	JPanel mainBoard, board, scoreBoard, control;
	JButton control1, control2, quitButton;

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
		connectionIP = null;
		squares = new ArrayList<Square>();
		startUp();
		setSessionNoGame(SessionNoGame.Null);
		setSessionInGame(SessionInGame.Null);
		setSessionEndGame(SessionEndGame.Null);
		setSession(Session.NoGame);
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

	public boolean setConnectionIP(String aConnectionIP) {
		boolean wasSet = false;
		connectionIP = aConnectionIP;
		wasSet = true;
		return wasSet;
	}

	public boolean setPortNumber(int aPortNumber) {
		boolean wasSet = false;
		portNumber = aPortNumber;
		wasSet = true;
		return wasSet;
	}

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

	public String getConnectionIP() {
		return connectionIP;
	}

	public int getPortNumber() {
		return portNumber;
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

	public boolean localQuit() {
		boolean wasEventProcessed = false;

		Session aSession = session;
		switch (aSession) {
		case InGame:
			exitSession();
			// line 37 "model.ump"
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

	public boolean remoteQuit() {
		boolean wasEventProcessed = false;

		Session aSession = session;
		switch (aSession) {
		case InGame:
			exitSession();
			// line 38 "model.ump"
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

	boolean localStartsFlag = false;

	// line 228 "model.ump"
	public boolean localStarts() {
		return localStartsFlag;
	}

	public void setLocalStarts(boolean value) {
		localStartsFlag = value;
	}

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

	public boolean startTurn() {
		boolean wasEventProcessed = false;

		SessionInGame aSessionInGame = sessionInGame;
		switch (aSessionInGame) {
		case NoTurn:
			if (localStarts()) {
				setSessionInGame(SessionInGame.LocalTurn);
				wasEventProcessed = true;
				break;
			}
			if (!localStarts()) {
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
				exitSession();
				setSessionEndGame(SessionEndGame.Draw);
				wasEventProcessed = true;
				break;
			}
			if (isGameOver()) {
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
				exitSession();
				setSessionEndGame(SessionEndGame.Draw);
				wasEventProcessed = true;
				break;
			}
			if (isGameOver()) {
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

	private boolean exitEndGame() {
		boolean wasEventProcessed = false;

		SessionEndGame aSessionEndGame = sessionEndGame;
		switch (aSessionEndGame) {
		case LocalWin:
			setSessionEndGame(SessionEndGame.Null);
			wasEventProcessed = true;
			break;
		case RemoteWin:
			setSessionEndGame(SessionEndGame.Null);
			wasEventProcessed = true;
			break;
		case Draw:
			setSessionEndGame(SessionEndGame.Null);
			wasEventProcessed = true;
			break;
		default:
			// Other states do respond to this event
		}

		return wasEventProcessed;
	}

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

	private void setSession(Session aSession) {
		session = aSession;

		// entry actions and do activities
		switch (session) {
		case NoGame:
			// line 13 "model.ump"
			lockSquares();
			setRemotePlayer(null);
			if (sessionNoGame == SessionNoGame.Null) {
				setSessionNoGame(SessionNoGame.Idle);
			}
			break;
		case InGame:
			// line 31 "model.ump"
			resetSquares();
			updateStats();
			updateDisplay();
			if (sessionInGame == SessionInGame.Null) {
				setSessionInGame(SessionInGame.NoTurn);
			}
			break;
		case EndGame:
			// line 59 "model.ump"
			lockSquares();
			updateDisplay();
			if (!TESTMODE) client.handleMessageFromClientUI("#game_over");
			if (sessionEndGame == SessionEndGame.Null) {
				setSessionEndGame(SessionEndGame.LocalWin);
			}
			break;
		}
	}

	private void setSessionNoGame(SessionNoGame aSessionNoGame) {
		sessionNoGame = aSessionNoGame;
		if (session != Session.NoGame && aSessionNoGame != SessionNoGame.Null) {
			setSession(Session.NoGame);
		}

		// entry actions and do activities
		switch (sessionNoGame) {
		case Idle:
			// line 17 "model.ump"
			updateDisplay();
			break;
		case Searching:
			// line 23 "model.ump"
			updateDisplay();
			break;
		default:
			break;
		}
	}

	private void setSessionInGame(SessionInGame aSessionInGame) {
		sessionInGame = aSessionInGame;
		if (session != Session.InGame && aSessionInGame != SessionInGame.Null) {
			setSession(Session.InGame);
		}

		// entry actions and do activities
		switch (sessionInGame) {
		case LocalTurn:
			// line 45 "model.ump"
			unlockSquares();
			break;
		case RemoteTurn:
			// line 51 "model.ump"
			lockSquares();
			break;
		default:
			break;
		}
	}

	private void setSessionEndGame(SessionEndGame aSessionEndGame) {
		sessionEndGame = aSessionEndGame;
		if (session != Session.EndGame
				&& aSessionEndGame != SessionEndGame.Null) {
			setSession(Session.EndGame);
		}

		// entry actions and do activities
		switch (sessionEndGame) {
		case LocalWin:
			// line 69 "model.ump"
			setLocalWin();
			break;
		case RemoteWin:
			// line 74 "model.ump"
			setLocalLoss();
			break;
		case Draw:
			// line 79 "model.ump"
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

	public Square getSquare(int index) {
		Square aSquare = squares.get(index);
		return aSquare;
	}
	
	public Square getSquare(int x, int y) {
		Square aSquare = squares.get(x + 3*y);
		return aSquare;
	}

	public List<Square> getSquares() {
		List<Square> newSquares = Collections.unmodifiableList(squares);
		return newSquares;
	}

	public int numberOfSquares() {
		int number = squares.size();
		return number;
	}

	public boolean hasSquares() {
		boolean has = squares.size() > 0;
		return has;
	}

	public int indexOfSquare(Square aSquare) {
		int index = squares.indexOf(aSquare);
		return index;
	}

	public static int minimumNumberOfSquares() {
		return 0;
	}

	public static int maximumNumberOfSquares() {
		return 9;
	}

	public Square addSquare(int aPosX, int aPosY) {
		if (numberOfSquares() >= maximumNumberOfSquares()) {
			return null;
		} else {
			return new Square(aPosX, aPosY, this);
		}
	}

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

	public boolean addOrMoveSquareAt(Square aSquare, int index) {
		boolean wasAdded = false;
		if (squares.contains(aSquare)) {
			if (index < 0) {
				index = 0;
			}
			if (index > numberOfSquares()) {
				index = numberOfSquares() - 1;
			}
			squares.remove(aSquare);
			squares.add(index, aSquare);
			wasAdded = true;
		} else {
			wasAdded = addSquareAt(aSquare, index);
		}
		return wasAdded;
	}

	public void delete() {
		for (int i = squares.size(); i > 0; i--) {
			Square aSquare = squares.get(i - 1);
			aSquare.delete();
		}
	}

	// line 74 "model.ump"
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

	// line 87 "model.ump"
	public void startUp() {
		mainBoard = new JPanel();
		add(mainBoard);

		board = new JPanel();
		board.setLayout(new GridLayout(3, 3));
		mainBoard.add(board, BorderLayout.WEST);

		generateSquares();

		scoreBoard = new JPanel();
		scoreBoard.setLayout(new GridLayout(2, 1));
		mainBoard.add(scoreBoard, BorderLayout.EAST);

		control = new JPanel();
		control.setLayout(new GridLayout(1, 3));
		mainBoard.add(control, BorderLayout.SOUTH);

		control1 = new JButton();
		control2 = new JButton();
		quitButton = new JButton();
		control1.addActionListener(this);
		control2.addActionListener(this);
		quitButton.addActionListener(this);
		control.add(control1);
		control.add(control2);
		control.add(quitButton);
		control1.setText("Find Player");
		control2.setText("Reset");
		quitButton.setText("Quit");

		pack();
	}

	// line 122 "model.ump"
	public void generateSquares() {
		board.removeAll();
		board.setLayout(new GridLayout(3, 3));

		removeSquares();

		for (int y = 1; y <= 3; y++) {
			for (int x = 1; x <= 3; x++) {
				Square temp = addSquare(x, y);
				board.add(temp);
			}
		}

		lockSquares();
	}

	// line 138 "model.ump"
	public boolean unlockSquares() {
		for (int i = 0; i < numberOfSquares(); i++) {
			getSquare(i).unlock();
		}

		return true;
	}

	// line 146 "model.ump"
	public boolean lockSquares() {
		for (int i = 0; i < numberOfSquares(); i++) {
			getSquare(i).lock();
		}

		return true;
	}

	// line 154 "model.ump"
	public boolean resetSquares() {
		for (int i = 0; i < numberOfSquares(); i++) {
			getSquare(i).reset();
		}

		return true;
	}

	// line 234 "model.ump"
	public void updateStats() {
		System.out.println("Implement me! - updateStats()");
	}

	public void updateDisplay() {
		control1.setEnabled(true);
		control2.setEnabled(true);
		switch (getSession()) {
		case NoGame:
			switch (getSessionNoGame()) {
			case Idle:
				control1.setText("Find Player");
				control2.setText("Log off");
				break;
			case Searching:
				control1.setText("Stop Search");
				control2.setText("Log off");
				break;
			default:
				break;
			}
			break;
		case InGame:
			control1.setText("Surrender");
			control2.setText("No function");
			control2.setEnabled(TESTMODE);
			break;
		case EndGame:
			control1.setText("Rematch?");
			control2.setText("Leave Session");
			break;
		}
		pack();
	}

	// *************************************************************************
	//
	// Game Inspectors
	//
	// *************************************************************************

	// line 162 "model.ump"
	public boolean isGameOver() {
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

		if (getSessionInGame() == SessionInGame.LocalTurn) {
			if (over)
				System.out.println("Local has Won!!");
		} else {
			if (over)
				System.out.println("Remote has Won!!");
		}

		return over;
	}

	// line 189 "model.ump"
	private boolean playerTriple(int a, int b, int c, Player p) {
		return playerSquare(a, p) && playerSquare(b, p) && playerSquare(c, p);
	}

	// line 193 "model.ump"
	private boolean playerSquare(int index, Player p) {
		if (getSquare(index) != null) {
			if (getSquare(index).getToken() != null) {
				if (getSquare(index).getToken().getImageUrl()
						.equals(p.getToken().getImageUrl())) {
					return true;
				}
			}
		}
		return false;
	}

	// line 204 "model.ump"
	public boolean isFreeSquare() {
		boolean res = true;

		for (int i = 0; i < numberOfSquares() && res; i++) {
			res = getSquare(i).getClickablility() == Square.Clickablility.Clickable;
		}

		return res;
	}

	// *************************************************************************
	//
	// Outcome Handling
	//
	// *************************************************************************

	// line 210 "model.ump"
	public boolean setLocalWin() {
		if (getLocalPlayer() == null || getRemotePlayer() == null) return false;
		getLocalPlayer().setWins(getLocalPlayer().getWins() + 1);
		getRemotePlayer().setLosses(getRemotePlayer().getLosses() + 1);
		return true;
	}

	// line 216 "model.ump"
	public boolean setLocalLoss() {
		if (getLocalPlayer() == null || getRemotePlayer() == null) return false;
		getLocalPlayer().setLosses(getLocalPlayer().getLosses() + 1);
		getRemotePlayer().setWins(getRemotePlayer().getWins() + 1);
		return true;
	}

	// line 222 "model.ump"
	public boolean setLocalDraw() {
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

	public void remoteActionPerformed(Action e) {
		switch (getSession()) {
		case InGame:
			if (e instanceof MoveAction) {
				//TODO
			}
			break;
			default:
				break;
		}
	}

	// line 238 "model.ump"
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
								System.currentTimeMillis()), sqr.getX(), sqr.getY());
						getLocalPlayer().addAction(ma);
						if (!TESTMODE) client.handleMessageFromClientUI(ma);
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
						if (!TESTMODE) client.handleMessageFromClientUI("#search");
						startSearch();
					}
					break;
				case Searching:
					if (btn == control1) {
						if (!TESTMODE) client.handleMessageFromClientUI("#stop_search");
						stopSearch();
					}
					break;
					default:
						break;
				}
				
				if (btn == control2 && !TESTMODE) {
					client.handleMessageFromClientUI("#logoff");
					setSessionNoGame(SessionNoGame.Idle);
				} else if (btn == control2 && TESTMODE) {
					playerConnected();
					setLocalStarts(true);
					startTurn();
					System.out.println("Special Button");
				}
				break;
			case InGame:
				if (btn == control1) {
					MessageAction ma = new MessageAction(new Time(System.currentTimeMillis()), "#surrender");
					getLocalPlayer().addAction(ma);
					if (!TESTMODE) client.handleMessageFromClientUI(ma);
					if (!TESTMODE) client.handleMessageFromClientUI("#surrender");
					localQuit();
				} else if (btn == control2 && TESTMODE) { //REMOVE THIS BEFOR RELEASE
					remoteActed();
				}
				break;
			case EndGame:
				if (btn == control1) {
					if (!TESTMODE) client.handleMessageFromClientUI("#rematch");
					btn.setEnabled(false);
				} else if (btn == control2) {
					if (!TESTMODE) client.handleMessageFromClientUI("#end");
					localQuit();
				}
				break;
			default:
				break;
			}
			
			if (btn == quitButton) {
				if (!TESTMODE) client.handleMessageFromClientUI("#quit");
				else System.exit(0);
			}
		}
	}

	public String toString() {
		String outputString = "";
		return super.toString()
				+ "["
				+ "connectionIP"
				+ ":"
				+ getConnectionIP()
				+ ","
				+ "portNumber"
				+ ":"
				+ getPortNumber()
				+ "]"
				+ System.getProperties().getProperty("line.separator")
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