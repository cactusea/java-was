package com.cactus.was;

import com.cactus.was.config.Configuration;
import com.cactus.was.config.ServerSetting;
import com.cactus.was.util.Loader;
import com.cactus.was.util.RequestProcessor;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {

    private static Logger logger = LoggerFactory.getLogger(HttpServer.class);

    private static final int NUM_THREADS = 50;
    private final int port;

    public HttpServer(int port){
        this.port = port;
    }

    /**
     * http server start
     * @throws IOException
     */
    public void start() throws IOException {
        logger.info("server start");
        logger.info("run~!");

        ExecutorService pool = Executors.newFixedThreadPool(NUM_THREADS);
        try (ServerSocket server = new ServerSocket(port)) {
            logger.info("Accepting connections on port " + server.getLocalPort());
            while (true) {
                try {
                    Socket request = server.accept();
                    Runnable r = new RequestProcessor(request);
                    pool.submit(r);
                } catch (IOException ex) {
                    ex.getStackTrace();
                    logger.warn("Error accepting connection", ex);
                }
            }
        }
    }

    public static void main(String[] args) {

        logger.info("hello was main");

        //todo 흠 생성자 가져오는 것도 그렇고 뭔가 복잡해서 별로인데.. 리팩터링
        ServerSetting s = ServerSetting.getInstance();
        int port = s.getPort();

        //port정보로 server start
        try {
            if (port < 0 || port > 65535) port = 80;
        } catch (RuntimeException e) {
            logger.warn(e.getMessage(), e);
            port = 80;
        }
        try{
            HttpServer webserver = new HttpServer(port);
            webserver.start();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

}
