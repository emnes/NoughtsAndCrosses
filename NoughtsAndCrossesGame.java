import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;


// line 2 "model.ump"
// line 237 "model.ump"
@SuppressWarnings("serial")
public class NoughtsAndCrossesGame extends JFrame implements ActionListener {

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
	
	private JPanel mainBoard;
	private JPanel board;
	private JPanel control;
	private JButton control1, control2;
	private JPanel scoreBoard;
	private JButton quitButton;

	// NoughtsAndCrossesGame State Machines
	enum Session {
		NoGame, InGame
	}

	enum SessionInGame {
		Null, Turn
	}

	enum SessionInGameTurn {
		Null, Local, Remote
	}

	private Session session;
	private SessionInGame sessionInGame;
	private SessionInGameTurn sessionInGameTurn;

	// NoughtsAndCrossesGame Associations
	private List<Square> squares;
	private LocalPlayer localPlayer;
	private RemotePlayer remotePlayer;

	// ------------------------
	// CONSTRUCTOR
	// ------------------------

	private NoughtsAndCrossesGame() {
		connectionIP = null;
		squares = new ArrayList<Square>();
		setSessionInGame(SessionInGame.Null);
		setSessionInGameTurn(SessionInGameTurn.Null);
		setSession(Session.NoGame);
		startUp();
	}

	public static NoughtsAndCrossesGame getInstance() {
		if (theInstance == null) {
			theInstance = new NoughtsAndCrossesGame();
		}
		return theInstance;
	}

	// ------------------------
	// INTERFACE
	// ------------------------

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

