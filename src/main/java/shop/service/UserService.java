package shop.service;

import shop.model.User;

import java.util.Optional;

public interface UserService extends GenericService<User, Long> {
    Optional<User> findByLogin(String login);
}
