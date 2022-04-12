package com.cactus.was.util;

import com.cactus.was.config.Configuration;
import com.cactus.was.config.ServerSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

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

//    private Header header;
    private InputStream ins;
    private String fileName; //todo 아 이거 목적지 파일 이름이었어^^;;; 헷가렬서 이름 바꿔야겠
    private String requestURL; //http://localhost/Project/project.jsp
    private String requestURI; ///Project/project.jsp
    private String remoteHost; //127.0.0.1
    private String sererName;  //localhost
    private int serverPort;    //8000

    private Map<String, String> params = new HashMap<>();

    public HttpRequest(InputStream ins) {
        this.ins = ins;
    }

    public void setting(){
        try{
            BufferedReader re = new BufferedReader(new InputStreamReader(new BufferedInputStream(this.ins), "UTF-8"));
            StringBuilder requestLine = new StringBuilder();
            ServerSetting ss = ServerSetting.getInstance();

            while(re.ready()){
                requestLine.append(re.readLine()).append("\r\n");
            }

            String get = requestLine.toString();
            String[] getArr = get.split("\r\n");
            //todo 변수명....
            Header h = new Header(getArr);
            setParameter(h.getParams());

            //filename 리턴해주는 방법
            //현재 host 정보를 가져온다
            String host = h.getHost(); //www.was1.com
            String dest = h.getReqUrl(); //hello

            String rhost = "";
            //application.json에서 host값에 매핑된 목적지파일 주소를 가져온다
            for(Configuration.Servers server : ss.getServers()){
                if(server.getServerName().equals(host)){
                    rhost = server.getHttpRoot();

                    //요ㅛ청주소를...... 를...
                    //요청이 파일이 아니면어떻게함?! 그래도 파일인건가?!?!
                    //아니지 키워드에 매핑된 디렉토리 경로가 있으니까 그 클래스를 파일로 하면.. ??
                }
            }




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

    public String getParameter(String name) {
        if(this.params.size()>0){

        }
        String param = this.params.get(name);
        return param;
    }

    public void setParameter(Map<String, String> params) {
        this.params = params;
    }
}
