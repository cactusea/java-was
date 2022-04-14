package com.cactus.was.servlet;

import com.cactus.was.util.HttpRequest;
import com.cactus.was.util.HttpResponse;

import java.io.IOException;

public interface SimpleServlet {
    void service(HttpRequest req, HttpResponse res) throws IOException;
}
