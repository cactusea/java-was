package com.cactus.was.util;

import com.cactus.was.config.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.List;

public class Loader {

    private static Logger logger = LoggerFactory.getLogger(Loader.class);
    private static Configuration config;

    /**
     * Context ClassLoader
     */
    public static Configuration load(){
        logger.debug("============================");
        logger.debug("server config load start");
        logger.debug("============================");

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource("application.json");
        //todo const로 설정파일의 경로 관리하기?

        String path = url.getPath();
        File f = new File(path);

        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"))) {
            String line="";
            StringBuilder sb = new StringBuilder();
            while((line=br.readLine())!=null){
                sb.append(line);
            }

            JSONParser parser = new JSONParser();
            JSONObject appJson = (JSONObject) parser.parse(sb.toString());
            ObjectMapper objectMapper = new ObjectMapper();

            int port = (int)(long)appJson.get("port");
            List<Configuration.Servers> serverInfo = (List<Configuration.Servers>) appJson.get("servers");

            // Deserialization into the `Employee` class
//            Employee employee = objectMapper.readValue(json, Employee.class);

            Configuration config = objectMapper.readValue(appJson.toString(), Configuration.class);
            System.out.println(config);
//            config = new Configuration(port, serverInfo);
            return config;

        } catch (FileNotFoundException e){
            logger.error("FileNotFoundException!! :: {}", e);
        } catch (IOException e) {
            logger.error("IOException!! :: {}", e);
        } catch (ParseException e) {
            logger.error("ParseException!! :: {}", e);
        } catch(Exception e){
            logger.error("Exception!! :: {}", e);
        }

        return config;

    }
}
