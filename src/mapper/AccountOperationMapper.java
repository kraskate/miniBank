package mapper;

import model.AccountOperationType;
import model.AccountOperationsLog;

public class AccountOperationMapper {


    public static String toCsv(AccountOperationsLog accountOperationsLog) {
        StringBuilder sb = new StringBuilder();
        sb.append(accountOperationsLog.getId()).append(",");
        sb.append(accountOperationsLog.getType()).append(",");
        sb.append(accountOperationsLog.getAmount());
        sb.append(accountOperationsLog.getSourceAccountId());
        sb.append(accountOperationsLog.getTargetAccountId());

        return sb.toString();
    }

    public static AccountOperationsLog toObject(String csvString) {
        String[] strings = csvString.split(",");
        int i = 0;

        AccountOperationsLog accountOperationsLog = new AccountOperationsLog();

        accountOperationsLog.setId(Long.parseLong(strings[i++]));
        accountOperationsLog.setType(AccountOperationType.valueOf((strings[i++])));
        accountOperationsLog.setAmount(Integer.parseInt(strings[i++]));
        accountOperationsLog.setSourceAccountId(Long.parseLong(strings[i++]));
        accountOperationsLog.setTargetAccountId(Long.parseLong(strings[i++]));

        return accountOperationsLog;
    }
}
