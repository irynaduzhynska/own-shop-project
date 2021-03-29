package shop.controller.user;

import shop.lib.Injector;
import shop.model.Role;
import shop.model.ShoppingCart;
import shop.model.User;
import shop.service.ShoppingCartService;
import shop.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Set;

public class UserRegistrationController extends HttpServlet {
    private static final String USER_ID = "user_Id";
    private static final Injector injector = Injector.getInstance("com.internet.shop");
    private UserService userService = (UserService) injector.getInstance(UserService.class);
    private ShoppingCartService cartService = (ShoppingCartService) injector
            .getInstance(ShoppingCartService.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/user/registration.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String name = req.getParameter("name");
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        if (password.equals(req.getParameter("repeatPwd"))) {
            User user = new User(name, login, password);
            user.setRoles(Set.of(Role.of("USER")));
            User createdUser = userService.create(user);
            cartService.create(new ShoppingCart(createdUser.getId()));
            HttpSession session = req.getSession();
            session.setAttribute(USER_ID, createdUser.getId());
            resp.sendRedirect(req.getContextPath() + "/");
        } else {
            req.setAttribute("message", "Your password and repeat "
                    + "password are not the same!");
            req.setAttribute("currentLogin", login);
            req.setAttribute("currentName", name);
            req.getRequestDispatcher("/WEB-INF/views/user/registration.jsp").forward(req, resp);
        }
    }
}
