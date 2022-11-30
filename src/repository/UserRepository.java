package repository;

import model.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findById (Long UserId);
    Optional<User> findByUsername(String username);
    User save(User user);
}
