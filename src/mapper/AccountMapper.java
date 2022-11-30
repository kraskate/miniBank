package mapper;

import model.Account;

public class AccountMapper {

    public static String toCsv(Account account) {
        StringBuilder sb = new StringBuilder();
        sb.append(account.getId()).append(",");
        sb.append(account.getAmount()).append(",");
        sb.append(account.getUserId());

        return sb.toString();
    }

    public static Account toObject(String csvString) {
        String[] strings = csvString.split(",");
        int i = 0;

        Account account = new Account();

        account.setId(Long.parseLong(strings[i++]));
        account.setAmount(Integer.parseInt(strings[i++]));
        account.setUserId(Long.parseLong(strings[i++]));

        return account;
    }
}
