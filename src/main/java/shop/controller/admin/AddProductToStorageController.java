package shop.controller.admin;

import shop.lib.Injector;
import shop.model.Product;
import shop.service.ProductService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AddProductToStorageController extends HttpServlet {
    private static final Injector injector = Injector.getInstance("com.internet.shop");
    private ProductService productService = (ProductService) injector
            .getInstance(ProductService.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/product/addProduct.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String name = req.getParameter("name");
        double price = Double.parseDouble(req.getParameter("price"));
        productService.create(new Product(name, price));
        req.setAttribute("message", "Product added successfully!");
        req.getRequestDispatcher("/WEB-INF/views/product/addProduct.jsp").forward(req, resp);
    }
}
