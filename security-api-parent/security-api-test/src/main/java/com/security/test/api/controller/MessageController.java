package com.security.test.api.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@CrossOrigin
@RequestMapping("/message")
public class MessageController {

    private Logger logger = Logger.getLogger(getClass().getName());

    @RequestMapping(value = "/greeting", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getGreeting() {

        logger.info("getGreeting() invoked");

        return "Hello World!!!";
    }
}
