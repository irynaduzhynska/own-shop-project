package shop.controller.user;

import shop.lib.Injector;
import shop.model.Order;
import shop.service.OrderService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class GetAllOrdersForUserController extends HttpServlet {
    private static final String USER_ID = "user_Id";
    private static final Injector injector = Injector.getInstance("com.internet.shop");
    private OrderService orderService = (OrderService) injector.getInstance(OrderService.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Long userId = (Long) req.getSession().getAttribute(USER_ID);
        List<Order> allUserOrders = orderService.getAllUserOrders(userId);
        req.setAttribute("orders", allUserOrders);
        req.getRequestDispatcher("/WEB-INF/views/order/allUserOrders.jsp").forward(req, resp);
    }
}
