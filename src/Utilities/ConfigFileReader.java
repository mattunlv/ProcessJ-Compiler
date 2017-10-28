package Utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigFileReader {

    public static Properties getConfiguration() {
        Properties config = new Properties();
        InputStream input = null;

        try {

            String home = System.getProperty("user.home");
            String configFile = home + "/.pjconfig";
            input = new FileInputStream(configFile);
            config.load(input);

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return config; 
    }
}
