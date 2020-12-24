package com.bot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bot.common.BotException;
import com.bot.response.Response;
import com.bot.service.BotService;
import com.bot.vo.Request;
/**
 * 
 * This class acts as a controller layer in which service 
 * requests are handled POST and GET of /bot services.
 *
 */
@RestController
@RequestMapping("/bot")
public class BotController {
	private static final Logger LOGGER = LoggerFactory.getLogger(BotController.class);

	@Autowired
	BotService botService;

	@CrossOrigin(origins = "*")
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> changePosition(@RequestBody Request request) {
		Response response = null;
		LOGGER.debug("Started {}", request);
		try {
			if (request == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input");
			}
			response = botService.changePosition(request);
		} catch (BotException e) {
			LOGGER.error("Exception", e.fillInStackTrace());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
		LOGGER.debug("Completed {}", response);
		return ResponseEntity.ok().body(response);
	}

	@CrossOrigin(origins = "*")
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getPosition() {
		LOGGER.debug("Started");
		Response response = null;
		try {
			response = botService.getCurrentPosition();
		} catch (Exception e) {
			LOGGER.error("Exception", e.fillInStackTrace());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
		}
		LOGGER.debug("Completed {}", response);
		return ResponseEntity.ok().body(response);
	}

}
