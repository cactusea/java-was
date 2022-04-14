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
    private ClassLoader loader = Thread.currentThread().getContextClassLoader();
    private Socket connection;
    private OutputStream raw;
    private Writer out;

    InputStream ins=null;
    OutputStream ous=null;

    HttpRequest req;
    HttpResponse res;

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

        try{
            ins = connection.getInputStream();
            ous = connection.getOutputStream();
        } catch (IOException e){
            logger.error("connectoin steream error :: {}", e);
        }

        req = new HttpRequest(ins);
        res = new HttpResponse(ous);
        raw = new BufferedOutputStream(ous);
        out = new OutputStreamWriter(raw);

        try {
            req.setting();

            String fileName = req.getFileName();
            String filePath = req.getFilePath();
            logger.debug("fileName::{}, filepath::{}", fileName, filePath);

            //보안규칙에 위배되는 경우 403 반환
            for(String ft : req.getForbiddenType()){
                if(fileName.endsWith("."+ft) || fileName.contains(ft)){
                    logger.warn("forbidden type, access denied");
                    callExceptionPage(req, res, "403");
                    throw new Exception("access denied Exception");
                }
            }

            URL url = loader.getResource(filePath);
            if(url!=null && !filePath.equals("")){
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
            callExceptionPage(req, res, "404");
        } catch (IOException ex) {
            logger.error("Error talking to " + connection.getRemoteSocketAddress(), ex);
            callExceptionPage(req, res, "500");
        } catch(Exception e){
            logger.error("server Exception");
            e.printStackTrace();
            callExceptionPage(req, res, "500");
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
            servletClass = Class.forName("com.cactus.was.servlet."+req.getFilePath());
            method = servletClass.getMethod("service", new Class[] {HttpRequest.class, HttpResponse.class} );
        } catch (ClassNotFoundException e) {
            logger.error("ClassNotFoundException");
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            logger.error("NoSuchMethodException");
            e.printStackTrace();
        } catch (SecurityException e) {
            logger.error("SecurityException");
            e.printStackTrace();
        }

        Object simpleServlet = null;
        try {
            simpleServlet = servletClass.getDeclaredConstructor().newInstance();
            res.sendHeader(out, req.getHeader().getVersion()+" 200 OK", "text/html", 0);
            method.invoke(simpleServlet, req, res);

        } catch (Exception e) {
            e.printStackTrace();
            callExceptionPage(req, res, "500");
        }

    }

    /**
     * 각 host의 http status 응답 페이지 리턴
     *
     * @param req
     * @param res
     * @param statusCode
     */
    public void callExceptionPage(HttpRequest req, HttpResponse res, String statusCode){
        String statusMsg = ""; //todo enum으로 관리해도 좋을 듯
        String page = req.getServerConfig().getHttp_root();
        switch (statusCode){
            case "403": {
                page += req.getServerConfig().getPage403();
                statusMsg = " 403 Forbidden";
                break;
            }
            case "404": {
                page += req.getServerConfig().getPage404();
                statusMsg = " 404 Not Found";
                break;
            }
            case "500": {
                page += req.getServerConfig().getPage500();
                statusMsg = " 500 Internal Server Error";
                break;
            } default: {
                page += req.getServerConfig().getPage_index();
            }
        }

        page = loader.getResource(page).getPath();
        File tFile = new File(page);
        try {
            if (tFile.canRead()) {
                byte[] theData = Files.readAllBytes(new File(page).toPath());
                res.sendHeader(out, req.getHeader().getVersion()+statusMsg, "text/html", theData.length);
                raw.write(theData);
                raw.flush();
            }
        }catch (IOException ioe){
            logger.error("Server Error response :: IOException");
            ioe.printStackTrace();
        }
    }


}
