package com.cactus.was.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 요청의 header 정보
 * host
 * http_root
 *
 */
public class Header {

    private static Logger logger = LoggerFactory.getLogger(Header.class);

    private String host;
    private String method;
    private String version;
    private String reqUrl;
    private String contentType;
    private Map<String, String> params = new HashMap<>();

    public Header(String[] reqMsgHeader){
        //요청의 첫줄은 Request 정보이므로 배열의 0번으로 고정
        String[] tokens = reqMsgHeader[0].split("\\s+");
        //todo .... length처리?
        String method = tokens[0];
        String reqUrl = tokens[1];
        String version = "";

//        String fileName = tokens[1];
//        if (reqUrl.endsWith("/")) fileName += indexFileName;
        if (tokens.length > 2) {
            version = tokens[2];
        }

        String host = Arrays.stream(reqMsgHeader).filter(k->k.indexOf("Host:")>-1).findFirst().orElse("");
        //todo 흠 여기서 orelse에 걸렸으면 어떻게처리를 할까...???
        if(host.length()>1){
            host = host.split(":")[1];
        }

        // QueryString to parameter(map)
        if(tokens[1].contains("?")){ //localhost:8080?name=cactus&age=3
            String[] param = tokens[1].split("\\?");
            Map<String, String> paramMap = Pattern.compile("&")
                    .splitAsStream(param[1])
                    .map(s->s.split("="))
                    .collect(Collectors.toMap(k->k[0], v->v[1]));

            setParams(paramMap);
        }

        //setter
        setMethod(method);
        setHost(host);
        setReqUrl(reqUrl);
        setVersion(version);
//        setContentType(contentType);

    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getReqUrl() {
        return reqUrl;
    }

    public void setReqUrl(String reqUrl) {
        this.reqUrl = reqUrl;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

}
