package shop.controller.user;

import shop.lib.Injector;
import shop.model.Product;
import shop.service.ShoppingCartService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

public class GetShoppingCartController extends HttpServlet {
    private static final String USER_ID = "user_Id";
    private static final Injector injector = Injector.getInstance("com.internet.shop");
    private ShoppingCartService cartService = (ShoppingCartService) injector
            .getInstance(ShoppingCartService.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<Product> products = null;
        Long userId = (Long) req.getSession().getAttribute(USER_ID);
        try {
            products = cartService.getByUserId(userId).get().getProducts();
        } catch (NoSuchElementException e) {
            req.setAttribute("message", "Shopping Cart is empty!!!");
            req.getRequestDispatcher("/WEB-INF/views/user/userMainPage.jsp").forward(req, resp);
            return;
        }
        req.setAttribute("products", products);
        req.getRequestDispatcher("/WEB-INF/views/shoppingCart/shoppingCart.jsp").forward(req, resp);
    }
}
