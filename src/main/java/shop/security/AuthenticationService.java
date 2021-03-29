package shop.security;

import shop.exceptions.AuthenticationException;
import shop.model.User;

public interface AuthenticationService {
    User login(String login, String password) throws AuthenticationException;
}
