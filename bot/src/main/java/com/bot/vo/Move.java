package com.bot.vo;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Move implements Serializable{
	
	private static final long serialVersionUID = 1374053171332596182L;

	@JsonProperty("O")
	private String o;
	
	@JsonProperty("L")
	private String l;
	
	@JsonProperty("F")
	private String f;
	
	@JsonProperty("R")
	private String r;
	
	@JsonProperty("B")
	private String b;

	public String getO() {
		return o;
	}

	@XmlElement
	public void setO(String o) {
		this.o = o;
	}

	public String getL() {
		return l;
	}

	@XmlElement
	public void setL(String l) {
		this.l = l;
	}

	public String getF() {
		return f;
	}

	@XmlElement
	public void setF(String f) {
		this.f = f;
	}

	public String getR() {
		return r;
	}

	@XmlElement
	public void setR(String r) {
		this.r = r;
	}

	public String getB() {
		return b;
	}

	@XmlElement
	public void setB(String b) {
		this.b = b;
	}

	@Override
	public String toString() {
		return "Move [o=" + o + ", l=" + l + ", f=" + f + ", r=" + r + ", b=" + b + "]";
	}

}
