package repository.impl;

import logger.Logger;
import logger.LoggerFactory;
import mapper.AccountMapper;
import model.Account;
import repository.AccountRepository;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class FileAccountRepository implements AccountRepository {

    private static FileAccountRepository instance;
    private static final String ACCOUNTS_FILE_PATH = "./resources/accounts.csv";
    private static final Logger log = LoggerFactory.getInstance(FileAccountRepository.class);

    private AtomicLong accountIdCounter;

    private FileAccountRepository() {
        try (FileReader fileReader = new FileReader(ACCOUNTS_FILE_PATH);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            OptionalLong maxAccountId = bufferedReader.lines()
                    .map(line -> line.split(",")[0])
                    .mapToLong(Long::parseLong)
                    .max();

            if (maxAccountId.isPresent()) {
                accountIdCounter = new AtomicLong(maxAccountId.getAsLong());
                log.debug("Created UserIdCounter with initial value: %d".formatted(accountIdCounter.get()));
            } else {
                accountIdCounter = new AtomicLong();
                log.debug("Created empty accountIdCounter started with 0");
            }

        } catch (IOException e) {
            log.error("Error during initializing accountIdCounter." + e.getMessage());
        }
    }

    public static AccountRepository getInstance() {
        if (instance == null) {
            instance = new FileAccountRepository();
        }
        return instance;
    }


    @Override
    public List<Account> findAllByUserId(Long userId) {
        try (FileReader fileReader = new FileReader(ACCOUNTS_FILE_PATH);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            return bufferedReader.lines()
                    .filter(line -> line.split(",")[2].equals(userId.toString()))
                    .map(AccountMapper::toObject)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Can't find users %d accounts".formatted(userId) + ".\n" + e.getMessage());
            return Collections.emptyList();
        }

    }

    @Override
    public Optional<Account> findById(Long accountId) {
        try (FileReader fileReader = new FileReader(ACCOUNTS_FILE_PATH);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            return bufferedReader.lines()
                    .filter(line -> line.split(",")[0].equals(accountId.toString()))
                    .map(AccountMapper::toObject)
                    .findFirst();
        } catch (IOException e) {
            log.error("Can't find account %d".formatted(accountId) + ".\n" + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Account save(Account account) {

        if (account.isNew()) {
            return insert(account);
        } else {
            return update(account);
        }

    }

    private Account insert(Account account) {

        //Получаем новый id.
        //Обогощаем пользователя id
        //Конвертируем пользователя в строку и сохраняем последним

        try (FileWriter fileWriter = new FileWriter(ACCOUNTS_FILE_PATH, true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
             PrintWriter printWriter = new PrintWriter(bufferedWriter)) {

            long newAccountId = accountIdCounter.incrementAndGet();
            log.debug("Generated new accountIdCounter value: %d".formatted(newAccountId));

            account.setId(newAccountId);

            String csvString = AccountMapper.toCsv(account);

            printWriter.println(csvString);
            log.debug("Saved new User \"%s\"".formatted(csvString));

            return account;
        } catch (IOException e) {
            log.error("Error during inserting new account. " + e.getMessage());
            return null;
        }
    }

    private Account update(Account account) {

        //вычитать все строки из файла и сохранить в локальную переменную
        //находим пользователя по id (сохранить номер строки в файле)
        //заменяем старую строку пользователя на новую
        //записываем строки в файл


        try (FileReader reader = new FileReader(ACCOUNTS_FILE_PATH);
             BufferedReader bufferedReader = new BufferedReader(reader)) {

            List<String> lines = bufferedReader.lines().collect(Collectors.toList());

            int updateLineIndex = -1;

            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).split(",")[0].equals(account.getId().toString())) {
                    updateLineIndex = i;
                }
            }

            String csvString = AccountMapper.toCsv(account);

            if (updateLineIndex != -1) {
                lines.remove(updateLineIndex);
                lines.add(updateLineIndex, csvString);
            }

            try (FileWriter writer = new FileWriter(ACCOUNTS_FILE_PATH);
                 BufferedWriter bufferedWriter = new BufferedWriter(writer);
                 PrintWriter printWriter = new PrintWriter(bufferedWriter)) {
                lines.forEach(printWriter::println);
            }
            log.debug("Updated Account \"%s\"".formatted(csvString));

            return account;
        } catch (IOException e) {
            log.error("Error during updating account. " + e.getMessage());
            return null;
        }
    }

}
