package com.cactus.was.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLConnection;
import java.util.List;

/**
 * 요청의 header 정보
 * host
 * http_root
 *
 */
public class Header {

    private static Logger logger = LoggerFactory.getLogger(Header.class);

    private String Host;
    private List<String> AcceptEncoding;

    public Header(String[] h){
        String reqline = h[0];
        String[] tokens = reqline.split("\\s+"); //공백자르기
        String version = "";
        String contentType =
                //todo 리턴할때 헤더에 타입 넣는 용도
                URLConnection.getFileNameMap().getContentTypeFor("static/was1/index.html");
        if (tokens.length > 2) {
            version = tokens[2]; //HTTP/1.1
        }

        if (version.startsWith("HTTP/")) { // send a MIME header
            //헤더 여기서쓰는게 맞아??
//                res.sendHeader(out, "HTTP/1.0 200 OK", contentType, theData.length);
        }
    }

}
