package com.transcendenttopicals.productService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class ProductServiceTests {

    @Mock
    private DataSource dataSource;

    @InjectMocks
    private ProductService productService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetProduct() throws Exception {
        // Mock the Connection and PreparedStatement
        Connection connectionMock = mock(Connection.class);
        PreparedStatement statementMock = mock(PreparedStatement.class);
        ResultSet resultSetMock = mock(ResultSet.class);

        // Configure the ResultSet to return the existing product
        when(resultSetMock.next()).thenReturn(true);
        when(resultSetMock.getString("productId")).thenReturn("123");
        when(resultSetMock.getString("productName")).thenReturn("Test Product");
        when(resultSetMock.getString("description")).thenReturn("Test Description");
        when(resultSetMock.getBigDecimal("price")).thenReturn(BigDecimal.valueOf(10.0));
        when(resultSetMock.getInt("inventory")).thenReturn(5);

        // Configure the PreparedStatement to return the ResultSet
        when(statementMock.executeQuery()).thenReturn(resultSetMock);

        // Configure the Connection to return the PreparedStatement
        when(connectionMock.prepareStatement(anyString())).thenReturn(statementMock);

        // Configure the DataSource to return the Connection
        when(dataSource.getConnection()).thenReturn(connectionMock);

        // Create the ProductService
        ProductService productService = new ProductService();

        // Set the dataSource field using ReflectionTestUtils
        ReflectionTestUtils.setField(productService, "dataSource", dataSource);

        // Test the existing product scenario
        Product existingProduct = productService.getProduct("123");
        assertNotNull(existingProduct);
        assertEquals("123", existingProduct.getProductId());
        assertEquals("Test Product", existingProduct.getProductName());
        assertEquals("Test Description", existingProduct.getDescription());
        assertEquals(new BigDecimal("0.10"), existingProduct.getPrice());
        assertEquals(5, existingProduct.getInventory());
    }



    @Test
    public void testAddProduct() throws Exception {
        // Prepare mock data
        Product product = new Product("123", "Test Product", "Test Description", BigDecimal.valueOf(9.99), 10);

        // Mock the Connection and PreparedStatement
        Connection connectionMock = mock(Connection.class);
        PreparedStatement statementMock = mock(PreparedStatement.class);
        when(dataSource.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(statementMock);

        // Perform the addProduct operation
        productService.addProduct(product);

        // Verify the interactions
        verify(statementMock).setString(1, "123");
        verify(statementMock).setString(2, "Test Product");
        verify(statementMock).setString(3, "Test Description");
        verify(statementMock).setInt(4, 999);
        verify(statementMock).setInt(5, 10);
        verify(statementMock).executeUpdate();
    }

    @Test
    public void testUpdateProduct() throws Exception {
        // Prepare mock data
        Product product = new Product("123", "Updated Product", "Updated Description", BigDecimal.valueOf(19.99), 20);

        // Mock the Connection and PreparedStatement
        Connection connectionMock = mock(Connection.class);
        PreparedStatement statementMock = mock(PreparedStatement.class);
        when(dataSource.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(statementMock);

        // Perform the updateProduct operation
        productService.updateProduct(product);

        // Verify the interactions
        verify(statementMock).setString(1, "Updated Product");
        verify(statementMock).setString(2, "Updated Description");
        verify(statementMock).setInt(3, 1999);
        verify(statementMock).setInt(4, 20);
        verify(statementMock).setString(5, "123");
        verify(statementMock).executeUpdate();
    }

    @Test
    public void testGetProducts() throws Exception {
        // Prepare mock data
        ResultSet resultSetMock = mock(ResultSet.class);
        when(resultSetMock.next()).thenReturn(true).thenReturn(false);
        when(resultSetMock.getString("productId")).thenReturn("123");
        when(resultSetMock.getString("productName")).thenReturn("Test Product");
        when(resultSetMock.getString("description")).thenReturn("Test Description");
        when(resultSetMock.getBigDecimal("price")).thenReturn(BigDecimal.valueOf(9.99));
        when(resultSetMock.getInt("inventory")).thenReturn(10);

        // Mock the Connection and PreparedStatement
        Connection connectionMock = mock(Connection.class);
        PreparedStatement statementMock = mock(PreparedStatement.class);
        when(dataSource.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(statementMock);
        when(statementMock.executeQuery()).thenReturn(resultSetMock);

        // Perform the getProducts operation
        List<Product> products = productService.getProducts();

        // Verify the interactions and assertions
        verify(statementMock).executeQuery();

        assertEquals(1, products.size());
        Product product = products.get(0);
        assertEquals("123", product.getProductId());
        assertEquals("Test Product", product.getProductName());
        assertEquals("Test Description", product.getDescription());
        assertEquals(new BigDecimal("0.10"), product.getPrice());
        assertEquals(10, product.getInventory());
    }

    @Test
    public void testRemoveProduct() throws Exception {
        // Mock the Connection and PreparedStatement
        Connection connectionMock = mock(Connection.class);
        PreparedStatement statementMock = mock(PreparedStatement.class);
        when(dataSource.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(statementMock);
        when(statementMock.executeUpdate()).thenReturn(1);

        // Perform the removeProduct operation
        productService.removeProduct("123");

        // Verify the interactions
        verify(statementMock).setString(1, "123");
        verify(statementMock).executeUpdate();
    }
    
   








}
