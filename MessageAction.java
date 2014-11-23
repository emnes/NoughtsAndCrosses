import java.sql.Time;


public class MessageAction extends Action {

	// ------------------------
	// MEMBER VARIABLES
	// ------------------------

	// MessageAction Attributes
	private String message;

	// ------------------------
	// CONSTRUCTOR
	// ------------------------

	public MessageAction(Time aTime, String aMessage) {
		super(aTime);
		message = aMessage;
	}

	// ------------------------
	// INTERFACE
	// ------------------------

	public boolean setMessage(String aMessage) {
		boolean wasSet = false;
		message = aMessage;
		wasSet = true;
		return wasSet;
	}

	public String getMessage() {
		return message;
	}

	public void delete() {
		super.delete();
	}

	public String toString() {
		String outputString = "";
		return super.toString() + "[" + "message" + ":" + getMessage() + "]"
				+ outputString;
	}
}