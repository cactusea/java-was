package com.cactus.was;

import com.cactus.was.config.Configuration;
import com.cactus.was.util.ConfigLoader;
import com.cactus.was.util.HttpRequest;
import com.cactus.was.util.HttpResponse;
import com.cactus.was.util.RequestProcessor;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

public class HttpServerTest {

    Configuration config;
    static final String HOST1 = "www.was1.com";
    static final String HOST2 = "www.was2.com";

    @Before
    public void testDataSetting(){
        config = ConfigLoader.load();
        assertNotNull(config);
    }

    @Test
    public void host_was1_구분(){
        String rhost="";
        String rindex="";
        for(Configuration.Servers server : config.getServers()){
            if (server.getServer_name().equals(HOST1)) {
                rhost = server.getHttp_root();
                rindex = server.getPage_index();
                break;
            }
        }
        assertEquals(rhost, "template/was1");
        assertEquals(rindex, "/index.html");
    }

    @Test
    public void host_was2_구분(){
        String rhost="";
        String rindex="";
        for(Configuration.Servers server : config.getServers()){
            if (server.getServer_name().equals(HOST2)) {
                rhost = server.getHttp_root();
                rindex = server.getPage_index();
                break;
            }
        }
        assertEquals(rhost, "template/was2");
        assertEquals(rindex, "/index.html");
    }

    @Test
    public void 보안규칙위반_테스트_상위디렉토리접근 (){
        String req = "/../../../../etc/passwd";

        boolean result=false;
        for(String ft : config.getForbidden_type()){
            if(req.contains(ft)){
                result=true;
            }
        }
        assertTrue(result);
    }

    @Test
    public void 보안규칙위반_테스트_금지_확장자호출 (){
        String req = "/test.exe";

        boolean result=false;
        for(String ft : config.getForbidden_type()){
            if(req.contains(ft)){
                result=true;
            }
        }
        assertTrue(result);
    }

    @Test
    public void 보안규칙위반_테스트_일반_확장자호출 (){
        String req = "/test.txt";

        boolean result=false;
        for(String ft : config.getForbidden_type()){
            if(req.contains(ft)){
                result=true;
            }
        }
        assertFalse(result);
    }

    @Test
    public void Hello_테스트 (){
        call_servlet("com.cactus.was.servlet.Hello");
    }

    @Test
    public void service_Hello_테스트 (){
        call_servlet("com.cactus.was.servlet.service.Hello");
    }

    @Test
    public void 현재시각_출력(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf.format(date));
    }

    @Test
    public void DateServlet_호출_테스트 (){
        call_servlet("com.cactus.was.servlet.DateServlet");
    }

    public void call_servlet(String classname){
        Class<?> servletClass = null;
        Method method = null;

        try {
            servletClass = Class.forName(classname);
            method = servletClass.getMethod("service", new Class[] {HttpRequest.class, HttpResponse.class} );
        } catch (Exception e) {
            e.printStackTrace();
        }

        Object simpleServlet = null;
        try {
            simpleServlet = servletClass.getDeclaredConstructor().newInstance();
            method.invoke(simpleServlet, new HttpRequest(new InputStream() {
                @Override
                public int read() throws IOException {
                    return 0;
                }
            }), new HttpResponse(new OutputStream() {
                @Override
                public void write(int b) throws IOException {

                }
            }));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
