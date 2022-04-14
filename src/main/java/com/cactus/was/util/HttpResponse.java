package com.cactus.was.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Date;

/**
 * content-type, 응답 코드, 응답 메세지 등
 */
public class HttpResponse {

    private static Logger logger = LoggerFactory.getLogger(HttpResponse.class);

    private OutputStream ous;
    private Writer out;

    public HttpResponse(OutputStream ous){
        this.ous = ous;
    }
    /**
     * 전송할 Header 정보를 세팅한다.
     * @param out
     * @param responseCode
     * @param contentType
     * @param length
     * @throws IOException
     */
    public void sendHeader(Writer out, String responseCode, String contentType, int length)  throws IOException {
        out.write(responseCode + "\r\n");
        Date now = new Date();
        out.write("Date: " + now + "\r\n");
        out.write("Server: JHTTP 2.0\r\n");
        out.write("Content-length: " + length + "\r\n");
        out.write("Content-type: " + contentType + "\r\n\r\n");
        this.out = out;
        out.flush();
    }

    public Writer getWriter(){
        if(out==null){
            out = new OutputStreamWriter(ous);
        }
        return out;
    }

}
