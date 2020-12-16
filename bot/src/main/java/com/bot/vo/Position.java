package com.bot.vo;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement
public class Position implements Serializable{
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

	@XmlElement
	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getX() {
		return x;
	}

	@XmlElement
	public void setX(String x) {
		this.x = x;
	}

	public String getY() {
		return y;
	}

	@XmlElement
	public void setY(String y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return "Position [direction=" + direction + ", x=" + x + ", y=" + y + "]";
	}
	
	
}
