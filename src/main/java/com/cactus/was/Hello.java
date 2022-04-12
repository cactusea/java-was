package com.cactus.was;

import com.cactus.was.util.HttpRequest;
import com.cactus.was.util.HttpResponse;
import com.cactus.was.util.SimpleServlet;

import java.io.IOException;

public class Hello implements SimpleServlet {

    public void service(HttpRequest req, HttpResponse res) throws IOException {
        java.io.Writer writer = res.getWriter();
        writer.write("Hello, ");
        writer.write(req.getParameter("name"));
    }

}
