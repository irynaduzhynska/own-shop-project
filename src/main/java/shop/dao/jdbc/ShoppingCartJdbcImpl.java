package shop.dao.jdbc;

import shop.dao.ShoppingCartDao;
import shop.exceptions.DataProcessingException;
import shop.lib.Dao;
import shop.model.Product;
import shop.model.ShoppingCart;
import shop.util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Dao
public class ShoppingCartJdbcImpl implements ShoppingCartDao {

    @Override
    public Optional<ShoppingCart> getByUserId(Long userId) {
        String selectQuery =
                "SELECT * FROM shopping_carts WHERE user_id = ? AND is_deleted = FALSE ";
        ShoppingCart cart = null;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(selectQuery)) {
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                cart = new ShoppingCart(resultSet.getLong("cart_id"),
                        resultSet.getLong("user_id"));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get shopping_cart with userId: " + userId, e);
        }
        if (cart != null) {
            cart.setProducts(getShoppingCartProducts(cart.getId()));
        }
        return Optional.ofNullable(cart);
    }

    @Override
    public ShoppingCart create(ShoppingCart shoppingCart) {
        try (Connection connection = ConnectionUtil.getConnection(); PreparedStatement statement =
                connection.prepareStatement("INSERT INTO shopping_carts (user_id) VALUES(?)",
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, shoppingCart.getUserId());
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                shoppingCart.setId(generatedKeys.getLong(1));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create ShoppingCart with id: "
                    + shoppingCart.getId(), e);
        }
        for (Product product : shoppingCart.getProducts()) {
            addDataToShoppingCartProductsTable(shoppingCart.getId(), product.getId());
        }
        return shoppingCart;
    }

    @Override
    public ShoppingCart update(ShoppingCart cart) {
        String deleteQuery = "DELETE FROM shopping_cart_products WHERE cart_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
            statement.setLong(1, cart.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update ShoppingCart with id: "
                    + cart.getId(), e);
        }
        return addProductsToShoppingCart(cart);
    }

    @Override
    public Optional<ShoppingCart> get(Long id) {
        String selectQuery =
                "SELECT * FROM shopping_carts WHERE cart_id = ? AND is_deleted = FALSE";
        List<Product> products = getShoppingCartProducts(id);
        ShoppingCart cart = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(selectQuery)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                cart = new ShoppingCart(resultSet.getLong("cart_id"),
                        resultSet.getLong("user_id"));
                cart.setProducts(products);
            }
            return Optional.ofNullable(cart);
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get shopping_cart with id: " + id, e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String updateQuery = "UPDATE shopping_carts SET is_deleted = TRUE WHERE cart_id = ?";
        try (Connection connection = ConnectionUtil.getConnection(); PreparedStatement statement =
                connection.prepareStatement(updateQuery)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't add products to shoppingCart", e);
        }
    }

    @Override
    public List<ShoppingCart> getAll() {
        String selectQuery = "SELECT * FROM shopping_carts WHERE is_deleted = FALSE";
        List<ShoppingCart> carts = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(selectQuery)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                ShoppingCart cart = new ShoppingCart(resultSet.getLong("cart_id"),
                        resultSet.getLong("user_id"));
                carts.add(cart);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get shopping_carts", e);
        }
        for (ShoppingCart cart : carts) {
            cart.setProducts(getShoppingCartProducts(cart.getId()));
        }
        return carts;
    }

    private void addDataToShoppingCartProductsTable(long cartId, long productId) {
        String insertQuery = "INSERT INTO shopping_cart_products (cart_id, product_id) VALUES(?,?)";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection
                         .prepareStatement(insertQuery)) {
            statement.setLong(1, cartId);
            statement.setLong(2, productId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't add data to "
                    + "shopping_cart_products table", e);
        }
    }

    private List<Product> getShoppingCartProducts(long cartId) {
        String selectQuery = "SELECT * FROM products p JOIN shopping_cart_products scp "
                + "ON scp.product_id = p.product_id WHERE cart_id = ? AND is_deleted = FALSE";
        List<Product> products = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(selectQuery)) {
            statement.setLong(1, cartId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                products.add(new Product(resultSet.getLong("product_id"),
                        resultSet.getString("name"),
                        resultSet.getDouble("price")));
            }
            return products;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get ShoppingCart Products!!!", e);
        }
    }

    private ShoppingCart addProductsToShoppingCart(ShoppingCart cart) {
        String insertQuery = "INSERT INTO shopping_cart_products (cart_id, product_id) VALUES(?,?)";
        try (Connection connection = ConnectionUtil.getConnection(); PreparedStatement statement =
                connection.prepareStatement(insertQuery)) {
            for (Product product : cart.getProducts()) {
                statement.setLong(1, cart.getId());
                statement.setLong(2, product.getId());
                statement.executeUpdate();
            }
            return cart;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't add products to shoppingCart", e);
        }
    }
}
