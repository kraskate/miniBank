package logger;

import mapper.CsvBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class FileLogger implements Logger {

    private static final String LOG_FILE_PATH = "./resources/log.txt";
    private final Class clazz;
    private final LogLevel minimalLogLevel;


    FileLogger(Class clazz, LogLevel minimalLogLevel) {
        this.clazz = clazz;
        this.minimalLogLevel = minimalLogLevel;
    }


    @Override
    public void log(LogLevel level, String message) {
        if (level.ordinal() < minimalLogLevel.ordinal()) {
            return;
        }

        try (FileWriter fileWriter = new FileWriter(LOG_FILE_PATH, true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {


            MessageBuilder builder = new MessageBuilder();
            builder.add(level)
                    .add(clazz)
                    .add(LocalDateTime.now())
                    .addAndBuild(message);
            printWriter.println(builder);

        } catch (IOException e) {
            e.printStackTrace();
        }

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
