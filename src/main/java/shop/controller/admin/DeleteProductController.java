package shop.controller.admin;

import shop.lib.Injector;
import shop.service.ProductService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DeleteProductController extends HttpServlet {
    private static final Injector injector = Injector.getInstance("com.internet.shop");
    private ProductService productService = (ProductService) injector
            .getInstance(ProductService.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long productId = Long.parseLong(req.getParameter("id"));
        productService.deleteById(productId);
        resp.sendRedirect(req.getContextPath() + "/admin/products");
    }
}
