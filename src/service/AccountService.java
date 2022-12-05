package service;

import model.Account;
import repository.AccountRepository;
import repository.impl.FileAccountRepository;

import static model.AccountOperationType.*;

public class AccountService {

    private static AccountService instance;
    private final AccountRepository accountRepository = FileAccountRepository.getInstance();
    private final AccountOperationsLogService accountOperationsLogService = AccountOperationsLogService.getInstance();

    private AccountService() {
    }

    public static AccountService getInstance() {
        if (instance == null) {
            instance = new AccountService();
        }
        return instance;
    }


    public boolean withdraw(Account account, int withdrawAmount) {
        if (account.getAmount() - withdrawAmount >= 0) {
            account.setAmount(account.getAmount() - withdrawAmount);
            accountRepository.save(account);
            accountOperationsLogService.logOperation(WITHDRAW, withdrawAmount, account, null);
            return true;
        } else {
            return false;
        }
    }

    public boolean addSomeMoney(Account account, int addAmount) {
        if (Integer.MAX_VALUE - addAmount > account.getAmount() ) {
            account.setAmount(account.getAmount() + addAmount);
            accountRepository.save(account);
            accountOperationsLogService.logOperation(PUT, addAmount, account, null);
            return true;
        } else {
            return false;
        }
    }


    public boolean exists(long toAccountNumber) {
        return accountRepository.findById(toAccountNumber).isPresent();
    }

    public boolean transfer(Account sourceAccount, long toAccountNumber, int transferAmount) {
        Account targetAccount = accountRepository.findById(toAccountNumber).get();
        if (sourceAccount.getAmount() - transferAmount >= 0 && Integer.MAX_VALUE - transferAmount > targetAccount.getAmount()) {
            sourceAccount.setAmount(sourceAccount.getAmount() - transferAmount);
            targetAccount.setAmount(targetAccount.getAmount() + transferAmount);

            accountRepository.save(sourceAccount);
            accountRepository.save(targetAccount);

            accountOperationsLogService.logOperation(TRANSFER, transferAmount, sourceAccount, targetAccount);
            return true;
        } else {
            return false;
        }


    }
}
