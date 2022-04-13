package com.cactus.was.util;

import com.cactus.was.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    private static Logger logger = LoggerFactory.getLogger(HttpRequest.class);

    private InputStream ins;
    //헤더정보
    private Header header;
    //요청받은 파일
    public String fileName;
    //파일 경로
    public String filePath;
    //
//    public String name;
    //host별 정보
    public Configuration.Servers serverConfig;
    //접근금지
    public String[] forbiddenType;
    //파라미터
    public Map<String, String> paramMap = new HashMap<>();

    public HttpRequest(InputStream ins) {
        this.ins = ins;
    }

    public void setting(){
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(new BufferedInputStream(this.ins), "UTF-8"));
            StringBuilder reqMsg = new StringBuilder();
            while(br.ready()){
                reqMsg.append(br.readLine()).append("\r\n");
            }

            Configuration config = ConfigLoader.load();
            String reqStr = reqMsg.toString();
            String[] reqMsgHeader = reqStr.split("\r\n");
            Header header = new Header(reqMsgHeader);
            setHeader(header);
            setParamMap(header.getParams());

            //현재 host 정보를 가져온다
            String host = header.getHost();
            String fileName = header.getReqUrl();

            //todo processor에서 처리..?
            //application.json에서 host값에 매핑된 목적지파일 주소를 가져온다
            List<Configuration.Servers> serverList = config.getServers();

            String rhost = host; //매핑된 host 주소값의 http_root 경로
            String rindex = "/"; //mapper에서 매핑된 정보값 사용
            for (Configuration.Servers server : serverList) {
                if (server.getServer_name().equals(host)) {
                    rhost = server.getHttp_root();
                    rindex = server.getPage_index();
                    setServerConfig(server);
                    break;
                }
            }
            Map<String, String> mapperMap = config.getMapper();

            String filePath="";
            if (fileName.endsWith("/")) {
                filePath = rhost + rindex;
            } else {
                if(mapperMap.get(fileName)!=null){
                    filePath = mapperMap.get(fileName);
                }
            }
            setFileName(fileName);
            setFilePath(filePath);
            setForbiddenType(config.getForbidden_type());

        } catch (UnsupportedEncodingException e) {
            logger.error("UnsupportedEncodingException");
            e.printStackTrace();
        } catch (IOException e) {
            logger.error("IOException");
            e.printStackTrace();
        }
    }

    public String getParameter(String key) {
        String param = getParamMap().get(key);
        return param;
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

    public Configuration.Servers getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(Configuration.Servers serverConfig) {
        this.serverConfig = serverConfig;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Map<String, String> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, String> paramMap) {
        this.paramMap = paramMap;
    }

    public String[] getForbiddenType() {
        return forbiddenType;
    }

    public void setForbiddenType(String[] forbiddenType) {
        this.forbiddenType = forbiddenType;
    }
}
