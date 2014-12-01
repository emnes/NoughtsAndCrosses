import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * Square on the noughts and crosses grid.
 *
 * @author Alexander Dale (6839825)
 * @author Mazhar Shar (7987495)
 * @version November 2014
 */

@SuppressWarnings("serial")
public class Square extends JButton {
	// Different image is displayed depending on if a square can be clicked or not.
	public static final String CLICKABLE = "data\\clickable.png";
	public static final String UNCLICKABLE = "data\\unclickable.png";

	// ------------------------
	// MEMBER VARIABLES
	// ------------------------

	// Square Attributes
	// Coordinates on grid.
	private int posX;
	private int posY;

	// Square State Machines
	enum Clickablility {
		Clickable, NotClickable, Clicked
	}

	private Clickablility clickablility;

	// Square Associations
	private Token token;
	private NoughtsAndCrossesGame noughtsAndCrossesGame;

	// ------------------------
	// CONSTRUCTOR
	// ------------------------

	public Square(int aPosX, int aPosY,
			NoughtsAndCrossesGame aNoughtsAndCrossesGame) {
		posX = aPosX;
		posY = aPosY;
		boolean didAddNoughtsAndCrossesGame = setNoughtsAndCrossesGame(aNoughtsAndCrossesGame);
		if (!didAddNoughtsAndCrossesGame) {
			throw new RuntimeException(
					"Unable to create square due to noughtsAndCrossesGame");
		}
		// Square is initially clickable.
		setClickablility(Clickablility.Clickable);
		// Noughts and crosses game will listen for when user clicks on square.
		addActionListener(aNoughtsAndCrossesGame);
	}

	// ------------------------
	// INTERFACE
	// ------------------------

	public boolean setPosX(int aPosX) {
		boolean wasSet = false;
		posX = aPosX;
		wasSet = true;
		return wasSet;
	}

