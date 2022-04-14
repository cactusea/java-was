package com.cactus.was;

import com.cactus.was.config.Configuration;
//import com.cactus.was.config.ServerSetting;
import com.cactus.was.util.ConfigLoader;
import com.cactus.was.util.RequestProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.krb5.Config;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
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
        logger.debug("================================");
        logger.debug("  web application server start  ");
        logger.debug("================================");

        ExecutorService pool = Executors.newFixedThreadPool(NUM_THREADS);
        try (ServerSocket server = new ServerSocket(port)) {
            logger.info("Accepting connections on port :: {}", server.getLocalPort());
            while (true) {
                try {
                    server.setSoTimeout(5000);
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

        Configuration config = ConfigLoader.load();
        int port = config.getPort();

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
