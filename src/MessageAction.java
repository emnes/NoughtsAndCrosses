import java.sql.Time;

/**
 * MessageAction is a command sent to the server which may or may not affect the state of the game.
 * @author Alexander Dale (6839825)
 * @author Mazhar Shar (7987495)
 * @version November 2014
 */

@SuppressWarnings("serial")
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
	
	/**
	 * Compares message with object passed through. Enables comparison of messages.
	 * @param
	 * 		Object to compare with.
	 * @return
	 * 		Whether the object is equal to message.
	 */
	public boolean equals(Object o) {
		if (o instanceof String) {
			String s = (String) o;
			return this.message.equals(s);
		} else if (o instanceof MessageAction) {
			MessageAction ma = (MessageAction) o;
			return equals(ma.getMessage());
		}
		return false;
	}

	public String toString() {
		String outputString = "";
		return super.toString() + "[" + "message" + ":" + getMessage() + "]"
				+ outputString;
	}
}