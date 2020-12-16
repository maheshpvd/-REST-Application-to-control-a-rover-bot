package com.bot.vo;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement
public class Request implements Serializable{

	private static final long serialVersionUID = -3818803695806261061L;
	
	
	@JsonProperty("Position")
	private Position position;

	@JsonProperty("Move")
	private List<Move> move;
	
	public Position getPosition() {
		return position;
	}

	@XmlElement
	public void setPosition(Position position) {
		this.position = position;
	}
	public List<Move> getMove() {
		return move;
	}

	@XmlElement
	public void setMove(List<Move> move) {
		this.move = move;
	}

	@Override
	public String toString() {
		return "Request [position=" + position + ", move=" + move + "]";
	}

	
}
