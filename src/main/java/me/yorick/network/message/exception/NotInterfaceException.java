package me.yorick.network.message.exception;

public class NotInterfaceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public NotInterfaceException() {
		super("The input class is not an interface");
	}

}
