package com.cactus.was.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Date;

public class RequestProcessor implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(RequestProcessor.class);
    private Socket connection;
    private OutputStream ous;
    private InputStream ins;
    private Writer out;

    private HttpRequest req;
    private HttpResponse res;


    public RequestProcessor(Socket connection){
        this.connection = connection;
    }

    /**
     * 1. server setting 정보 가져오기
     * 2. 요청 도메인 확인 (ex. GET /asdf HTTP/1.1
     * 3. 요청 분석하여 403 처리  (forbidden_type)
     *    1) 도메인에 .exe가 포함되어 있으면 (예시로 걍 sh도 넣어봄)
     *    2) root보다 상위 디렉터리에 접근할 때 (/../.. ....)
     * 4. 403, 404, 500 응답시 설정에 맞는 파일 이름 페이지 보내기 (getPage403() ..)
     */
    @Override
    public void run(){
        try {
            ins = connection.getInputStream();
            ous = connection.getOutputStream();
            OutputStream raw = new BufferedOutputStream(ous);
            Writer out = new OutputStreamWriter(raw);

            req = new HttpRequest(ins);
            res = new HttpResponse(ous);

            req.setting();
            //String fileName = req.getFileName();
            String filePath = req.getFilePath();
            logger.debug("filepath::{}",req.getFilePath());

            if(filePath==null){
                //todo 404 exception 처리
                throw new FileNotFoundException();
            }else if("403".equals(filePath)){ //todo 임시처리~~
                //todo 403 forbidden
                //throw new ErrorHandler("403 Forbidden");
            }

            //경로처리
            //루트 아니고 경로 파일일 경우에도 처리..
            File file = new File(filePath);

            String contentType = URLConnection.getFileNameMap().getContentTypeFor(filePath);

            String path = filePath;
            File theFile = new File(path);

            if(path.endsWith("html")){
//                page로 이동
                //file canread로 확인 후 리턴

            } else {
                doAction();
            }

            if (theFile.canRead()) {
                byte[] theData = Files.readAllBytes(theFile.toPath());
                // send the file; it may be an image or other binary data
                // so use the underlying output stream
                // instead of the writer
                //todo 흠 헤더... 헤더를 어떻게 보낼까
                //이거 flush에ㅔ서 소켓익셉션이..
//                sendHeader(out, "HTTP/1.0 200 OK", contentType, theData.length);

                ous.write(theData);
                ous.flush();
            } else {
//                logger.info(filePath);

//                url = loader.getResource("template/was1/404.html");
//                logger.error("file cannot read");
            }

        } catch (FileNotFoundException e){
            logger.error("FileNotFoundException :: ", e);
            //todo 404 리턴
        } catch (IOException ex) {
            logger.error("Error talking to " + connection.getRemoteSocketAddress(), ex);
        } catch(Exception e){
            logger.error("server run --> fail :: {}", e);
        }finally {
            try {
                connection.close();
            } catch (IOException ex) {
            }
        }
    }

    protected void doAction() { // (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] path = "req.getPathInfo()".split("/"); //todo request path

        if( path.length <= 1 ) {
            //resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            //return;
        }

        Class<?> servletClass = null;
        Method method = null; //무순메소드?

        try {
            servletClass = Class.forName("com.cactus.was."+"Hello");
            method = servletClass.getMethod("service", new Class[] {HttpRequest.class, HttpResponse.class} );
//            servletClass = Class.forName(controllerPackage + "." + WordUtils.capitalize(path[1]) );
//            method = servletClass.getMethod(path[2], new Class[] {HttpServletRequest.class, HttpServletResponse.class});
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        Object simpleServlet = null; //todo controller가 없으니까.. 변수명 변경

        try {
            simpleServlet = servletClass.newInstance(); //todo jdk9 newinstance deprecated
//            servletClass.getDeclaredConstructor().newInstance();  // replace?
            method.invoke(simpleServlet, req, res);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendHeader(Writer out, String responseCode, String contentType, int length)
            throws IOException {
        out.write(responseCode + "\r\n");
        Date now = new Date();
        out.write("Date: " + now + "\r\n");
        out.write("Server: JHTTP 2.0\r\n");
        out.write("Content-length: " + length + "\r\n");
        out.write("Content-type: " + contentType + "\r\n\r\n");
        out.flush();
    }

}
