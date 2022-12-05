package service;

import logger.Logger;
import logger.LoggerFactory;
import mapper.AccountOperationLogMapper;
import model.Account;
import model.AccountOperationType;
import model.AccountOperationsLog;
import model.User;
import repository.AccountOperationsLogRepo;
import repository.AccountRepository;
import repository.impl.FileAccountOperationsLogRepo;
import repository.impl.FileAccountRepository;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class AccountOperationsLogService {
    private static AccountOperationsLogService instance;

    private static final Logger log = LoggerFactory.getInstance(AccountOperationsLogService.class);
    private final AccountOperationsLogRepo logRepo = FileAccountOperationsLogRepo.getInstance();
    private final AccountRepository accountRepository = FileAccountRepository.getInstance();

    private AccountOperationsLogService() {
    }

    public static AccountOperationsLogService getInstance() {
        if (instance == null) {
            instance = new AccountOperationsLogService();
        }
        return instance;
    }

    public void logOperation(final AccountOperationType type, final Integer amount,
                             final Account sourceAcc) {
        logOperation(type, amount, sourceAcc, null);
    }

    public void logOperation(final AccountOperationType type, final Integer amount,
                             final Account sourceAcc, final Account targetAccount) {
        logRepo.save(new AccountOperationsLog(type, amount, sourceAcc.getId(),
                targetAccount != null ? targetAccount.getId() : null));
    }

    public List<AccountOperationsLog> getOperations(final User user) {
        List<Long> accountIdList = user.getAccounts()
                .stream()
                .map(Account::getId)
                .collect(Collectors.toList());
        return logRepo.findAllByAccountIdIn(accountIdList);
    }

    public void exportOperations(final User user) {
        List<AccountOperationsLog> toExport = getOperations(user);

        String exportFileName = "user_" + user.getId() + "_operations_log_" + LocalDateTime.now() + ".csv";

        try (FileWriter fileWriter = new FileWriter("./resources/export/" + exportFileName);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
             PrintWriter printWriter = new PrintWriter(bufferedWriter)) {

            toExport.stream()
                    .map(AccountOperationLogMapper::toCsv)
                    .forEach(printWriter::println);

            log.debug("Exported user %d account operations logs %s".formatted(user.getId(), exportFileName));
        } catch (IOException e) {
            log.error("Error during exporting users %d account operations logs. %s".formatted(user.getId(), e.getMessage()));
        }
    }

    public void importOperations(final User user) {
        List<AccountOperationsLog> accountOperationsLogs = readImportFile();
    }

    private List<AccountOperationsLog> readImportFile() {
        File importDir = new File("./resources/import");
        File[] files = importDir.listFiles();
        File workingFile = null;
        if (files != null && files.length > 0) {
            workingFile = files[0];
        }

        if (workingFile == null) {
            log.error("Нет файла в искомой папке");
            return Collections.emptyList();
        }

        try (FileReader fileReader = new FileReader(workingFile);
             BufferedReader br = new BufferedReader(fileReader)) {

            List<AccountOperationsLog> importedLogs = br.lines()
                    .map(AccountOperationLogMapper::toObject)
                    .collect(Collectors.toList());
            processImport(importedLogs);

            workingFile.delete();
        } catch (IOException e) {
            log.error("Error reading import file. " + e.getMessage());
        }

        return Collections.emptyList();
    }

    private void processImport(final List<AccountOperationsLog> accountOperationsLogs) {
        Map<Long, Account> accountByIdMap = accountOperationsLogs.stream()
                .map(acc -> new Long[]{acc.getSourceAccountId(), acc.getTargetAccountId()})
                .flatMap(Arrays::stream)
                .filter(Objects::nonNull)
                .map(accountRepository::findById)
                .map(acc -> acc.orElse(null))
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toMap(Account::getId, acc -> acc));

        log.debug("Started processing imported account operations logs");

        for (var log : accountOperationsLogs) {
            Long sourceAccountId = log.getSourceAccountId();
            Long targetAccountId = log.getTargetAccountId();
            switch (log.getType()) {
                case WITHDRAW: {
                    if (accountByIdMap.containsKey(sourceAccountId)) {
                        Account account = accountByIdMap.get(sourceAccountId);
                        if (account.getAmount() - log.getAmount() >= 0) {
                            account.setAmount(account.getAmount() - log.getAmount());
                            accountRepository.save(account);
                        }
                    }
                    break;
                }
                case PUT: {
                    if (accountByIdMap.containsKey(sourceAccountId)) {
                        Account account = accountByIdMap.get(sourceAccountId);
                        if (Integer.MAX_VALUE - log.getAmount() > account.getAmount()) {
                            account.setAmount(account.getAmount() + log.getAmount());
                            accountRepository.save(account);
                        }
                    }
                    break;
                }
                case TRANSFER: {
                    if (accountByIdMap.containsKey(sourceAccountId) && accountByIdMap.containsKey(targetAccountId)) {
                        Account sourceAccount = accountByIdMap.get(sourceAccountId);
                        Account targetAccount = accountByIdMap.get(targetAccountId);
                        if (sourceAccount.getAmount() - log.getAmount() >= 0
                                && Integer.MAX_VALUE - log.getAmount() > targetAccount.getAmount()) {
                            sourceAccount.setAmount(sourceAccount.getAmount() - log.getAmount());
                            targetAccount.setAmount(targetAccount.getAmount() + log.getAmount());

                            accountRepository.save(sourceAccount);
                            accountRepository.save(targetAccount);
                        }
                    }
                    break;
                }
            }
        }
    }

}
