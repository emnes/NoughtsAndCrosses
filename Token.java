import java.util.*;

// line 160 "model.ump"
// line 228 "model.ump"
public class Token {
	
	public static final String NOUGHT = "data\\nought.png";
	public static final String CROSS = "data\\cross.png";

	// ------------------------
	// MEMBER VARIABLES
	// ------------------------

	// Token Attributes
	private String imageUrl;

	// Token Associations
	private Player player;
	private List<Square> squares;

	// ------------------------
	// CONSTRUCTOR
	// ------------------------

	public Token(String aImageUrl, Player aPlayer) {
		imageUrl = aImageUrl;
		boolean didAddPlayer = setPlayer(aPlayer);
		if (!didAddPlayer) {
			throw new RuntimeException("Unable to create token due to player");
		}
		squares = new ArrayList<Square>();
	}

	// ------------------------
	// INTERFACE
	// ------------------------

	public boolean setImageUrl(String aImageUrl) {
		boolean wasSet = false;
		imageUrl = aImageUrl;
		wasSet = true;
		return wasSet;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public Player getPlayer() {
		return player;
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

	public boolean setPlayer(Player aNewPlayer) {
		boolean wasSet = false;
		if (aNewPlayer == null) {
			// Unable to setPlayer to null, as token must always be associated
			// to a player
			return wasSet;
		}

		Token existingToken = aNewPlayer.getToken();
		if (existingToken != null && !equals(existingToken)) {
			// Unable to setPlayer, the current player already has a token,
			// which would be orphaned if it were re-assigned
			return wasSet;
		}

		Player anOldPlayer = player;
		player = aNewPlayer;
		player.setToken(this);

		if (anOldPlayer != null) {
			anOldPlayer.setToken(null);
		}
		wasSet = true;
		return wasSet;
	}

	public static int minimumNumberOfSquares() {
		return 0;
	}

	public boolean addSquare(Square aSquare) {
		boolean wasAdded = false;
		if (squares.contains(aSquare)) {
			return false;
		}
		Token existingToken = aSquare.getToken();
		if (existingToken == null) {
			aSquare.setToken(this);
		} else if (!this.equals(existingToken)) {
			existingToken.removeSquare(aSquare);
			addSquare(aSquare);
		} else {
			squares.add(aSquare);
		}
		wasAdded = true;
		return wasAdded;
	}

	public boolean removeSquare(Square aSquare) {
		boolean wasRemoved = false;
		if (squares.contains(aSquare)) {
			squares.remove(aSquare);
			aSquare.setToken(null);
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
		Player existingPlayer = player;
		player = null;
		if (existingPlayer != null) {
			existingPlayer.setToken(null);
		}
		while (!squares.isEmpty()) {
			squares.get(0).setToken(null);
		}
	}

	public String toString() {
		String outputString = "";
		return super.toString()
				+ "["
				+ "imageUrl"
				+ ":"
				+ getImageUrl()
				+ "]"
				+ System.getProperties().getProperty("line.separator")
				+ "  "
				+ "player = "
				+ (getPlayer() != null ? Integer.toHexString(System
						.identityHashCode(getPlayer())) : "null")
				+ outputString;
	}
}
