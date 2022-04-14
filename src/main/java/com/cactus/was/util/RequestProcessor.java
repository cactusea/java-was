package com.cactus.was.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

public class RequestProcessor implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(RequestProcessor.class);
    private final static String[] EXTENTION = {"html","txt"};
    private Socket connection;

    public RequestProcessor(Socket connection){
        this.connection = connection;
    }

    /**
     * 1. server setting 정보 가져오기
     * 2. 요청 도메인 확인 (ex. GET /asdf HTTP/1.1
     * 3. 요청 분석하여 403 처리  (forbidden_type)
     *    1) 도메인에 .exe가 포함되어 있으면
     *    2) root보다 상위 디렉터리에 접근할 때 (/../.. ....)
     * 4. 403, 404, 500 응답시 설정에 맞는 파일 이름 페이지 보내기 (getPage403() ..)
     */
    @Override
    public void run(){
        InputStream ins=null;
        OutputStream ous=null;
        try{
            ins = connection.getInputStream();
            ous = connection.getOutputStream();
        } catch (IOException e){
            logger.error("connectoin steream error :: {}", e);
        }

        HttpRequest req = new HttpRequest(ins);
        HttpResponse res = new HttpResponse(ous);

        OutputStream raw = new BufferedOutputStream(ous);
        Writer out = new OutputStreamWriter(raw);

        try {
            req.setting();

            String fileName = req.getFileName();
            String filePath = req.getFilePath();
            logger.debug("fileName::{}, filepath::{}", fileName, filePath);

            ClassLoader loader = Thread.currentThread().getContextClassLoader();

            //..todo ... 빼고싶당
            //보안규칙에 위배되는 경우 403 반환
            for(String ft : req.getForbiddenType()){
                if(fileName.endsWith("."+ft) || fileName.contains(ft)){
                    logger.info("forbidden type, access denied");
                    String page = loader.getResource(req.getServerConfig().getHttp_root()
                            +req.getServerConfig().getPage403()).getPath();
                    File theFile = new File(page);

                    if (theFile.canRead()) {
                        byte[] theData = Files.readAllBytes(theFile.toPath());
                        res.sendHeader(out, req.getHeader().getVersion()+" 403 Forbidden", "text/html", theData.length);
                        raw.write(theData);
                        raw.flush();
                        throw new Exception("access denied Exception");
                    }
                }
            }

            URL url = loader.getResource(filePath);
            if(url!=null){
                File theFile = new File(url.getPath());
                if (theFile.canRead()) {
                    byte[] theData = Files.readAllBytes(theFile.toPath());
                    String contentType = URLConnection.getFileNameMap().getContentTypeFor(url.getPath());
                    res.sendHeader(out, req.getHeader().getVersion()+" 200 OK", contentType, theData.length);
                    raw.write(theData);
                    raw.flush();
                }
            } else {
                //classloader에서 file resource를 읽지 못하는 경우 클래스 요청으로 간주함
                doAction(req, res);
            }


        } catch (FileNotFoundException e){
            logger.error("FileNotFoundException");
            e.printStackTrace();

        } catch (IOException ex) {
            logger.error("Error talking to " + connection.getRemoteSocketAddress(), ex);
        } catch(Exception e){
            logger.error("server Exception");
            e.printStackTrace();
        }finally {
            try {
                connection.close();
            } catch (IOException ex) {
                logger.error("finally IOException");
                ex.printStackTrace();
            }
        }
    }

    protected void doAction(HttpRequest req, HttpResponse res) {
        Class<?> servletClass = null;
        Method method = null;

        try {
            //todo package ...
            servletClass = Class.forName("com.cactus.was."+"Hello");
//            servletClass = Class.forName("com.cactus.was.service."+"Hello");
            method = servletClass.getMethod("service", new Class[] {HttpRequest.class, HttpResponse.class} );
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        Object simpleServlet = null;

        try {
            simpleServlet = servletClass.getDeclaredConstructor().newInstance();
            method.invoke(simpleServlet, req, res);
            res.sendHeader(req.getHeader().getVersion()+" 200 OK", "text/html", 0);
            //...?todo 왜 에러가...
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
