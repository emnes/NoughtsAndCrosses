import java.io.Serializable;

/**
 * Token represents a nought or a cross in a Noughts and Crosses game.
 * 
 * @author Alexander Dale (6839825)
 * @author Mazhar Shar (7987495)
 * @version November 2014
 */

@SuppressWarnings("serial")
public class Token implements Serializable {
	public static final String NOUGHT = "data\\nought.png"; // Image for a nought
	public static final String CROSS = "data\\cross.png"; // Image for a cross


	// ------------------------
	// MEMBER VARIABLES
	// ------------------------

	// Token Attributes
	private String imageUrl;

	// Token Associations
	// Each player has either a CROSS or NOUGHT token.
	private Player player;

	// ------------------------
	// CONSTRUCTOR
	// ------------------------

	public Token(String aImageUrl, Player aPlayer) {
		imageUrl = aImageUrl;
		boolean didAddPlayer = setPlayer(aPlayer);
		if (!didAddPlayer) {
			throw new RuntimeException("Unable to create token due to player");
		}
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
	/**
	 * Changes the player token is assigned to.
	 * @param
	 * 		The player you would like to assign token to.
	 */
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
		// Set current player to new player and set its token.
		player = aNewPlayer;
		player.setToken(this);
		
		// Old player's token is now set to null.
		if (anOldPlayer != null) {
			anOldPlayer.setToken(null);
		}
		wasSet = true;
		return wasSet;
	}
	
	/**
	 * Deletes player.
	 * 
	 */
	public void delete() {
		Player existingPlayer = player;
		player = null;
		if (existingPlayer != null) {
			existingPlayer.setToken(null);
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

	// ------------------------
	// DEVELOPER CODE - PROVIDED AS-IS
	// ------------------------

	// line 329 model.ump
	
}