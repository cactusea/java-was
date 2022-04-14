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

public class ConfigLoader {

    private static Logger logger = LoggerFactory.getLogger(ConfigLoader.class);
    private static Configuration config;

    /**
     * Context ClassLoader
     */
    public static Configuration load(){
        logger.debug("server config load");

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource("application.json");
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

            Configuration config = objectMapper.readValue(appJson.toString(), Configuration.class);
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
