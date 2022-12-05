package repository;


import model.AccountOperationsLog;

import java.util.List;

public interface AccountOperationsLogRepo {

    AccountOperationsLog save(AccountOperationsLog log);
    List<AccountOperationsLog> findAllByAccountIdIn(List<Long> accountIdList);
}
