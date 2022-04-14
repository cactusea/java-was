package com.cactus.was.servlet;

import com.cactus.was.util.HttpRequest;
import com.cactus.was.util.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;

public class Hello implements SimpleServlet {

    private static Logger logger = LoggerFactory.getLogger(Hello.class);

    @Override
    public void service(HttpRequest req, HttpResponse res) throws IOException {
        logger.debug("Hello");

        Writer writer = res.getWriter();
        writer.write("Hello,");
        String name = req.getParameter("name");
        if(name!=null){
            logger.debug(name);
            writer.write(name);
        }
    }

}
