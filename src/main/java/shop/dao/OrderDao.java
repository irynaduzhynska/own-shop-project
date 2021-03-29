package shop.dao;

import shop.model.Order;

import java.util.List;

public interface OrderDao extends GenericDao<Order, Long> {
    List<Order> getUsersOrders(Long userId);
}
