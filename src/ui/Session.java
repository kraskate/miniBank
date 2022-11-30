package ui;

import logger.Logger;
import logger.LoggerFactory;
import model.Account;
import model.User;


public class Session {

    private User user;
    private Account account;

    private static Session instance;
    private static final Logger log = LoggerFactory.getInstance(Session.class);

    private Session() {}

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public void createSession(User user) {
        log.info("Создана сессия с %s".formatted(user.getUserName()));
        this.user = user;
    }

    public void addAccount(Account account) {
        this.account = account;
    }

    public User getUser() {
        return this.user;
    }

    public Account getAccount() {
        return account;
    }
}


