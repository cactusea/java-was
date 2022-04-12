package com.cactus.was.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configuration {

    int port;
    List<Servers> servers;
    String[] forbidden_type; //보안 규칙이 추가될 수 있으므로 배열로 구현
    Map<String, String> mapper = new HashMap<>();

    public Configuration(){

    }

    public static class Servers{
        String server_name;
        String http_root;
        String page_index;
        String page403;
        String page404;
        String page500;

        public String getServer_name() {
            return server_name;
        }

        public String getHttp_root() {
            return http_root;
        }

        public String getPage_index() {
            return page_index;
        }

        public String getPage403() {
            return page403;
        }

        public String getPage404() {
            return page404;
        }

        public String getPage500() {
            return page500;
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public List<Servers> getServers() {
        return servers;
    }

    public void setServers(List<Servers> servers) {
        this.servers = servers;
    }

    public String[] getForbidden_type() {
        return forbidden_type;
    }

    public void setForbidden_type(String[] forbidden_type) {
        this.forbidden_type = forbidden_type;
    }

    public Map<String, String> getMapper() {
        return mapper;
    }

    public void setMapper(Map<String, String> mapper) {
        this.mapper = mapper;
    }
}
