package utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyValues {

    public static String getProperty(String propertyKey) {
        Properties properties = new Properties();

        try {
            String propFileName = "application.properties";
            InputStream inputStream = PropertyValues.class.getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties.getProperty(propertyKey);
    }
}
