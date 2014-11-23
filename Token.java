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