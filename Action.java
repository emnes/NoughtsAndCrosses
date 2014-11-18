import java.sql.Time;

// line 200 "model.ump"
// line 248 "model.ump"
public abstract class Action {

	// ------------------------
	// MEMBER VARIABLES
	// ------------------------

	// Action Attributes
	private Time time;

	// ------------------------
	// CONSTRUCTOR
	// ------------------------

	public Action(Time aTime) {
		time = aTime;
	}

	// ------------------------
	// INTERFACE
	// ------------------------

	public boolean setTime(Time aTime) {
		boolean wasSet = false;
		time = aTime;
		wasSet = true;
		return wasSet;
	}

	public Time getTime() {
		return time;
	}

	public void delete() {
	}

	public String toString() {
		String outputString = "";
		return super.toString()
				+ "["
				+ "]"
				+ System.getProperties().getProperty("line.separator")
				+ "  "
				+ "time"
				+ "="
				+ (getTime() != null ? !getTime().equals(this) ? getTime()
						.toString().replaceAll("  ", "    ") : "this" : "null")
				+ outputString;
	}
}
