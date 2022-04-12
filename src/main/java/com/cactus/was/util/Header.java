package com.cactus.was.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private String Host;
    private String method;
    private String version;
    private Map<String, String> params = new HashMap<>();
    private List<String> AcceptEncoding;

    public Header(String[] h){
        String[] tokens = h[0].split("\\s+"); //공백자르기
        String method = tokens[0];
        String host = h[1].split(":")[1]; //ex localhost:8000 -> localhost
        String contentType = URLConnection.getFileNameMap().getContentTypeFor("static/was1/index.html");
        String version = tokens[2];

        // QueryString to parameter(map)
        if(tokens[1].contains("?")){ //localhost:8080?name=cactus&age=3
            String[] param = tokens[1].split("\\?");
            Map<String, String> paramMap = Pattern.compile("&")
                    .splitAsStream(param[1])
                    .map(s->s.split("="))
                    .collect(Collectors.toMap(k->k[0], v->v[1]));

//            params = paramMap;
            setParams(paramMap);
        }

        setMethod(method);
        setHost(host);

        if (tokens.length > 2) {
            setVersion(tokens[2]); //HTTP/1.1
        }

    }

    public String getHost() {
        return Host;
    }

    public void setHost(String host) {
        Host = host;
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

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public List<String> getAcceptEncoding() {
        return AcceptEncoding;
    }

    public void setAcceptEncoding(List<String> acceptEncoding) {
        AcceptEncoding = acceptEncoding;
    }
}
