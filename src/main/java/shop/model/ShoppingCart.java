package shop.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShoppingCart {
    private Long id;
    private List<Product> products;
    private Long userId;

    public ShoppingCart(Long userId) {
        this.products = new ArrayList<>();
        this.userId = userId;
    }

    public ShoppingCart(Long id, Long userId) {
        this.products = new ArrayList<>();
        this.id = id;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShoppingCart)) {
            return false;
        }
        ShoppingCart that = (ShoppingCart) o;
        return Objects.equals(id, that.id)
                && Objects.equals(products, that.products)
                && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, products, userId);
    }

    @Override
    public String toString() {
        return "ShoppingCart {"
                + "id =" + id
                + ", products =" + products.toString()
                + ", userId =" + userId + '}';
    }
}
