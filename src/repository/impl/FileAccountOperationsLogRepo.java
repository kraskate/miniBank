package repository.impl;

import logger.Logger;
import logger.LoggerFactory;
import mapper.AccountOperationLogMapper;
import model.AccountOperationsLog;
import repository.AccountOperationsLogRepo;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class FileAccountOperationsLogRepo implements AccountOperationsLogRepo {

    private static final Logger logger = LoggerFactory.getInstance(FileAccountOperationsLogRepo.class);
    private static final String ACCOUNT_OPS_LOG_FILE = "./resources/acc_ops_log.csv";

    private static AccountOperationsLogRepo instance;

    private AtomicLong logIdCounter;

    private FileAccountOperationsLogRepo() {
        try (FileReader fileReader = new FileReader(ACCOUNT_OPS_LOG_FILE);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            OptionalLong maxAccountOpsLogId = bufferedReader.lines()
                    .map(line -> line.split(",")[0]) // first element is id
                    .mapToLong(Long::parseLong)
                    .max();
            if (maxAccountOpsLogId.isPresent()) {
                logIdCounter = new AtomicLong(maxAccountOpsLogId.getAsLong());
                logger.debug( "Created logIdCounter with initial value: %d".formatted(logIdCounter.get()));
            } else {
                logIdCounter = new AtomicLong();
                logger.debug( "Created empty logIdCounter started with 0");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public static AccountOperationsLogRepo getInstance() {
        if (instance == null) {
            instance = new FileAccountOperationsLogRepo();
        }
        return instance;
    }

    @Override
    public AccountOperationsLog save(final AccountOperationsLog log) {
        try (FileWriter fileWriter = new FileWriter(ACCOUNT_OPS_LOG_FILE, true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
             PrintWriter printWriter = new PrintWriter(bufferedWriter)) {

            long newLogId = logIdCounter.incrementAndGet();
            logger.debug("Generated new logIdCounter value: %d".formatted(newLogId));
            log.setId(newLogId);
            String csvString = AccountOperationLogMapper.toCsv(log);
            printWriter.println(csvString);
            logger.debug("Saved new AccountOpsLog \"%s\"".formatted(csvString));
            return log;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    public List<AccountOperationsLog> findAllByAccountIdIn(final List<Long> accountIdList) {
        try (FileReader reader = new FileReader(ACCOUNT_OPS_LOG_FILE);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            Set<String> accountIdSet = accountIdList.stream().map(String::valueOf).collect(Collectors.toSet());
            return bufferedReader.lines()
                    .filter(line -> accountIdSet.contains(line.split(",")[3])) // third element is sourceAccId
                    .map(AccountOperationLogMapper::toObject)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error(e.getMessage());
            return Collections.emptyList();
        }
    }
}
