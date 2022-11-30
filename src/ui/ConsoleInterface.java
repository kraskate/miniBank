package ui;

import exception.BackToLastMenuException;
import exception.ExitAppException;
import logger.Logger;
import logger.LoggerFactory;
import model.Account;
import model.User;
import service.AccountOperationsLogService;
import service.AccountService;
import service.UserService;
import service.validation.UserValidationRequest;
import service.validation.UserValidationResult;

import java.util.List;
import java.util.Scanner;

public class ConsoleInterface {
    private static Logger log = LoggerFactory.getInstance(ConsoleInterface.class);

    private final Scanner scanner;
    private final Session session = Session.getInstance();
    private final UserService userService = UserService.getInstance();
    private final AccountService accountService = AccountService.getInstance();
    private final AccountOperationsLogService accountOperationsLogService = AccountOperationsLogService.getInstance();


    public ConsoleInterface(Scanner scanner) {
        this.scanner = scanner;
    }

    public void loginLoop() {
        while (true) {
            System.out.println("Добро пожаловать в систему. Войдите или зарегистрируйтесь");
            System.out.println("1 Войти");
            System.out.println("2 Зарегистрироваться");
            System.out.println("0 Завершить работу");

            int loginChoose = scanner.nextInt();
            switch (loginChoose) {
                case 1: {
                    System.out.println("Введите логин");
                    String usernameInput = scanner.next();
                    System.out.println("Введите пароль");
                    String passwordInput = scanner.next();

                    User loginResult = userService.checkLogin(usernameInput, passwordInput);
                    if (loginResult != null) {
                        System.out.printf("Добро пожаловать в систему, %s%n", loginResult.getFirstName());
                        session.createSession(loginResult);
                        log.info("Успешный вход для %s".formatted(loginResult.getUserName()));
                        return;
                    } else {
                        System.out.println("Неверные данные, проверьте и повторите");
                        log.info("Введены неверные данные");
                        break;
                    }
                }
                case 2: {
                    System.out.println("Введите логин");
                    String userName = scanner.next();
                    System.out.println("Введите пароль");
                    String password = scanner.next();
                    System.out.println("Введите имя");
                    String firstName = scanner.next();
                    System.out.println("Введите фамилию");
                    String lastName = scanner.next();
                    System.out.println("Введите дату рождения");
                    String birthDate = scanner.next();
                    System.out.println("Введите пол");
                    String sex = scanner.next();
                    System.out.println("Введите email");
                    String email = scanner.next();

                    UserValidationRequest request = new UserValidationRequest(userName, password, firstName, lastName,
                            birthDate, sex, email);
                    UserValidationResult result = userService.validate(request);

                    if (result.isSuccess()) {
                        User user = userService.create(request);
                        session.createSession(user);
                        System.out.printf("Добро пожаловать в систему, %s \n", user.getUserName());
                        return;
                    } else {
                        System.out.println("Ошибка при создании пользователя. \n" + result.getValidationMessage());
                        continue;
                    }

                }
                case 0: {
                    throw new ExitAppException();
                }
                default: {
                    break;
                }
            }
        }

    }

    public void mainLoop() {
        while (true) {
            System.out.println("1 Операции со счетом");
            System.out.println("2 Работа с историей операций");
            System.out.println("0 Завершить работу");

            int mainChoose = scanner.nextInt();
            switch (mainChoose) {
                case 1: {
                    try {
                        chooseAccountLoop();
                    } catch (BackToLastMenuException e) {
                        break;
                    }
                    break;
                }
                case 2: {
                    try {
                        accountOperationsLogLoop();
                    } catch (BackToLastMenuException e) {
                        break;
                    }
                    break;
                }
                case 0: {
                    throw new ExitAppException();
                }
                default: {
                    System.out.println("Unimplemented");
                    return;
                }
            }
        }

    }

