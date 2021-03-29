package shop.web.filters;

import shop.lib.Injector;
import shop.service.UserService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

public class AuthenticationFilter implements Filter {
    private static final String USER_ID = "user_Id";
    private static final Injector injector = Injector.getInstance("com.internet.shop");
    private Set<String> availableUrls = new HashSet<>();
    private UserService userService = (UserService) injector
            .getInstance(UserService.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        availableUrls.add("/login");
        availableUrls.add("/registration");
        availableUrls.add("/");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        String url = req.getServletPath();
        if (availableUrls.contains(url)) {
            filterChain.doFilter(req, resp);
            return;
        }
        Long userId = (Long) req.getSession().getAttribute(USER_ID);
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        try {
            userService.get(userId);
        } catch (NoSuchElementException e) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        filterChain.doFilter(req, resp);
    }

    @Override
    public void destroy() {
    }
}
