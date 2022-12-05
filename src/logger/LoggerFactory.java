package logger;

import exception.NoLogConfigFileException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LoggerFactory {

    public static Logger getInstance(Class clazz) {
        Properties properties = loadLoggerProperties();
        String level = properties.getProperty("log.level", "INFO");
        LogLevel minimalLogLevel = LogLevel.valueOf(level);

        String logTarget = properties.getProperty("log.target", "FILE");
        Target target = Target.valueOf(logTarget);

        switch (target) {
            case FILE: {
                return new FileLogger(clazz, minimalLogLevel);
            }
            case CONSOLE: {
                return new ConsoleLogger(clazz, minimalLogLevel);
            }
            default: {
                return null;
            }
        }

    }

    public static Properties loadLoggerProperties() {
        try (InputStream is = new FileInputStream("resources/log.properties")) {
            Properties properties = new Properties();
            properties.load(is);
            return properties;
        } catch (IOException e) {
            throw new NoLogConfigFileException();
        }
    }
}
