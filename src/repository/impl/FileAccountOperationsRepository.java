package repository.impl;

import logger.Logger;
import logger.LoggerFactory;
import mapper.AccountOperationMapper;
import model.AccountOperationsLog;
import repository.AccountOperationsLogRepository;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.OptionalLong;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class FileAccountOperationsRepository implements AccountOperationsLogRepository {

    private static FileAccountOperationsRepository instance;
    private static final String OPERATIONS_FILE_PATH = "./resources/acc_ops_log.csv";
    private static final Logger log = LoggerFactory.getInstance(FileAccountOperationsRepository.class);

    private AtomicLong accountOperationIdCounter;

    private FileAccountOperationsRepository() {
        try (FileReader fileReader = new FileReader(OPERATIONS_FILE_PATH);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            OptionalLong maxAccountOperationId = bufferedReader.lines()
                    .map(line -> line.split(",")[0])
                    .mapToLong(Long::parseLong)
                    .max();

            if (maxAccountOperationId.isPresent()) {
                accountOperationIdCounter = new AtomicLong(maxAccountOperationId.getAsLong());
                log.debug("Created AccountOperationIdCounter with initial value: %d".formatted(accountOperationIdCounter.get()));
            } else {
                accountOperationIdCounter = new AtomicLong();
                log.debug("Created empty accountOperationIdCounter started with 0");
            }

        } catch (IOException e) {
            log.error("Error during initializing accountOperationIdCounter." + e.getMessage());
        }
    }

    public static AccountOperationsLogRepository getInstance() {
        if (instance == null) {
            instance = new FileAccountOperationsRepository();
        }
        return instance;
    }

    @Override
    public AccountOperationsLog save(AccountOperationsLog accountOperationsLog) {
        return insert(accountOperationsLog);
    }

    private AccountOperationsLog insert(AccountOperationsLog accountOperationsLog) {

        //Получаем новый id.
        //Обогощаем пользователя id
        //Конвертируем пользователя в строку и сохраняем последним

        try (FileWriter fileWriter = new FileWriter(OPERATIONS_FILE_PATH, true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
             PrintWriter printWriter = new PrintWriter(bufferedWriter)) {

            long newAccountOperationId = accountOperationIdCounter.incrementAndGet();
            log.debug("Generated new accountOperationIdCounter value: %d".formatted(newAccountOperationId));

            accountOperationsLog.setId(newAccountOperationId);

            String csvString = AccountOperationMapper.toCsv(accountOperationsLog);

            printWriter.println(csvString);
            log.debug("Saved new Operation \"%s\"".formatted(csvString));

            return accountOperationsLog;
        } catch (IOException e) {
            log.error("Error during inserting new account operation. " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<AccountOperationsLog> findAllByAccountId(List<Long> accountId) {
        try (FileReader fileReader = new FileReader(OPERATIONS_FILE_PATH);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            return bufferedReader.lines()
                    .filter(line -> line.split(",")[3].equals(sourceAccountId.toString()))
                    .map(AccountOperationMapper::toObject)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Can't find account %d ".formatted(sourceAccountId) + ".\n" + e.getMessage());
            return Collections.emptyList();
        }
    }


}