    private void accountOperationsLogLoop() {
        while (true) {
            System.out.println("1 Просмотреть историю операций");
            System.out.println("2 Экспорт истории");
            System.out.println("3 Загрузить операции");
            System.out.println(" Возврат в предыдущее меню");

            int chooseLogOperation = scanner.nextInt();
            switch (chooseLogOperation) {
                case 1: {
                    List<String> operations = accountOperationsLogService.getOperations(session.getUser());
                    operations.forEach(System.out::println);
                    break;
                }
                case 2: {
                    accountOperationsLogService.exportOperations(session.getUser());
                    break;
                }
                case 3: {

                }
                case 0: {
                    throw new BackToLastMenuException();
                }
            }
        }
    }


    private void chooseAccountLoop() {

        while (true) {
            System.out.println("1 Выбрать счет");
            System.out.println("2 Создать счет");
            System.out.println("0 Возврат в предыдущее меню");

            int chooseAccount = scanner.nextInt();
            switch (chooseAccount) {
                case 1: {
                    try {
                        chooseUserAccountLoop();
                    } catch (BackToLastMenuException e) {
                        break;
                    }

                    try {
                        accountOperationsLoop();
                    } catch (BackToLastMenuException e) {
                        break;
                    }

                    accountOperationsLoop();
                    break;
                }
                case 2: {
                    userService.createAccount(session.getUser());
                    System.out.println("Новый счет создан ");
                    break;
                }
                case 0: {
                    throw new BackToLastMenuException();
                }

            }


        }

    }

    private void chooseUserAccountLoop() {
        //TODO Добавить проверку на пустой список счетов

        session.createSession(userService.getUser(session.getUser().getId()));

        List<Account> accounts = session.getUser().getAccounts();

        while (true) {
            for (int i = 0; i < accounts.size(); i++) {
                System.out.println((i + 1) + "Счет номер " + accounts.get(i).getId());
            }
            System.out.println("0 Возврат в предыдущее меню");

            int chooseUserAccount = scanner.nextInt();
            if (chooseUserAccount > 0 && chooseUserAccount <= accounts.size()) {
                session.addAccount(accounts.get(chooseUserAccount - 1));
                System.out.println("Выбран счет номер " + accounts.get(chooseUserAccount - 1).getId());
            } else if (chooseUserAccount == 0) {
                throw new BackToLastMenuException();
            } else {
                System.out.println("Неверный ввод");
            }
        }
    }

    private void accountOperationsLoop() {
        while (true) {
            System.out.println("1 Проверить баланс");
            System.out.println("2 Снять наличные");
            System.out.println("3 Пополнить счет");
            System.out.println("4 Перевод по номеру счета");
            System.out.println("0 Возврат в предыдущее меню");

            int accountOperationChoose = scanner.nextInt();
            switch (accountOperationChoose) {
                case 1: {
                    System.out.println("Баланс: " + session.getAccount().getAmount());
                    break;
                }
                case 2: {
                    System.out.println("Введите сумму для снятия");
                    int withdrawAmount = scanner.nextInt();
                    boolean isSuccessfulWithdraw = accountService.withdraw(session.getAccount(), withdrawAmount);
                    if (isSuccessfulWithdraw) {
                        System.out.println("Возьмите деньги " + withdrawAmount);
                    } else {
                        System.out.println("Недостаточно средств");
                    }
                    break;
                }
                case 3: {
                    System.out.println("Введите сумму для пополнения");
                    int addAmount = scanner.nextInt();
                    boolean result = accountService.addSomeMoney(session.getAccount(), addAmount);
                    break;
                }
                case 4: {
                    System.out.println("Введите сумму перевода");
                    int transferAmount = scanner.nextInt();
                    System.out.println("Введите целевой номер счета");
                    long toAccountNumber = scanner.nextLong();
                    boolean isTargetAccountExists = accountService.exists(toAccountNumber);

                    if (isTargetAccountExists) {
                        boolean transferSuccessful = accountService.transfer(session.getAccount(), toAccountNumber, transferAmount);
                        if (isTargetAccountExists) {
                            System.out.println("Перевод выполнен успешно");
                        } else {
                            System.out.println("Перевод невозможен");
                        }
                    } else {
                        System.out.println("Целевой счет не существует");
                    }
                    break;
                }
                case 0: {
                    throw new BackToLastMenuException();
                }
            }
        }
    }
}
