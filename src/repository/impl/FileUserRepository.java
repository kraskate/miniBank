package repository.impl;

import logger.Logger;
import logger.LoggerFactory;
import mapper.UserMapper;
import model.User;
import repository.UserRepository;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.atomic.AtomicLong;

public class FileUserRepository implements UserRepository {

    private static FileUserRepository instance;
    private static final String USERS_FILE_PATH = "./resources/users.csv";
    private static final Logger log = LoggerFactory.getInstance(FileUserRepository.class);

    private AtomicLong userIdCcounter;

    private FileUserRepository() {

        try (FileReader fileReader = new FileReader(USERS_FILE_PATH);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            OptionalLong maxUserId = bufferedReader.lines()
                    .map(line -> line.split(",")[0])
                    .mapToLong(Long::parseLong)
                    .max();

            if (maxUserId.isPresent()) {
                userIdCcounter = new AtomicLong(maxUserId.getAsLong());
                log.debug("Created UserIdCounter with initial value: %d".formatted(userIdCcounter.get()));
            } else {
                userIdCcounter = new AtomicLong();
                log.debug("Created empty UserIdCounter started with 0");
            }

        } catch (IOException e) {
            log.error("Error during initializing User Id Counter." + e.getMessage());
        }

    }

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new FileUserRepository();
        }
        return instance;
    }

    @Override
    public Optional<User> findByUsername(String username) {

        if (username == null || username.isEmpty()) {
            return Optional.empty();
        }

        try (FileReader fileReader = new FileReader(USERS_FILE_PATH);
             BufferedReader br = new BufferedReader(fileReader)) {

            br.lines()
                    .filter(line -> username.equals(line.split(",")[1]))
                    .map(UserMapper::toObject)
                    .findFirst();

        } catch (IOException e) {
            log.error("Error in time looking for user with username = " + username);
        }

        return Optional.empty();
    }

    @Override
    public User save(User user) {//homework

        if (user.isNew()) {
            return insert(user);
        } else {
            return update(user);
        }

    }

    private User insert(User user) {

        //Получаем новый id.
        //Обогощаем пользователя id
        //Конвертируем пользователя в строку и сохраняем последним
    }

    private User update(User user) {

        //вычитать все строки из файла и сохранить в локальную переменную
        //находим пользователя по id (сохранить номер строки в файле)
        //заменяем старую строку пользователя на новую
        //записываем строки в файл
    }
}
