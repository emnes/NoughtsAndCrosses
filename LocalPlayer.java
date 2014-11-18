import java.util.*;

// line 192 "model.ump"
// line 238 "model.ump"
public class LocalPlayer extends Player {

	// ------------------------
	// MEMBER VARIABLES
	// ------------------------

	// ------------------------
	// CONSTRUCTOR
	// ------------------------

	public LocalPlayer(String aName, int aPlayerId, int aWins, int aLosses,
			int aDraws) {
		super(aName, aPlayerId, aWins, aLosses, aDraws);
	}

	// ------------------------
	// INTERFACE
	// ------------------------

	public void delete() {
		super.delete();
	}

}
