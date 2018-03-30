package fr.gfi.tools.utils;

public class GfiToolException extends Exception {
	private static final long serialVersionUID = 4816698586041146905L;

	public GfiToolException(String msg) {
		super(msg);
	}

	public GfiToolException(String msg, Throwable exception) {
		super(msg, exception);
	}


}
