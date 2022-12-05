package mapper;

import model.AccountOperationType;
import model.AccountOperationsLog;

public class AccountOperationLogMapper {

    private AccountOperationLogMapper() {
    }

    public static String toCsv(final AccountOperationsLog log) {

        CsvBuilder builder = new CsvBuilder();
        builder.add(log.getId())
                .add(log.getType())
                .add(log.getAmount())
                .add(log.getSourceAccountId());
        if (log.getTargetAccountId() != null) {
            return builder.addAndBuild(log.getTargetAccountId());
        } else {
            return builder.build();
        }

    }

    public static AccountOperationsLog toObject(final String csvString) {
        String[] strings = csvString.split(",");
        int i = 0;

        AccountOperationsLog log = new AccountOperationsLog();
        log.setId(Long.parseLong(strings[i++]));
        log.setType(AccountOperationType.valueOf(strings[i++]));
        log.setAmount(Integer.parseInt(strings[i++]));
        log.setSourceAccountId(Long.parseLong(strings[i++])); // i = 4
        if ((strings.length) > i) {
            log.setTargetAccountId(Long.parseLong(strings[i]));
        }

        return log;
    }
}
