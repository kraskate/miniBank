package logger;

import java.time.LocalDateTime;

public class ConsoleLogger implements Logger {

    private final Class clazz;
    private final LogLevel minimalLogLevel;


    public ConsoleLogger(Class clazz, LogLevel minimalLogLevel) {
        this.clazz = clazz;
        this.minimalLogLevel = minimalLogLevel;
    }


    @Override
    public void log(LogLevel level, String message) {
        if (level.ordinal() < minimalLogLevel.ordinal()) {
            return;
        }
            MessageBuilder builder = new MessageBuilder();
            builder.add(level)
                    .add(clazz)
                    .add(LocalDateTime.now())
                    .addAndBuild(message);
            System.out.println(builder);

    }

    @Override
    public void debug(String message) {
        log(LogLevel.DEBUG, message);
    }

    @Override
    public void info(String message) {
        log(LogLevel.INFO, message);
    }

    @Override
    public void warn(String message) {
        log(LogLevel.WARN, message);
    }

    @Override
    public void error(String message) {
        log(LogLevel.ERROR, message);
    }

    @Override
    public void fatal(String message) {
        log(LogLevel.FATAL, message);
    }
}
