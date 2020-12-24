package com.bot.common;

/**
 * 
 * This class basically acts as a Custom exception to track the 
 * application specific exceptions
 *
 */
public class BotException extends Exception {

	private static final long serialVersionUID = -3740748188973265567L;

	private final String message;

	public BotException(String message) {
		this.message = message;
	}

	public BotException(Exception excp) {
		this.message = excp.getMessage();
	}

	@Override
	public String getMessage() {
		return message;
	}

}
