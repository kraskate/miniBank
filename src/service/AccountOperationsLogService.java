package service;

import model.Account;
import model.AccountOperationType;
import model.User;
import repository.AccountOperationsLogRepository;
import repository.AccountRepository;
import repository.impl.FileAccountOperationsRepository;
import repository.impl.FileAccountRepository;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;

import static model.AccountOperationType.WITHDRAW;

public class AccountOperationsLogService {

    private static AccountOperationsLogService instance;
    private final AccountOperationsLogRepository accountOperationsLogRepository = FileAccountOperationsRepository.getInstance();

    private AccountOperationsLogService() {
    }

    public static AccountOperationsLogService getInstance() {
        if (instance == null) {
            instance = new AccountOperationsLogService();
        }
        return instance;
    }


    public List<String> getOperations(User user) {


    }


    public void exportOperations(User user) {


    }

    public void logOperation(AccountOperationType transfer, int transferAmount, Account sourceAccount, Account targetAccount) {


    }
}
