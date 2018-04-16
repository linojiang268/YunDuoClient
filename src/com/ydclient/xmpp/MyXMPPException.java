package com.ydclient.xmpp;

@SuppressWarnings("serial")
public class MyXMPPException extends Exception {

	public MyXMPPException() {
		super();
	}

	public MyXMPPException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public MyXMPPException(String detailMessage) {
		super(detailMessage);
	}

	public MyXMPPException(Throwable throwable) {
		super(throwable);
	}

}
