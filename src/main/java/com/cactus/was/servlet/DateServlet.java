package com.cactus.was.servlet;

import com.cactus.was.util.HttpRequest;
import com.cactus.was.util.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateServlet implements SimpleServlet{

    private static Logger logger = LoggerFactory.getLogger(DateServlet.class);

    @Override
    public void service(HttpRequest req, HttpResponse res) throws IOException{
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Writer writer = res.getWriter();
        writer.write(sdf.format(date));
        logger.debug(sdf.format(date));
    }

}
