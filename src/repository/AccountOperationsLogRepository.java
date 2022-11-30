package repository;

import model.Account;
import model.AccountOperationsLog;

import java.util.List;
import java.util.Optional;

public interface AccountOperationsLogRepository {

    AccountOperationsLog save(AccountOperationsLog accountOperationsLog);

    List<AccountOperationsLog> findAllByAccountId(List<Long> accountIdList);
}
