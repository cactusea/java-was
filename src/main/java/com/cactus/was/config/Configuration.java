package com.cactus.was.config;

import java.util.List;

public class Configuration {

    int port;
    List<Servers> servers;

    public Configuration (int port, List<Servers> servers){
        System.out.println("Configuration constructor");
        this.port = port;
        this.servers = servers;
    }

    public class Servers{
        String serverName;
        String httpRoot;
        String[] forbidden_type; //보안 규칙이 추가될 수 있으므로 배열로 구현
        String pageIndex;
        String page403;
        String page404;
        String page500;

        String memo; //todo 임시변수임

        public String getServerName() {
            return serverName;
        }

        public String getHttpRoot() {
            return httpRoot;
        }

        public String[] getForbidden_type() {
            return forbidden_type;
        }

        public String getPageIndex() {
            return pageIndex;
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


}
