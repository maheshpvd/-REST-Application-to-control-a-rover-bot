package com.bot.response;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * This class hold the data of position of a bot
 *
 */
@XmlRootElement
public class Position implements Serializable {
	private static final long serialVersionUID = -4036348441662891611L;

	@JsonProperty("Direction")
	private String direction;

	@JsonProperty("X")
	private String x;

	@JsonProperty("Y")
	private String y;

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getX() {
		return x;
	}

	public void setX(String x) {
		this.x = x;
	}

	public String getY() {
		return y;
	}

	public void setY(String y) {
		this.y = y;
	}

}