	public String getConnectionIP() {
		return connectionIP;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public String getSessionFullName() {
		String answer = session.toString();
		if (sessionInGame != SessionInGame.Null) {
			answer += "." + sessionInGame.toString();
		}
		if (sessionInGameTurn != SessionInGameTurn.Null) {
			answer += "." + sessionInGameTurn.toString();
		}
		return answer;
	}

	public Session getSession() {
		return session;
	}

	public SessionInGame getSessionInGame() {
		return sessionInGame;
	}

	public SessionInGameTurn getSessionInGameTurn() {
		return sessionInGameTurn;
	}

	public boolean startGame() {
		boolean wasEventProcessed = false;

		Session aSession = session;
		switch (aSession) {
		case NoGame:
			if (arePlayersReady()) {
				setSession(Session.InGame);
				wasEventProcessed = true;
				break;
			}
			break;
		default:
			// Other states do respond to this event
		}

		return wasEventProcessed;
	}

	private boolean enterInGame() {
		boolean wasEventProcessed = false;

		SessionInGame aSessionInGame = sessionInGame;
		SessionInGameTurn aSessionInGameTurn = sessionInGameTurn;
		switch (aSessionInGame) {
		case Null:
			setSessionInGame(SessionInGame.Turn);
			wasEventProcessed = true;
			break;
		default:
			// Other states do respond to this event
		}

		switch (aSessionInGameTurn) {
		case Null:
			setSessionInGameTurn(SessionInGameTurn.Local);
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
		SessionInGameTurn aSessionInGameTurn = sessionInGameTurn;
		switch (aSessionInGame) {
		case Turn:
			setSessionInGame(SessionInGame.Null);
			wasEventProcessed = true;
			break;
		default:
			// Other states do respond to this event
		}

		switch (aSessionInGameTurn) {
		case Local:
			setSessionInGameTurn(SessionInGameTurn.Null);
			wasEventProcessed = true;
			break;
		case Remote:
			setSessionInGameTurn(SessionInGameTurn.Null);
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
		case Turn:
			if (localStarts()) {
				setSessionInGameTurn(SessionInGameTurn.Local);
				wasEventProcessed = true;
				break;
			} else {
				setSessionInGameTurn(SessionInGameTurn.Remote);
				wasEventProcessed = true;
				break;
			}
		default:
			// Other states do respond to this event
		}

		return wasEventProcessed;
	}

	public boolean localActed() {
		boolean wasEventProcessed = false;

		SessionInGameTurn aSessionInGameTurn = sessionInGameTurn;
		switch (aSessionInGameTurn) {
		case Local:
			if (!getGameOver()) {
				setSessionInGameTurn(SessionInGameTurn.Remote);
				wasEventProcessed = true;
				break;
			} else {
				exitSessionInGame();
				setSession(Session.NoGame);
				wasEventProcessed = true;
				break;
			}
		default:
			// Other states do respond to this event
		}

		return wasEventProcessed;
	}

	public boolean localQuit() {
		boolean wasEventProcessed = false;

		SessionInGameTurn aSessionInGameTurn = sessionInGameTurn;
		switch (aSessionInGameTurn) {
		case Local:
			exitSessionInGame();
			// line 31 "model.ump"
			setLocalLoss();
			setSession(Session.NoGame);
			wasEventProcessed = true;
			break;
		case Remote:
			exitSessionInGame();
			// line 38 "model.ump"
			setLocalLoss();
			setSession(Session.NoGame);
			wasEventProcessed = true;
			break;
		default:
			// Other states do respond to this event
		}

		return wasEventProcessed;
	}

	public boolean remoteQuit() {
		boolean wasEventProcessed = false;

		SessionInGameTurn aSessionInGameTurn = sessionInGameTurn;
		switch (aSessionInGameTurn) {
		case Local:
			exitSessionInGame();
			// line 32 "model.ump"
			setLocalWin();
			setSession(Session.NoGame);
			wasEventProcessed = true;
			break;
		case Remote:
			exitSessionInGame();
			// line 39 "model.ump"
			setLocalWin();
			setSession(Session.NoGame);
			wasEventProcessed = true;
			break;
		default:
			// Other states do respond to this event
		}

		return wasEventProcessed;
	}

	public boolean remoteActed() {
		boolean wasEventProcessed = false;

		SessionInGameTurn aSessionInGameTurn = sessionInGameTurn;
		switch (aSessionInGameTurn) {
		case Remote:
			if (!getGameOver()) {
				setSessionInGameTurn(SessionInGameTurn.Local);
				wasEventProcessed = true;
				break;
			} else {
				exitSessionInGame();
				setSession(Session.NoGame);
				wasEventProcessed = true;
				break;
			}
		default:
			// Other states do respond to this event
		}

		return wasEventProcessed;
	}

	private void exitSession() {
		switch (session) {
		case InGame:
			exitInGame();
			break;
		}
	}

	private void setSession(Session aSession) {
		session = aSession;

		// entry actions and do activities
		switch (session) {
		case NoGame:
			// line 13 "model.ump"
			lockSquares();
			break;
		case InGame:
			// line 18 "model.ump"
			resetSquares();
			updateStats();
			if (sessionInGame == SessionInGame.Null) {
				setSessionInGame(SessionInGame.Turn);
			}
			break;
		}
	}

	private void exitSessionInGame() {
		switch (sessionInGame) {
		case Turn:
			exitInGame();
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
		case Turn:
			if (sessionInGameTurn == SessionInGameTurn.Null) {
				setSessionInGameTurn(SessionInGameTurn.Local);
			}
			break;
		}
	}

	private void setSessionInGameTurn(SessionInGameTurn aSessionInGameTurn) {
		sessionInGameTurn = aSessionInGameTurn;
		if (sessionInGame != SessionInGame.Turn
				&& aSessionInGameTurn != SessionInGameTurn.Null) {
			setSessionInGame(SessionInGame.Turn);
		}

		// entry actions and do activities
		switch (sessionInGameTurn) {
		case Local:
			// line 28 "model.ump"
			unlockSquares();
			break;
		case Remote:
			// line 35 "model.ump"
			lockSquares();
			break;
		}
	}

	public Square getSquare(int index) {
		Square aSquare = squares.get(index);
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

	public LocalPlayer getLocalPlayer() {
		return localPlayer;
	}

	public boolean hasLocalPlayer() {
		boolean has = localPlayer != null;
		return has;
	}

	public RemotePlayer getRemotePlayer() {
		return remotePlayer;
	}

	public boolean hasRemotePlayer() {
		boolean has = remotePlayer != null;
		return has;
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
	
	public boolean removeSquares() {
		boolean wasRemoved = false;
		
		while (numberOfSquares() != 0) {
			removeSquare(getSquare(0));
		}
		
		wasRemoved = numberOfSquares() != 0;
		
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

	public boolean setLocalPlayer(LocalPlayer aNewLocalPlayer) {
		boolean wasSet = false;
		localPlayer = aNewLocalPlayer;
		wasSet = true;
		return wasSet;
	}

	public boolean setRemotePlayer(RemotePlayer aNewRemotePlayer) {
		boolean wasSet = false;
		remotePlayer = aNewRemotePlayer;
		wasSet = true;
		return wasSet;
	}

	public void delete() {
		for (int i = squares.size(); i > 0; i--) {
			Square aSquare = squares.get(i - 1);
			aSquare.delete();
		}
		localPlayer = null;
		remotePlayer = null;
	}

	// line 47 "model.ump"
	public void startUp() {
		mainBoard = new JPanel();
		add(mainBoard);
		
		board = new JPanel();
		board.setLayout(new GridLayout(3,3));
		mainBoard.add(board, BorderLayout.WEST);
		
		generateSquares();
		
		scoreBoard = new JPanel();
		scoreBoard.setLayout(new GridLayout(2,1));
		mainBoard.add(scoreBoard, BorderLayout.EAST);
		
		control = new JPanel();
		control.setLayout(new GridLayout(1,3));
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

	// line 66 "model.ump"
	public void generateSquares() {
		board.removeAll();
		board.setLayout(new GridLayout(3,3));
		
		removeSquares();
		
		for (int y = 1; y <= 3; y++) {
			for (int x = 1; x <= 3; x++) {
				Square temp = new Square(x, y, this);
				board.add(temp);
			}
		}
		
		lockSquares();
	}

	// line 80 "model.ump"
	public boolean unlockSquares() {
		for (int i = 0; i < numberOfSquares(); i++) {
			getSquare(i).unlock();
		}
		
		return true;
	}

	// line 86 "model.ump"
	public boolean lockSquares() {
		for (int i = 0; i < numberOfSquares(); i++) {
			getSquare(i).lock();
		}
		
		return true;
	}

	// line 92 "model.ump"
	public boolean resetSquares() {
		for (int i = 0; i < numberOfSquares(); i++) {
			getSquare(i).reset();
		}
		
		return true;
	}

	// line 98 "model.ump"
	public boolean getGameOver() {
		boolean over = false;
		Player p;
		if (getSessionInGameTurn() == SessionInGameTurn.Local) {
			p = getLocalPlayer();
		} else {
			p = getRemotePlayer();
		}
		
		over = playerTriple(0, 1, 2, p);
		if (!over) over = playerTriple(3,4,5,p);
		if (!over) over = playerTriple(6,7,8,p);
		if (!over) over = playerTriple(0,3,6,p);
		if (!over) over = playerTriple(1,4,7,p);
		if (!over) over = playerTriple(2,5,8,p);
		if (!over) over = playerTriple(0,4,8,p);
		if (!over) over = playerTriple(2,4,6,p);
		
		if (getSessionInGameTurn() == SessionInGameTurn.Local) {
			if (over) System.out.println("Local has Won!!");
		} else {
			if (over) System.out.println("Remote has Won!!");
		}
		
		return over;
	}
	
	private boolean playerTriple(int a, int b, int c, Player p) {
		return playerSquare(a, p) && playerSquare(b, p) && playerSquare(c, p);
	}
	
	private boolean playerSquare(int index, Player p) {
		if (getSquare(index) != null) {
			if (getSquare(index).getToken() != null) {
				if (getSquare(index).getToken().getImageUrl().equals(p.getToken().getImageUrl())) {
					return true;
				}
			}
		}
		return false;
	}

	// line 104 "model.ump"
	public boolean setLocalWin() {
		System.out.println("Implement me!");

		return false;
	}

	// line 110 "model.ump"
	public boolean setLocalLoss() {
		System.out.println("Implement me!");

		return false;
	}

	// line 116 "model.ump"
	public boolean localStarts() {
		System.out.println("I say local always starts");

		return true;
	}

	// line 122 "model.ump"
	public boolean arePlayersReady() {
		return (getLocalPlayer() != null && getRemotePlayer() != null);
	}

	// line 128 "model.ump"
	public void updateStats() {
		JLabel localStats = new JLabel();
		JLabel remoteStats = new JLabel();
		
		if (getLocalPlayer() != null) {
			
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
				+ "localPlayer = "
				+ (getLocalPlayer() != null ? Integer.toHexString(System
						.identityHashCode(getLocalPlayer())) : "null")
				+ System.getProperties().getProperty("line.separator")
				+ "  "
				+ "remotePlayer = "
				+ (getRemotePlayer() != null ? Integer.toHexString(System
						.identityHashCode(getRemotePlayer())) : "null")
				+ outputString;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		if (source instanceof Square) {
			Square sqr = (Square) source;
			
			if (getSessionInGameTurn() == SessionInGameTurn.Local) {
				if (sqr.getClickablility() == Square.Clickablility.Clickable) {
					sqr.setToken(getLocalPlayer().getToken());
					sqr.click();
					localActed();
					if (!getGameOver()) unlockSquares();
				}
			} else if (getSessionInGameTurn() == SessionInGameTurn.Remote) {
				if (sqr.getClickablility() == Square.Clickablility.Clickable) {
					sqr.setToken(getRemotePlayer().getToken());
					sqr.click();
					remoteActed();
				}
			}
			
		} else if (source instanceof JButton) {
			JButton btn = (JButton) source;
			
			if (btn == control1) {
				
			} else if (btn == control2) {
				startGame();
			} else if (btn == quitButton) {
				System.exit(0);
			}
		}
		
	}
	
	public static void main(String agrs[]) {
		NoughtsAndCrossesGame game = getInstance();
		LocalPlayer lp = new LocalPlayer("Alex", 1, 5, 0, 0);
		RemotePlayer rp = new RemotePlayer("Maz", 2, 0, 0, 0);
		new Token(Token.NOUGHT, rp);
		new Token(Token.CROSS, lp);
		
		game.setLocalPlayer(lp);
		game.setRemotePlayer(rp);
		
		game.setVisible(true);
		game.startGame();
	}
}
