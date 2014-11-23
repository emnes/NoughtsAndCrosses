import java.sql.Time;

// line 366 "model.ump"
// line 402 "model.ump"
public class MoveAction extends Action {

	// ------------------------
	// MEMBER VARIABLES
	// ------------------------

	// MoveAction Attributes
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
