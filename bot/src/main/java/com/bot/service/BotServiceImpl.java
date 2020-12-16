package com.bot.service;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
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

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.bot.response.Position;
import com.bot.response.Response;
import com.bot.vo.Move;
import com.bot.vo.Request;

@Service
public class BotServiceImpl implements BotService {

	private static final String XML_NAME = "bot.xml";
	private static final int TOTAL_DIRECTIONS = 4;

	@Override
	public Response getCurrentPosition() throws Exception {
		Response response = new Response();
		response.setPosition(getCurrentPositionFromXML());
		return response;
	}

	private Position getCurrentPositionFromXML() {
		Position position = new Position();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			URL url = getClass().getClassLoader().getResource(XML_NAME);
			File file1 = new File(url.toURI());
			Document document = builder.parse(file1);
			document.getDocumentElement().normalize();
			NodeList nList = document.getElementsByTagName("currentPosition");
			Node node = nList.item(0);
			NodeList nList1 = document.getElementsByTagName("position");
			Node node1 = nList1.item(0);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) node1;
				position.setDirection(eElement.getElementsByTagName("direction").item(0).getTextContent());
				position.setX(eElement.getElementsByTagName("x").item(0).getTextContent());
				position.setY(eElement.getElementsByTagName("y").item(0).getTextContent());
			}
		} catch (SAXException | IOException | URISyntaxException | ParserConfigurationException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return position;
	}

	@Override
	public Response changePosition(Request request) throws Exception {
		Response response = new Response();
		List<Move> moves = request.getMove();
		Position position = getCurrentPositionFromXML();
		if (moves == null || moves.isEmpty()) {
			response.setPosition(position);
			return response;
		}

		Collections.sort(moves, new Comparator<Move>() {
			public int compare(Move o1, Move o2) {
				return o1.getO().compareTo(o2.getO());
			}
		});

		Map<Integer, String> directions = getDirections();
		Map<String, Integer> directions1 = getDirections1();
		List<String> rotations = BotService.getRotationsAllowed();
		int d = 0;
		int current = directions1.get(position.getDirection());
		int x = Integer.parseInt(position.getX());
		int y = Integer.parseInt(position.getY());
		for (Move move : moves) {
			current = getCurrentDirection(move, rotations, current);
			String f = move.getF();
			String b = move.getB();
			if (f != null) {
				d = Integer.parseInt(f);
				x = x + d;
			} else if (b != null) {
				d = Integer.parseInt(b);
				y = y - d;
			}
		}
		position.setDirection(directions.get(current));
		position.setX(String.valueOf(x));
		position.setY(String.valueOf(y));
		response.setPosition(position);
		updateResponse(position, request);
		return response;
	}

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

	private int getReverseDirection(int current, int changed) {
		if (changed == 0 || changed == 4) {
			return current;
		}
		int a = current - changed;
		a = TOTAL_DIRECTIONS - (Math.abs(a));
		return a;
	}

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

	private void updateResponse(Position position, Request request) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			URL url = getClass().getClassLoader().getResource(XML_NAME);
			File file1 = new File(url.toURI());
			Document document = builder.parse(file1);
			NodeList rnList = document.getElementsByTagName("requests");
			Node rnode = rnList.item(0);
			appendChild(rnode, document, request);

			NodeList nList = document.getElementsByTagName("currentPosition");
			Node node = nList.item(0);
			NodeList nList1 = document.getElementsByTagName("position");
			Node node1 = nList1.item(0);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) node1;
				Node dir1 = eElement.getElementsByTagName("direction").item(0);
				dir1.setTextContent(position.getDirection());
				Node dir2 = eElement.getElementsByTagName("x").item(0);
				dir2.setTextContent(position.getX());
				Node dir3 = eElement.getElementsByTagName("y").item(0);
				dir3.setTextContent(position.getY());
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(file1);
			transformer.transform(source, result);
		} catch (SAXException | IOException | URISyntaxException | ParserConfigurationException
				| TransformerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void appendChild(Node rnode, Document document, Request request) {
		com.bot.vo.Position position = request.getPosition();
		List<Move> movesList = request.getMove();
		Element dir = document.createElement("Direction");
		dir.setTextContent(position.getDirection());
		Element x = document.createElement("X");
		x.setTextContent(position.getX());
		Element y = document.createElement("Y");
		y.setTextContent(position.getY());

		Element pos = document.createElement("Position");
		pos.appendChild(dir);
		pos.appendChild(x);
		pos.appendChild(y);

		Element moves = document.createElement("Moves");
		for (Move move : movesList) {
			Element mv = document.createElement("Move");

			Element o = document.createElement("O");
			o.setTextContent(move.getO());
			mv.appendChild(o);
			if (move.getL() != null) {
				Element le = document.createElement("L");
				le.setTextContent(move.getL());
				mv.appendChild(le);
			} else if (move.getR() != null) {
				Element re = document.createElement("R");
				re.setTextContent(move.getR());
				mv.appendChild(re);
			}

			if (move.getF() != null) {
				Element fe = document.createElement("F");
				fe.setTextContent(move.getF());
				mv.appendChild(fe);
			} else if (move.getB() != null) {
				Element be = document.createElement("B");
				be.setTextContent(move.getB());
				mv.appendChild(be);
			}
			moves.appendChild(mv);
		}
		Element req = document.createElement("request");
		req.appendChild(pos);
		req.appendChild(moves);
		rnode.appendChild(req);
	}

}
