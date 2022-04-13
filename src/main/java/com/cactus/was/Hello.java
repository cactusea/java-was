package com.cactus.was;

import com.cactus.was.util.HttpRequest;
import com.cactus.was.util.HttpResponse;
import com.cactus.was.util.SimpleServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;

public class Hello implements SimpleServlet {

    private static Logger logger = LoggerFactory.getLogger(com.cactus.was.service.Hello.class);

    @Override
    public void service(HttpRequest req, HttpResponse res) throws IOException {
        Writer writer = res.getWriter();
        writer.write("Hello,");
        String name = req.getParameter("name");
        if(name!=null){
            writer.write(name);
        }
    }

}
