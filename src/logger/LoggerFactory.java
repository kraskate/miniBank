package logger;

public class LoggerFactory {

    public static Logger getInstance(Class clazz) {
        return new FileLogger(clazz);
    }
}
