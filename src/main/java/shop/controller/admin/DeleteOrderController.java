package shop.controller.admin;

import shop.lib.Injector;
import shop.service.OrderService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DeleteOrderController extends HttpServlet {
    private static final Injector injector = Injector.getInstance("com.internet.shop");
    private OrderService orderService = (OrderService) injector.getInstance(OrderService.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long orderId = Long.parseLong(req.getParameter("id"));
        orderService.deleteById(orderId);
        resp.sendRedirect(req.getContextPath() + "/admin/orders");
    }
}
