import java.util.*;

// line 165 "model.ump"
// line 233 "model.ump"
public abstract class Player {

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
	private boolean isReady;

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
		determineExperienceLevel();
	}

	// ------------------------
	// INTERFACE
	// ------------------------

	public boolean setName(String aName) {
		boolean wasSet = false;
		name = aName;
		wasSet = true;
		return wasSet;
	}

	public boolean setPlayerId(int aPlayerId) {
		boolean wasSet = false;
		playerId = aPlayerId;
		wasSet = true;
		return wasSet;
	}

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

	public boolean setIsReady(boolean aIsReady) {
		boolean wasSet = false;
		isReady = aIsReady;
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

	public boolean getIsReady() {
		return isReady;
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

	public int indexOfAction(Action aAction) {
		int index = actions.indexOf(aAction);
		return index;
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
		if (actions.contains(aAction)) {
			return false;
		}
		if (actions.contains(aAction)) {
			return false;
		}
		actions.add(aAction);
		wasAdded = true;
		return wasAdded;
	}

	public boolean removeAction(Action aAction) {
		boolean wasRemoved = false;
		if (actions.contains(aAction)) {
			actions.remove(aAction);
			wasRemoved = true;
		}
		return wasRemoved;
	}

	public boolean addActionAt(Action aAction, int index) {
		boolean wasAdded = false;
		if (addAction(aAction)) {
			if (index < 0) {
				index = 0;
			}
			if (index > numberOfActions()) {
				index = numberOfActions() - 1;
			}
			actions.remove(aAction);
			actions.add(index, aAction);
			wasAdded = true;
		}
		return wasAdded;
	}

	public boolean addOrMoveActionAt(Action aAction, int index) {
		boolean wasAdded = false;
		if (actions.contains(aAction)) {
			if (index < 0) {
				index = 0;
			}
			if (index > numberOfActions()) {
				index = numberOfActions() - 1;
			}
			actions.remove(aAction);
			actions.add(index, aAction);
			wasAdded = true;
		} else {
			wasAdded = addActionAt(aAction, index);
		}
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

	// line 178 "model.ump"
	public void determineExperienceLevel() {
		double ratio;
		if (losses + draws != 0)
			ratio = (double) wins / (losses + draws);
		else
			ratio = 0.0;

		if (ratio <= 1.0) {
			setExpLvl("Beginner");
		} else if (ratio > 1.0 && ratio <= 3.0) {
			setExpLvl("Intermediate");
		} else {
			setExpLvl("Expert");
		}
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
				+ ","
				+ "isReady"
				+ ":"
				+ getIsReady()
				+ "]"
				+ System.getProperties().getProperty("line.separator")
				+ "  "
				+ "token = "
				+ (getToken() != null ? Integer.toHexString(System
						.identityHashCode(getToken())) : "null") + outputString;
	}
}
