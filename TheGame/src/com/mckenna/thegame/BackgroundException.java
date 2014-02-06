package com.mckenna.thegame;

public class BackgroundException extends Exception {
	private static final long serialVersionUID = -8146219868505465166L;

	//Parameterless Constructor
    public BackgroundException() {}

    //Constructor that accepts a message
    public BackgroundException(String message)
    {
       super(message);
    }
}
