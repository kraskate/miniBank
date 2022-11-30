package repository;

import model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    Optional<Account> findById(Long accountId);

    Account save(Account account);

    List<Account> findAllByUserId(Long userId);

}
