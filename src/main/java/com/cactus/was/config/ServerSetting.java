package com.cactus.was.config;

import com.cactus.was.util.Loader;

public class ServerSetting {
    private static int port;
    static ServerSetting ss; //todo 근데 굳이 이렇게 전역으로 빼서 내보내야되나..?

    private ServerSetting(){
        System.out.println("ServerSetting constructor");
        Configuration config = Loader.load();
        port = config.getPort();
;
        System.out.println("port:: "+port);
    }

    public static ServerSetting getInstance(){
        System.out.println("ServerSetting getInstance");
        ss = new ServerSetting();
        return ss;
    }

    public int getPort() {
        System.out.println("ServerSetting getPort");
        return port;
    }

    public ServerSetting getSs() {
        return ss;
    }
}
