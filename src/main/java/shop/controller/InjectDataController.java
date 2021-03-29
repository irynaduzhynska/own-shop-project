package shop.controller;

import shop.lib.Injector;
import shop.model.Product;
import shop.model.Role;
import shop.model.ShoppingCart;
import shop.model.User;
import shop.service.ProductService;
import shop.service.ShoppingCartService;
import shop.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

public class InjectDataController extends HttpServlet {
    private static final Injector injector = Injector.getInstance("com.internet.shop");
    private UserService userService = (UserService) injector.getInstance(UserService.class);
    private ProductService productService = (ProductService) injector
            .getInstance(ProductService.class);
    private ShoppingCartService cartService = (ShoppingCartService) injector
            .getInstance(ShoppingCartService.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User roman = new User("Roma", "roman", "pwdR");
        roman.setRoles(Set.of(Role.of("USER")));
        User cr2 = userService.create(roman);
        cartService.create(new ShoppingCart(cr2.getId()));

        User alice = new User("Alice", "alice", "pwdA");
        alice.setRoles(Set.of(Role.of("USER")));
        User cr1 = userService.create(alice);
        cartService.create(new ShoppingCart(cr1.getId()));

        User admin = new User("Administrator", "admin", "1");
        admin.setRoles(Set.of(Role.of("ADMIN")));
        User cr3 = userService.create(admin);
        cartService.create(new ShoppingCart(cr3.getId()));

        Product iphoneXR = new Product("iphoneXR", 21000);
        productService.create(iphoneXR);
        Product macBookPro = new Product("macBookPro", 46000);
        productService.create(macBookPro);
        Product laptopAcer = new Product("AcerAspire5", 22000);
        productService.create(laptopAcer);

        req.getRequestDispatcher("/WEB-INF/views/injectData.jsp").forward(req, resp);
    }
}
