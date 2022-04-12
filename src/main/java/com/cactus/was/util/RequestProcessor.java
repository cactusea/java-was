package com.cactus.was.util;

import com.cactus.was.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
     * inputstream, outputstream setting
     * req, res setting
     */
    public void init(){
        try {
            ins = connection.getInputStream();
            ous = connection.getOutputStream();
            req = new HttpRequest(ins);
            res = new HttpResponse(ous);

            OutputStream raw = new BufferedOutputStream(connection.getOutputStream());
            Writer out = new OutputStreamWriter(raw);
            this.out = out;
        }catch (IOException e){
            e.printStackTrace();
        }
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
            init();
            req.setting();

            String fileName = req.getFileName();
            String contentType = URLConnection.getFileNameMap().getContentTypeFor(fileName);

            // 파일읽어오기
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            URL url = loader.getResource("static/was1/index.html"); //todo 대상 파일 적용.. 라우팅 결과물을 가져와서 넣어야한다.

            if(url==null){
                url = loader.getResource("static/was1/404.html");
                //throw exception?
            }

            String path = url.getPath();
            File theFile = new File(path);

            if (theFile.canRead()) {
                byte[] theData = Files.readAllBytes(theFile.toPath());
//                if (version.startsWith("HTTP/")) { // send a MIME header
//                    sendHeader(writer, "HTTP/1.0 200 OK", contentType, theData.length);
//                }
                // send the file; it may be an image or other binary data
                // so use the underlying output stream
                // instead of the writer
                //todo 흠 헤더...
                sendHeader(out, "HTTP/1.0 200 OK", contentType, theData.length);

                ous.write(theData);
                ous.flush();
            } else {
                logger.warn("file cannot read");
            }

        } catch (IOException ex) {
            logger.warn("Error talking to " + connection.getRemoteSocketAddress(), ex);
        } finally {
            try {
                connection.close();
            } catch (IOException ex) {
            }
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


    /**
     * ㅠㅠ
     */
    public void run_o() {
        System.out.println("request processor run");

        try {
            ous = connection.getOutputStream();
            ins = connection.getInputStream();

            //httpreq, res 클라이언트 생성
            req = new HttpRequest(ins);
            res = new HttpResponse(ous);

            // 파일읽어오기
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            URL url = loader.getResource("static/was1/index.html"); //todo 대상 파일 적용.. 여기서 라우팅을??
            String path = url.getPath();
            File theFile = new File(path);

            if(theFile.canRead()){
//                byte[] theData = Files.readAllBytes(theFile.toPath());
                //todo send header
//                response.getWriter().flush();
//                response.send(data);

                String body = new StringBuilder("<HTML>\r\n")
                        .append("<HEAD><TITLE>Index</TITLE>\r\n")
                        .append("</HEAD>\r\n")
                        .append("<BODY>")
                        .append("<H1>HTTP test..</H1>\r\n")
                        .append("</BODY></HTML>\r\n")
                        .toString();

                res.getWriter().write(body);
                res.getWriter().flush();
//                res.write(body);
            }else{
                //404
            }
        } catch (IOException ex) {
            logger.warn("Error talking to :: {}", connection.getRemoteSocketAddress(), ex);
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (IOException e) {
                logger.error("Connection close exception :: {}", e);
            }
        }
    }

}
