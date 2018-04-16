package frame.ydclient.socket;

@SuppressWarnings("serial")
public class OutLineException extends Exception {
	private String eMsg = "OutLineException";

	public OutLineException(String eMsg) {
		super();
		if (null != eMsg) {
			this.eMsg += ":" + eMsg;
		}
	}

	public String toString() {
		return eMsg.toString();
	}
}
