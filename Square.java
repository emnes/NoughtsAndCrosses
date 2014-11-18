import javafx.scene.image.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class Square extends JButton {
	
	public static String getUnclickableImageURL() {
		return "data\\unclickable.png";
	}
	
	public static String getClickableImageURL() {
		return "data\\clickable.png";
	}

	// ------------------------
	// MEMBER VARIABLES
	// ------------------------

	// Square Attributes
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
		setClickablility(Clickablility.Clickable);
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
			// line 120 "model.ump"
			setClickableImage();
			setToken(null);
			break;
		case NotClickable:
			// line 125 "model.ump"
			setNotClickableImage();
			break;
		case Clicked:
			// line 130 "model.ump"
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

	public boolean setToken(Token aToken) {
		boolean wasSet = false;
		Token existingToken = token;
		token = aToken;
		if (existingToken != null && !existingToken.equals(aToken)) {
			existingToken.removeSquare(this);
		}
		if (aToken != null) {
			aToken.addSquare(this);
		}
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
		if (token != null) {
			Token placeholderToken = token;
			this.token = null;
			placeholderToken.removeSquare(this);
		}
		NoughtsAndCrossesGame placeholderNoughtsAndCrossesGame = noughtsAndCrossesGame;
		this.noughtsAndCrossesGame = null;
		placeholderNoughtsAndCrossesGame.removeSquare(this);
	}

	// line 136 "model.ump"
	public boolean changeImage(String imageURL) {
		boolean wasSet = false;
		
		try {
			ImageIcon img = new ImageIcon(Square.class.getResource(imageURL));
			this.setIcon(img);
			wasSet = true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return wasSet;
	}

	// line 142 "model.ump"
	public boolean setClickedImage() {
		boolean wasSet = false;
		if (getToken() != null) {
			changeImage(getToken().getImageUrl());
			wasSet = true;
		}
		
		return wasSet;
	}

	// line 148 "model.ump"
	public boolean setClickableImage() {
		boolean wasSet = false;
		if (getClickablility() == Clickablility.Clickable) {
			changeImage(getClickableImageURL());
			wasSet = true;
		}
		
		return wasSet;
	}

	// line 154 "model.ump"
	public boolean setNotClickableImage() {
		boolean wasSet = false;
		if (getClickablility() == Clickablility.NotClickable) {
			changeImage(getUnclickableImageURL());
			wasSet = true;
		}
		
		return wasSet;
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
