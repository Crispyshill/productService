package com.transcendenttopicals.productService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class ProductService {
    @Autowired
    private DataSource dataSource;

    private final TemplateEngine templateEngine = new TemplateEngine();

    public void addDefaultProduct() {
        addProduct(new Product("420", "WEED", "The good stuff", BigDecimal.valueOf(4.20), 69));
    }

    public Product getProduct(String productId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM product WHERE productId = ?")) {
            statement.setString(1, productId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String id = resultSet.getString("productId");
                String name = resultSet.getString("productName");
                String description = resultSet.getString("description");
                BigDecimal price = resultSet.getBigDecimal("price").divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
                int inventory = resultSet.getInt("inventory");

                return new Product(id, name, description, price, inventory);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void addProduct(Product product) {
        try (Connection connection = dataSource.getConnection()) {
            String query = "INSERT INTO product (productId, productName, description, price, inventory) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, product.getProductId());
                statement.setString(2, product.getProductName());
                statement.setString(3, product.getDescription());
                statement.setInt(4, product.getPrice().multiply(BigDecimal.valueOf(100)).intValue());
                statement.setInt(5, product.getInventory());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateProduct(Product product) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE products SET productName = ?, description = ?,  price = ?, inventory = ? WHERE id = ?")) {
            statement.setString(1, product.getProductName());
            statement.setString(2, product.getDescription());
            statement.setInt(3, product.getPrice().multiply(BigDecimal.valueOf(100)).intValue());
            statement.setInt(4, product.getInventory());
            statement.setString(5, product.getProductId());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Product updated successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Product> getProducts() {
        List<Product> products = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM product")) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String productId = resultSet.getString("productId");
                String productName = resultSet.getString("productName");
                String description = resultSet.getString("description");
                BigDecimal price = resultSet.getBigDecimal("price").divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
                int inventory = resultSet.getInt("inventory");

                Product product = new Product(productId, productName, description, price, inventory);
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }


public void removeProduct(String productId) {
    try (Connection connection = dataSource.getConnection();
         PreparedStatement statement = connection.prepareStatement("DELETE FROM product WHERE productId = ?")) {
        statement.setString(1, productId);
        int rowsDeleted = statement.executeUpdate();
        if (rowsDeleted > 0) {
            System.out.println("Product removed successfully.");
        } else {
            System.out.println("Product not found.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
}
