package com.bot.service;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.bot.common.BotException;
import com.bot.response.Position;
import com.bot.response.Response;
import com.bot.vo.Move;
import com.bot.vo.Request;

/**
 * 
 * This class acts as a business layer which actually has 
 * the business logic on operation of position of the bot. 
 *
 */
@Service
public class BotServiceImpl implements BotService {
	private static final Logger LOGGER = LoggerFactory.getLogger(BotServiceImpl.class);

	private static final String XML_NAME = "bot.xml";
	private static final int TOTAL_DIRECTIONS = 4;
	private static final String STARTED_LOG = "Started";
	private static final String COMPLETED_LOG = "Completed";
	private static final String CURRENTPOSITION = "currentPosition";
	private static final String POSITION = "position";
	private static final String DIRECTION = "direction";
	static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	static TransformerFactory transformerFactory = TransformerFactory.newInstance();

	/*
	 * (non-Javadoc)
	 * @see com.bot.service.BotService#getCurrentPosition()
	 * Get Current position of bot
	 */
	@Override
	public Response getCurrentPosition() throws BotException {
		LOGGER.debug(STARTED_LOG);
		Response response = new Response();
		response.setPosition(getCurrentPositionFromXML());
		LOGGER.debug(COMPLETED_LOG);
		return response;
	}

	/*
	 * (non-Javadoc)
	 * @see com.bot.service.BotService#changePosition(com.bot.vo.Request)
	 * This method is used to process the position of  bot with requested input
	 */
	@Override
	public Response changePosition(Request request) throws BotException {
		LOGGER.debug(STARTED_LOG);
		Response response = new Response();
		try {
			List<Move> moves = request.getMove();
			com.bot.vo.Position pos = request.getPosition();
			Map<String, Integer> directionsR = getDirectionsR();
			List<String> rotations = BotService.getRotationsAllowed();
			if (!validateInput(request, rotations, directionsR)) {
				LOGGER.debug("Invali Input");
				throw new BotException("Invalid Input");
			}
			
			Position position = new Position();
			position.setDirection(pos.getDirection());
			position.setX(pos.getX());
			position.setY(pos.getY());
			if (moves == null || moves.isEmpty()) {
				response.setPosition(position);
				return response;
			}
			Map<Integer, String> directions = getDirections();
			
			int current = directionsR.get(position.getDirection());
			int x = Integer.parseInt(position.getX());
			int y = Integer.parseInt(position.getY());

			Collections.sort(moves, (o1, o2) -> o1.getO().compareTo(o2.getO()));
			for (Move move : moves) {
				current = getCurrentDirection(move, rotations, current);
				String f = move.getF();
				String b = move.getB();
				x = getXAxisVal(current, f, b, x);
				y = getYAxisVal(current, f, b, y);
			}
			position.setDirection(directions.get(current));
			position.setX(String.valueOf(x));
			position.setY(String.valueOf(y));
			response.setPosition(position);
			updateResponse(position, request);
		} catch (BotException e) {
			LOGGER.error("Exception", e.fillInStackTrace());
			throw new BotException(e.getMessage());
		}catch (Exception e) {
			LOGGER.error("Exception", e.fillInStackTrace());
			throw new BotException("Internal Server Error");
		}
		LOGGER.debug(COMPLETED_LOG);
		return response;
	}