	public boolean setPosY(int aPosY) {
		boolean wasSet = false;
		posY = aPosY;
		wasSet = true;
		return wasSet;
	}

	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}

	public String getClickablilityFullName() {
		String answer = clickablility.toString();
		return answer;
	}

	public Clickablility getClickablility() {
		return clickablility;
	}
	/**
	 * Square has been clicked. Sets clickability to clicked.
	 * @return
	 * 		Whether the click was processed.
	 */
	public boolean click() {
		boolean wasEventProcessed = false;

		Clickablility aClickablility = clickablility;
		switch (aClickablility) {
		case Clickable:
			setClickablility(Clickablility.Clicked);
			wasEventProcessed = true;
			break;
		default:
			// Other states do respond to this event
		}

		return wasEventProcessed;
	}
	
	/**
	 * Lock square when it is opponent's turn.
	 * @return
	 * 		Whether the square was locked successfully.
	 */
	public boolean lock() {
		boolean wasEventProcessed = false;

		Clickablility aClickablility = clickablility;
		switch (aClickablility) {
		case Clickable:
			setClickablility(Clickablility.NotClickable);
			wasEventProcessed = true;
			break;
		default:
			// Other states do respond to this event
		}

		return wasEventProcessed;
	}
	
	/**
	 * Reset clickability of square and allow square to be clicked again regardless of previous state.
	 * @return
	 * 		Whether reset was successful.
	 */
	public boolean reset() {
		boolean wasEventProcessed = false;

		Clickablility aClickablility = clickablility;
		switch (aClickablility) {
		case NotClickable:
			setClickablility(Clickablility.Clickable);
			wasEventProcessed = true;
			break;
		case Clicked:
			setClickablility(Clickablility.Clickable);
			wasEventProcessed = true;
			break;
		default:
			// Other states do respond to this event
		}

		return wasEventProcessed;
	}
	/**
	 * Unlock square when it is player's turn.
	 * @return
	 * 		Whether the square was unlocked successfully.
	 */
	public boolean unlock() {
		boolean wasEventProcessed = false;

		Clickablility aClickablility = clickablility;
		switch (aClickablility) {
		case NotClickable:
			setClickablility(Clickablility.Clickable);
			wasEventProcessed = true;
			break;
		default:
			// Other states do respond to this event
		}

		return wasEventProcessed;
	}

	private void setClickablility(Clickablility aClickablility) {
		clickablility = aClickablility;

		// entry actions and do activities
		switch (clickablility) {
		case Clickable:
			// line 285 "model.ump"
			setClickableImage();
			break;
		case NotClickable:
			// line 290 "model.ump"
			setNotClickableImage();
			break;
		case Clicked:
			// line 295 "model.ump"
			setClickedImage();
			break;
		}
	}

	public Token getToken() {
		return token;
	}

	public boolean hasToken() {
		boolean has = token != null;
		return has;
	}

	public NoughtsAndCrossesGame getNoughtsAndCrossesGame() {
		return noughtsAndCrossesGame;
	}

	public boolean setToken(Token aNewToken) {
		boolean wasSet = false;
		token = aNewToken;
		wasSet = true;
		return wasSet;
	}

	public boolean setNoughtsAndCrossesGame(
			NoughtsAndCrossesGame aNoughtsAndCrossesGame) {
		boolean wasSet = false;
		// Must provide noughtsAndCrossesGame to square
		if (aNoughtsAndCrossesGame == null) {
			return wasSet;
		}

		// noughtsAndCrossesGame already at maximum (9)
		if (aNoughtsAndCrossesGame.numberOfSquares() >= NoughtsAndCrossesGame
				.maximumNumberOfSquares()) {
			return wasSet;
		}

		NoughtsAndCrossesGame existingNoughtsAndCrossesGame = noughtsAndCrossesGame;
		noughtsAndCrossesGame = aNoughtsAndCrossesGame;
		if (existingNoughtsAndCrossesGame != null
				&& !existingNoughtsAndCrossesGame
						.equals(aNoughtsAndCrossesGame)) {
			boolean didRemove = existingNoughtsAndCrossesGame
					.removeSquare(this);
			if (!didRemove) {
				noughtsAndCrossesGame = existingNoughtsAndCrossesGame;
				return wasSet;
			}
		}
		noughtsAndCrossesGame.addSquare(this);
		wasSet = true;
		return wasSet;
	}

	public void delete() {
		token = null;
		NoughtsAndCrossesGame placeholderNoughtsAndCrossesGame = noughtsAndCrossesGame;
		this.noughtsAndCrossesGame = null;
		placeholderNoughtsAndCrossesGame.removeSquare(this);
	}

	// line 301 "model.ump"
	/**
	 * Change the image of the square to represent whether it is clickable, not clickable or clicked.
	 * @return
	 * 		Whether image was changed successfully.
	 */
	public boolean changeImage(String imageURL) {
		try {
			this.setIcon(new ImageIcon(Square.class.getResource(imageURL)));

			return true;
		} catch (Exception ioe) {
			System.out.println("Image not found: " + imageURL);
			return false;
		}
	}

	// line 307 "model.ump"
	public boolean setClickedImage() {
		return changeImage(getToken().getImageUrl());
	}

	// line 313 "model.ump"
	public boolean setClickableImage() {
		return changeImage(CLICKABLE);
	}

	// line 319 "model.ump"
	public boolean setNotClickableImage() {
		return changeImage(UNCLICKABLE);
	}

	public String toString() {
		String outputString = "";
		return super.toString()
				+ "["
				+ "posX"
				+ ":"
				+ getPosX()
				+ ","
				+ "posY"
				+ ":"
				+ getPosY()
				+ "]"
				+ System.getProperties().getProperty("line.separator")
				+ "  "
				+ "token = "
				+ (getToken() != null ? Integer.toHexString(System
						.identityHashCode(getToken())) : "null")
				+ System.getProperties().getProperty("line.separator")
				+ "  "
				+ "noughtsAndCrossesGame = "
				+ (getNoughtsAndCrossesGame() != null ? Integer
						.toHexString(System
								.identityHashCode(getNoughtsAndCrossesGame()))
						: "null") + outputString;
	}
}