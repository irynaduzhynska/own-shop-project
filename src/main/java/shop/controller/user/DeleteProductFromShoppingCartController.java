package shop.controller.user;

import shop.lib.Injector;
import shop.model.Product;
import shop.model.ShoppingCart;
import shop.service.ProductService;
import shop.service.ShoppingCartService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DeleteProductFromShoppingCartController extends HttpServlet {
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
        ShoppingCart shoppingCart = cartService.getByUserId(userId).get();
        long productId = Long.parseLong(req.getParameter("id"));
        Product product = productService.get(productId);
        cartService.deleteProduct(shoppingCart, product);
        resp.sendRedirect(req.getContextPath() + "/shoppingCart/products");
    }
}
