package mapper;

public class CsvBuilder {

    private final StringBuilder builder;

    public CsvBuilder() {
        this.builder = new StringBuilder();
    }

    public CsvBuilder add(Object o) {
        builder.append(o.toString()).append(",");
        return this;
    }

    public String addAndBuild(Object o) {
        builder.append(o.toString());
        return builder.toString();
    }

    public String build() {
        if (builder.charAt(builder.length() - 1) == ',') {
            builder.deleteCharAt(builder.length()-1);
        }
        return builder.toString();
    }
}
