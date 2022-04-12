package com.cactus.was.config;

import com.cactus.was.util.Loader;

import java.util.List;

public class ServerSetting {
    public static ServerSetting ss;
    public static int port;
    public static List<Configuration.Servers> servers;

    private ServerSetting(){
        Configuration config = Loader.load();
        setPort(config.getPort());
        setServers(config.getServers());
    }

    public static ServerSetting getInstance(){
        ss = new ServerSetting();
        setSs(ss);
        return getSs();
    }

    public int getPort() {
        return port;
    }

    public static void setPort(int port) {
        ServerSetting.port = port;
    }

    public static ServerSetting getSs() {
        return ss;
    }

    public static void setSs(ServerSetting ss) {
        ServerSetting.ss = ss;
    }

    public static List<Configuration.Servers> getServers() {
        return servers;
    }

    public static void setServers(List<Configuration.Servers> servers) {
        ServerSetting.servers = servers;
    }
}
