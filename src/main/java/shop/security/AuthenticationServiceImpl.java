package shop.security;

import shop.exceptions.AuthenticationException;
import shop.lib.Inject;
import shop.lib.Service;
import shop.model.User;
import shop.service.UserService;
import shop.util.HashUtil;

import java.util.Optional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Inject
    private UserService userService;

    @Override
    public User login(String login, String password) throws AuthenticationException {
        Optional<User> userOptional = userService.findByLogin(login);
        if (userOptional.isPresent()) {
            User userDB = userOptional.get();
            String hashedPwd = HashUtil.hashPassword(password, userDB.getSalt());
            if (userDB.getPassword().equals(hashedPwd)) {
                return userDB;
            }
        }
        throw new AuthenticationException("Incorrect user name or password!!!");
    }
}
