package shop.controller.user;

import shop.exceptions.AuthenticationException;
import shop.lib.Injector;
import shop.model.User;
import shop.security.AuthenticationService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginController extends HttpServlet {
    private static final String USER_ID = "user_Id";
    private static final Injector injector = Injector.getInstance("com.internet.shop");
    private AuthenticationService authenticationService = (AuthenticationService) injector
            .getInstance(AuthenticationService.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("WEB-INF/views/user/userLogin.jsp")
                .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String login = req.getParameter("login");
        String pwd = req.getParameter("pwd");
        try {
            User user = authenticationService.login(login, pwd);
            HttpSession session = req.getSession();
            session.setAttribute(USER_ID, user.getId());
        } catch (AuthenticationException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.getRequestDispatcher("WEB-INF/views/user/userLogin.jsp")
                    .forward(req, resp);
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/");
    }
}