	/*
	 * This is a private method which fetches current position from bot.xml
	 */
	private Position getCurrentPositionFromXML() throws BotException {
		LOGGER.debug(STARTED_LOG);
		Position position = new Position();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			URL url = getClass().getClassLoader().getResource(XML_NAME);
			File file1 = new File(url.toURI());
			Document document = builder.parse(file1);
			document.getDocumentElement().normalize();
			NodeList nList = document.getElementsByTagName(CURRENTPOSITION);
			Node node = nList.item(0);
			NodeList nList1 = document.getElementsByTagName(POSITION);
			Node node1 = nList1.item(0);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) node1;
				position.setDirection(eElement.getElementsByTagName(DIRECTION).item(0).getTextContent());
				position.setX(eElement.getElementsByTagName("x").item(0).getTextContent());
				position.setY(eElement.getElementsByTagName("y").item(0).getTextContent());
			}
		} catch (Exception e) {
			LOGGER.error("Exception", e.fillInStackTrace());
			throw new BotException("Internal Server Error");
		}
		LOGGER.debug(COMPLETED_LOG);
		return position;
	}

	/*
	 *  This is a private method which calculates the current position post applying each move
	 */
	private int getCurrentDirection(Move move, List<String> rotations, int current) {
		String l = move.getL();
		String r = move.getR();
		int d = 0;
		if (l != null && rotations.contains(l)) {
			d = Integer.parseInt(l);
			d = d / 90;
			current = getReverseDirection(current, d);
		} else if (r != null && rotations.contains(r)) {
			d = Integer.parseInt(r);
			d = d / 90;
			current = getForwardDirection(current, d);
		}
		return current;
	}

	/*
	 *  This is a private method which get reverse direction of bot
	 */
	private int getReverseDirection(int current, int changed) {
		if (changed == 0 || changed == 4) {
			return current;
		}
		int a = current - changed;
		a = TOTAL_DIRECTIONS - (Math.abs(a));
		return a;
	}

	/*
	 *  This is a private method which gets forward direction of bot
	 */
	private int getForwardDirection(int current, int changed) {
		if (changed == 0 || changed == 4) {
			return current;
		}

		int a = current + changed;
		if (a > TOTAL_DIRECTIONS) {
			return a - TOTAL_DIRECTIONS;
		} else {
			return a;
		}
	}
	
	/*
	 *  This is a private method which gets X-axis value
	 */
	private int getXAxisVal(int current, String f, String b, int x) throws BotException {
		int d = 0;
		if(current==4 || current==2) {
			if (f != null) {
				d = Integer.parseInt(f);
				if(d < 0) {
					throw new BotException("Invalid Input. Move should not be a negative value");
				}
				x = x - d;
			} else if (b != null) {
				d = Integer.parseInt(b);
				if(d < 0) {
					throw new BotException("Invalid Input. Move should not be a negative value");
				}
				x = x +(- d);
			}
		}
		return x;
	}
	
	/*
	 *  This is a private method which gets Y-axis value
	 */
	private int getYAxisVal(int current, String f, String b, int y) throws BotException {
		int d = 0;
		if(current==1 || current==3) {
			if (f != null) {
				d = Integer.parseInt(f);
				if(d < 0) {
					throw new BotException("Invalid Input. Move should not be a negative value");
				}
				y = y + d;
			} else if (b != null) {
				d = Integer.parseInt(b);
				if(d < 0) {
					throw new BotException("Invalid Input. Move should not be a negative value");
				}
				y = y +(- d);
			}
		}
		return y;
	}

	/*
	 * This is a private method which updates the response in bot.xml
	 */
	private void updateResponse(Position position, Request request)
			throws ParserConfigurationException, URISyntaxException, SAXException, IOException, TransformerException {
		LOGGER.debug(STARTED_LOG);
		DocumentBuilder builder = factory.newDocumentBuilder();
		URL url = getClass().getClassLoader().getResource(XML_NAME);
		File file = new File(url.toURI());
		if (file.exists()) {
			Document document = builder.parse(file);
			NodeList rnList = document.getElementsByTagName("requests");
			Node rnode = rnList.item(0);
			appendChild(rnode, document, request);

			NodeList nList = document.getElementsByTagName(CURRENTPOSITION);
			Node node = nList.item(0);
			NodeList nList1 = document.getElementsByTagName(POSITION);
			Node node1 = nList1.item(0);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) node1;
				Node dir1 = eElement.getElementsByTagName(DIRECTION).item(0);
				dir1.setTextContent(position.getDirection());
				Node dir2 = eElement.getElementsByTagName("x").item(0);
				dir2.setTextContent(position.getX());
				Node dir3 = eElement.getElementsByTagName("y").item(0);
				dir3.setTextContent(position.getY());
			}

			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(file);
			transformer.transform(source, result);
		}

		LOGGER.debug(COMPLETED_LOG);
	}

	/*
	 * This is a private method which appends the new request in bot.xml
	 */
	private void appendChild(Node rnode, Document document, Request request) {
		LOGGER.debug(STARTED_LOG);
		com.bot.vo.Position position = request.getPosition();
		List<Move> movesList = request.getMove();

		Element pos = document.createElement("Position");
		pos.appendChild(createElement("Direction", position.getDirection(), document));
		pos.appendChild(createElement("X", position.getX(), document));
		pos.appendChild(createElement("Y", position.getY(), document));

		Element moves = document.createElement("Moves");
		for (Move move : movesList) {
			Element mv = document.createElement("Move");

			Element o = document.createElement("O");
			o.setTextContent(move.getO());
			mv.appendChild(o);
			if (move.getL() != null) {
				mv.appendChild(createElement("L", move.getL(), document));
			} else if (move.getR() != null) {
				mv.appendChild(createElement("R", move.getR(), document));
			}

			if (move.getF() != null) {
				mv.appendChild(createElement("F", move.getF(), document));
			} else if (move.getB() != null) {
				mv.appendChild(createElement("B", move.getB(), document));
			}
			moves.appendChild(mv);
		}
		Element req = document.createElement("request");
		req.appendChild(pos);
		req.appendChild(moves);
		rnode.appendChild(req);
		LOGGER.debug(COMPLETED_LOG);
	}

	/*
	 * This is a private method which creates new element
	 */
	private Element createElement(String e, String v, Document document) {
		Element ele = document.createElement(e);
		ele.setTextContent(v);
		return ele;
	}

	/*
	 * This is a private method which validates the input request
	 */
	private boolean validateInput(Request request, List<String> rotations, Map<String, Integer> directionsR) {
		com.bot.vo.Position pos = request.getPosition();
		String dir = pos.getDirection();
		if (dir == null || dir.isEmpty() || !directionsR.containsKey(dir)) {
			return false;
		}

		List<Move> moves = request.getMove();
		for (Move move : moves) {
			String l = move.getL();
			if (l != null && !l.isEmpty() && !rotations.contains(l)) {
				return false;
			}
			String r = move.getL();
			if (r != null && !r.isEmpty() && !rotations.contains(r)) {
				return false;
			}
		}

		return true;

	}

}
