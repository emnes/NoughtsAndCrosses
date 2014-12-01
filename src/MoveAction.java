import java.sql.Time;

/**
 * MoveAction is an action that represents a player's move. 
 * A move is where, on the grid, the player is placing their nought or cross.
 * @author Alexander Dale (6839825)
 * @author Mazhar Shar (7987495)
 * @version November 2014
 */

// line 366 "model.ump"
// line 402 "model.ump"
@SuppressWarnings("serial")
public class MoveAction extends Action {

	// ------------------------
	// MEMBER VARIABLES
	// ------------------------

	// MoveAction Attributes
	
	// Coordinates of a move. Used to identify where player has made a move on grid.
	private int x;
	private int y;

	// ------------------------
	// CONSTRUCTOR
	// ------------------------

	public MoveAction(Time aTime, int aX, int aY) {
		super(aTime);
		x = aX;
		y = aY;
	}

	// ------------------------
	// INTERFACE
	// ------------------------

	public boolean setX(int aX) {
		boolean wasSet = false;
		x = aX;
		wasSet = true;
		return wasSet;
	}

	public boolean setY(int aY) {
		boolean wasSet = false;
		y = aY;
		wasSet = true;
		return wasSet;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void delete() {
		super.delete();
	}

	public String toString() {
		String outputString = "";
		return super.toString() + "[" + "x" + ":" + getX() + "," + "y" + ":"
				+ getY() + "]" + outputString;
	}
}
