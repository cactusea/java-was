package com.cactus.was.servlet.service;

import com.cactus.was.util.HttpRequest;
import com.cactus.was.util.HttpResponse;
import com.cactus.was.servlet.SimpleServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class Hello implements SimpleServlet {

    private static Logger logger = LoggerFactory.getLogger(Hello.class);

    @Override
    public void service(HttpRequest req, HttpResponse res) throws IOException {
        logger.debug("service.Hello");

        Writer writer = res.getWriter();
        writer.write("Hello, ");
        String name = req.getParameter("name");
        if(name!=null){
            logger.debug(name);
            writer.write(name);
        }
    }

}
