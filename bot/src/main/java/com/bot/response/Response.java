package com.bot.response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Response implements Serializable{

	private static final long serialVersionUID = -3818803695806261061L;
	
	@JsonProperty("Position")
	private Position position;

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}
	
}
