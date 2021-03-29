package shop.controller.user;

import shop.lib.Injector;
import shop.model.Product;
import shop.service.ProductService;
import shop.service.ShoppingCartService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AddProductToShoppingCartController extends HttpServlet {
    private static final String USER_ID = "user_Id";
    private static final Injector injector = Injector.getInstance("com.internet.shop");
    private ShoppingCartService cartService = (ShoppingCartService) injector
            .getInstance(ShoppingCartService.class);
    private ProductService productService = (ProductService) injector
            .getInstance(ProductService.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Long userId = (Long) req.getSession().getAttribute(USER_ID);
        Long productId = Long.parseLong(req.getParameter("id"));
        Product product = productService.get(productId);
        cartService.addProduct(cartService.getByUserId(userId).get(), product);
        resp.sendRedirect(req.getContextPath() + "/products");
    }
}
