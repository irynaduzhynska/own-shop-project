package shop.service;

import shop.model.Order;
import shop.model.ShoppingCart;

import java.util.List;

public interface OrderService extends GenericService<Order, Long> {
    Order completeOrder(ShoppingCart shoppingCart);

    List<Order> getAllUserOrders(Long userId);

    double getSumProducts(Long id);
}
