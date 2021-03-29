package shop.dao.jdbc;

import shop.dao.ProductDao;
import shop.exceptions.DataProcessingException;
import shop.lib.Dao;
import shop.model.Product;
import shop.util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Dao
public class ProductDaoJdbcImpl implements ProductDao {

    @Override
    public Product create(Product product) {
        String query = "INSERT INTO products (name, price) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection
                        .prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            setValues(preparedStatement, product);
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                product.setId(generatedKeys.getLong(1));
            }
        } catch (SQLException ex) {
            throw new DataProcessingException("Can't insert product", ex);
        }
        return product;
    }

    @Override
    public Product update(Product product) {
        String query = "UPDATE products SET name =?,price=? "
                + "WHERE product_id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement preparedStatement = connection
                        .prepareStatement(query)) {
            setValues(preparedStatement, product);
            preparedStatement.setLong(3, product.getId());
            if (preparedStatement.executeUpdate() > 0) {
                return product;
            }
            throw new DataProcessingException("Can't update product with id: " + product.getId());
        } catch (SQLException ex) {
            throw new DataProcessingException("Can't update product with id: "
                    + product.getId(), ex);
        }
    }

    @Override
    public Optional<Product> get(Long id) {
        String query = "SELECT * FROM products WHERE product_id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection
                        .prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(conversionToProduct(resultSet));
            }
        } catch (SQLException ex) {
            throw new DataProcessingException("Can't get product with id: " + id, ex);
        }
        return Optional.empty();
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE products SET is_deleted = TRUE WHERE product_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection
                        .prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DataProcessingException("Can't delete product with id: " + id, ex);
        }
    }

    @Override
    public List<Product> getAll() {
        String query = "SELECT * FROM products WHERE is_deleted = FALSE";
        List<Product> products = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection
                        .prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                products.add(conversionToProduct(resultSet));
            }
            return products;
        } catch (SQLException ex) {
            throw new DataProcessingException("Can't get products from DB", ex);
        }
    }

    private void setValues(PreparedStatement ps, Product product) throws SQLException {
        ps.setString(1, product.getName());
        ps.setDouble(2, product.getPrice());
    }

    private Product conversionToProduct(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("product_id");
        String name = resultSet.getString("name");
        double price = resultSet.getDouble("price");
        return new Product(id, name, price);
    }
}
