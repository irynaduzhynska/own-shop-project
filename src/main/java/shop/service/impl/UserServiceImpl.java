package shop.service.impl;

import shop.dao.UserDao;
import shop.lib.Inject;
import shop.lib.Service;
import shop.model.User;
import shop.service.UserService;
import shop.util.HashUtil;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Inject
    private UserDao userDao;

    @Override
    public User create(User user) {
        user.setSalt(HashUtil.getSalt());
        String hashedPwd = HashUtil.hashPassword(user.getPassword(), user.getSalt());
        user.setPassword(hashedPwd);
        return userDao.create(user);
    }

    @Override
    public User get(Long id) {
        return userDao.get(id).get();
    }

    @Override
    public List<User> getAll() {
        return userDao.getAll();
    }

    @Override
    public User update(User user) {
        return userDao.update(user);
    }

    @Override
    public boolean deleteById(Long id) {
        return userDao.delete(id);
    }

    @Override
    public boolean delete(User item) {
        return userDao.delete(item.getId());
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return userDao.findByLogin(login);
    }
}
