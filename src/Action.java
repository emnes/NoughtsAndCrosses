import java.io.Serializable;
import java.sql.Time;

/**
 * Action interface which represents a general action made by a player.
 * @author Alexander Dale (6839825)
 * @author Mazhar Shar (7987495)
 * @version November 2014
 */

@SuppressWarnings("serial")
public abstract class Action implements Serializable {

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
