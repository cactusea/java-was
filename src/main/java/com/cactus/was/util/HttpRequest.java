package com.cactus.was.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * HTTP 요청 정보(클라이언트 요청, 쿠키, 세션 등)를 제공하는 인터페이스
 * HTTP 프로토콜의 request 정보를 서블릿에게 전달하기 위한 목적으로 사용한다.
 * Message Body의 Stream을 읽어들이는 메서드를 가지고 있다.
 *
 * ex)
 * 메서드 예시
 * getParameterNames() : 현재 요청에 포함된 매개변수 이름을 열거 형태로 넘겨준다.
 * getParameter(name) : 문자열 name과 같은 이름의 매개변수를 가져온다.
 */
public class HttpRequest {

    private static Logger logger = LoggerFactory.getLogger(HttpRequest.class);

    private InputStream ins;
    private String fileName;
    private String parameter;
//    private String pathInfo;

    public HttpRequest(InputStream ins) {
        this.ins = ins;
    }

    public void setting(){
        try{
            BufferedReader re = new BufferedReader(new InputStreamReader(new BufferedInputStream(this.ins), "UTF-8"));
            StringBuilder requestLine = new StringBuilder();

            while(re.ready()){
                requestLine.append(re.readLine()).append("\r\n");
            }

            String get = requestLine.toString();
            String[] getArr = get.split("\r\n");

            //흠...헤더 토큰인데....
            String[] tokens = getArr[0].split("\\s+");
            String fileName = tokens[1];
            setFileName(fileName); //todo 흠 전역 쓰는것보다 세터쓰는게 훨날듯..

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getParameter(String parameter) {
       return parameter;
    }
}
