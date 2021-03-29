package shop.service;

import shop.model.Product;
import shop.model.ShoppingCart;

import java.util.Optional;

public interface ShoppingCartService extends GenericService<ShoppingCart, Long> {
    ShoppingCart addProduct(ShoppingCart shoppingCart, Product product);

    boolean deleteProduct(ShoppingCart shoppingCart, Product product);

    void clear(ShoppingCart shoppingCart);

    Optional<ShoppingCart> getByUserId(Long userId);
}
