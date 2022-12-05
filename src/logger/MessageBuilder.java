package logger;

import mapper.CsvBuilder;

public class MessageBuilder {

    private final StringBuilder builder;

    public MessageBuilder() {
        this.builder = new StringBuilder();
    }

    public static MessageBuilder newBuilder() {
        return new MessageBuilder();
    }

    public MessageBuilder add(Object o) {
        builder.append("[").append(o).append("] ");
        return this;
    }

    public String addAndBuild(Object o) {
        builder.append(o);
        return builder.toString();
    }

}
