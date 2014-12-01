import java.io.Serializable;
import java.util.*;


/**
 * Player object from Noughts and crosses.  Stores the
 * player stats, and can be passed to and from the server
 * to keep both server and client up to date.
 *
 * @author Alexander Dale (6839825)
 * @author Mazhar Shar (7987495)
 * @version November 2014
 */

@SuppressWarnings("serial")
public class Player implements Serializable {

	// ------------------------
	// MEMBER VARIABLES
	// ------------------------

	// Player Attributes
	private String name;
	private int playerId;
	private int wins;
	private int losses;
	private int draws;
	private String expLvl;

	// Player Associations
	private Token token;
	private List<Action> actions;

	// ------------------------
	// CONSTRUCTOR
	// ------------------------

	public Player(String aName, int aPlayerId, int aWins, int aLosses,
			int aDraws) {
		name = aName;
		playerId = aPlayerId;
		wins = aWins;
		losses = aLosses;
		draws = aDraws;
		expLvl = null;
		actions = new ArrayList<Action>();
	}

	// ------------------------
	// INTERFACE
	// ------------------------

	public boolean setWins(int aWins) {
		boolean wasSet = false;
		wins = aWins;
		wasSet = true;
		return wasSet;
	}

	public boolean setLosses(int aLosses) {
		boolean wasSet = false;
		losses = aLosses;
		wasSet = true;
		return wasSet;
	}

	public boolean setDraws(int aDraws) {
		boolean wasSet = false;
		draws = aDraws;
		wasSet = true;
		return wasSet;
	}

	public boolean setExpLvl(String aExpLvl) {
		boolean wasSet = false;
		expLvl = aExpLvl;
		wasSet = true;
		return wasSet;
	}

	public String getName() {
		return name;
	}

	public int getPlayerId() {
		return playerId;
	}

	public int getWins() {
		return wins;
	}

	public int getLosses() {
		return losses;
	}

	public int getDraws() {
		return draws;
	}

	public String getExpLvl() {
		return expLvl;
	}

	public Token getToken() {
		return token;
	}

	public boolean hasToken() {
		boolean has = token != null;
		return has;
	}

	public Action getAction(int index) {
		Action aAction = actions.get(index);
		return aAction;
	}

	public List<Action> getActions() {
		List<Action> newActions = Collections.unmodifiableList(actions);
		return newActions;
	}

	public int numberOfActions() {
		int number = actions.size();
		return number;
	}

	public boolean hasActions() {
		boolean has = actions.size() > 0;
		return has;
	}

	public boolean setToken(Token aNewToken) {
		boolean wasSet = false;
		if (token != null && !token.equals(aNewToken)
				&& equals(token.getPlayer())) {
			// Unable to setToken, as existing token would become an orphan
			return wasSet;
		}

		token = aNewToken;
		Player anOldPlayer = aNewToken != null ? aNewToken.getPlayer() : null;

		if (!this.equals(anOldPlayer)) {
			if (anOldPlayer != null) {
				anOldPlayer.token = null;
			}
			if (token != null) {
				token.setPlayer(this);
			}
		}
		wasSet = true;
		return wasSet;
	}

	public static int minimumNumberOfActions() {
		return 0;
	}

	public boolean addAction(Action aAction) {
		boolean wasAdded = false;
		if (actions.contains(aAction)) {
			return false;
		}
		actions.add(aAction);
		wasAdded = true;
		return wasAdded;
	}

	public void delete() {
		Token existingToken = token;
		token = null;
		if (existingToken != null) {
			existingToken.delete();
		}
		actions.clear();
	}


	/**Will calculate the Players experience level based
	 * on the wins, losses and draws of the player and
	 * will store the result in the <code>expLvl</code>.
 	 */
	public void determineExperienceLevel() {
		double ratio;
		if (losses + draws != 0)
			ratio = (double) wins / (losses + draws);
		else
			ratio = (double) wins;

		if (ratio <= 1.0) {
			setExpLvl("Beginner");
		} else if (ratio > 1.0 && ratio <= 3.0) {
			setExpLvl("Intermediate");
		} else {
			setExpLvl("Expert");
		}
	}
	
	/**Checks to see if two players are equal
	 * by comparing playerIds, will also be
	 * equals to a String of the same player name.
	 */
	public boolean equals(Object o) {
		if (o instanceof Player) {
			Player temp = (Player) o;
			return temp.playerId == this.playerId;
		} else if (o instanceof String) {
			String temp = (String) o;
			return temp.equals(this.name);
		}
		return false;
	}

	public String toString() {
		String outputString = "";
		return super.toString()
				+ "["
				+ "name"
				+ ":"
				+ getName()
				+ ","
				+ "playerId"
				+ ":"
				+ getPlayerId()
				+ ","
				+ "wins"
				+ ":"
				+ getWins()
				+ ","
				+ "losses"
				+ ":"
				+ getLosses()
				+ ","
				+ "draws"
				+ ":"
				+ getDraws()
				+ ","
				+ "expLvl"
				+ ":"
				+ getExpLvl()
				+ "]"
				+ System.getProperties().getProperty("line.separator")
				+ "  "
				+ "token = "
				+ (getToken() != null ? Integer.toHexString(System
						.identityHashCode(getToken())) : "null") + outputString;
	}
}