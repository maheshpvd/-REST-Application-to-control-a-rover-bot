package com.bot.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bot.common.BotException;
import com.bot.response.Response;
import com.bot.vo.Request;

public interface BotService {

	static List<String> getRotationsAllowed() {
		List<String> list = new ArrayList<>();
		list.add("0");
		list.add("90");
		list.add("180");
		list.add("270");
		list.add("360");
		return list;
	}

	default Map<Integer, String> getDirections() {
		Map<Integer, String> directions = new HashMap<>();
		directions.put(1, "N");
		directions.put(2, "E");
		directions.put(3, "S");
		directions.put(4, "W");
		return directions;
	}

	default Map<String, Integer> getDirectionsR() {
		Map<String, Integer> directions = new HashMap<>();
		directions.put("N", 1);
		directions.put("E", 2);
		directions.put("S", 3);
		directions.put("W", 4);
		return directions;
	}

	public Response getCurrentPosition() throws BotException;

	public Response changePosition(Request request) throws BotException;

}
