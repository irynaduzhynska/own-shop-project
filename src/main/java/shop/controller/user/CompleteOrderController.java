package shop.controller.user;

import shop.lib.Injector;
import shop.model.ShoppingCart;
import shop.service.OrderService;
import shop.service.ShoppingCartService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CompleteOrderController extends HttpServlet {
    private static final String USER_ID = "user_Id";
    private static final Injector injector = Injector.getInstance("com.internet.shop");
    private OrderService orderService = (OrderService) injector.getInstance(OrderService.class);
    private ShoppingCartService cartService = (ShoppingCartService) injector
            .getInstance(ShoppingCartService.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Long userId = (Long) req.getSession().getAttribute(USER_ID);
        ShoppingCart userCart = cartService.getByUserId(userId).get();
        orderService.completeOrder(userCart);
        req.setAttribute("message", "The order is placed, thank you for your purchase");
        req.getRequestDispatcher("/WEB-INF/views/shoppingCart/shoppingCart.jsp").forward(req, resp);
    }
}
