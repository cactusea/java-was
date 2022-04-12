package com.cactus.was.util;

import com.cactus.was.config.Configuration;
//import com.cactus.was.config.ServerSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.List;
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

    private InputStream ins;
    private Header header;
    public String fileName; //todo 아 이거 목적지 파일 이름이었어^^;;; 헷가렬서 이름 바꿔야겠
    public Map<String, String> params = new HashMap<>();
    public String rhost;
    public String rdest;

//    private String requestURL; //http://localhost/Project/project.jsp
//    private String requestURI; ///Project/project.jsp
//    private String remoteHost; //127.0.0.1
//    private String sererName;  //localhost
//    private int serverPort;    //8000

    public HttpRequest(InputStream ins) {
        this.ins = ins;
    }

    public void setting(){
        try(BufferedReader br = new BufferedReader(new InputStreamReader(new BufferedInputStream(this.ins), "UTF-8"))){
            StringBuilder reqMsg = new StringBuilder();

            while(br.ready()){
                reqMsg.append(br.readLine()).append("\r\n");
            }

            Configuration config = ConfigLoader.load();
            String reqStr = reqMsg.toString();
            String[] reqMsgHeader = reqStr.split("\r\n");
            Header header = new Header(reqMsgHeader);
            setParameter(header.getParams());

            //filename 리턴해주는 방법
            //현재 host 정보를 가져온다
            String host = header.getHost(); //www.was1.com
            String dest = header.getReqUrl(); //hello //todo mapper에서 매칭

            // ex 1) was1.com/
            // ex 2) was1.com/Hello
            // ex 3) was1.com/service.Hello

            //application.json에서 host값에 매핑된 목적지파일 주소를 가져온다
            List<Configuration.Servers> serverList = config.getServers();

            String rhost = "";
            for (Configuration.Servers server : serverList) {
                if (server.getServer_name().equals(host)) {
                    rhost = server.getHttp_root();
                    break;
                }
            }
            Map<String, String> mapperMap = config.getMapper();
            String rdest = mapperMap.get(dest);
            logger.info("rhost :: {}", rhost);
            logger.info("rdest :: {}", rdest);
            setRdest(rdest);
            setRhost(rhost);


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
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

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getRhost() {
        return rhost;
    }

    public void setRhost(String rhost) {
        this.rhost = rhost;
    }

    public String getRdest() {
        return rdest;
    }

    public void setRdest(String rdest) {
        this.rdest = rdest;
    }
}
