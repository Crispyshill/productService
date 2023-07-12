package com.transcendenttopicals.productService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.test.web.servlet.result.MockMvcResultHandlers; // Import the MockMvcResultHandlers class

@RunWith(SpringRunner.class)
@WebMvcTest(ProductController.class)
@TestPropertySource(properties = "server.port=8001")
public class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ProductController productController;
    
    @Test
    public void testAddDefaultProductAndGetProduct() throws Exception {
        // Mock the getProduct method to return a product with the specified details
        Product mockProduct = new Product();
        mockProduct.setProductId("420");
        mockProduct.setProductName("WEED");
        mockProduct.setDescription("The Good Stuff");
        mockProduct.setPrice(BigDecimal.valueOf(4.20));
        mockProduct.setInventory(69);
        Mockito.when(productService.getProduct("420")).thenReturn(mockProduct);

        // Perform the request to add the default product
        mockMvc.perform(MockMvcRequestBuilders.post("/addDefaultProduct")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Retrieve the product using the getProduct endpoint
        mockMvc.perform(MockMvcRequestBuilders.get("/getProduct/{productId}", "420"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.productId").value("420"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.productName").value("WEED"));
    }
    
    @Test
    public void testGetProducts() throws Exception {
        List<Product> mockProducts = new ArrayList<>();
        mockProducts.add(new Product("420", "WEED", "The Good Stuff", BigDecimal.valueOf(4.20), 69));
        Mockito.when(productService.getProducts()).thenReturn(mockProducts);

        mockMvc.perform(MockMvcRequestBuilders.get("/getProducts"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].productId").value("420"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].productName").value("WEED"));
    }
    
    @Test
    public void testGetProductsTemplate() throws Exception {
        // Mock products
        List<Product> mockProducts = new ArrayList<>();
        mockProducts.add(new Product("420", "WEED", "The Good Stuff", BigDecimal.valueOf(4.20), 69));
        Mockito.when(productService.getProducts()).thenReturn(mockProducts);

        // Perform GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/products"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("products"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("products"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("inlineTemplate"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("baseUrl"));
    }
    @Test
    public void testRemoveProduct() throws Exception {
        String productId = "123";

        mockMvc.perform(MockMvcRequestBuilders.delete("/removeProduct/{productId}", productId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Product removed"));

        Mockito.verify(productService, Mockito.times(1)).removeProduct(productId);
    }
    
    @Test
    public void testAddProduct() throws Exception {
        Product product = new Product();
        product.setProductId("123");
        product.setProductName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(BigDecimal.valueOf(9.99));
        product.setInventory(10);

        MockMultipartFile image = new MockMultipartFile("productImage", "image.jpg", "image/jpeg", "Test Image".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/addProduct")
                .file(image)
                .flashAttr("product", product))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Product add attempted!"));

        Mockito.verify(productService, Mockito.times(1)).addProduct(product);
    }




    
    @Test
    public void testUpdateProduct() throws Exception {
        Product updatedProduct = new Product("420", "Updated Weed", "Even Better Stuff", BigDecimal.valueOf(9.99), 100);

        mockMvc.perform(MockMvcRequestBuilders.put("/updateProduct")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updatedProduct)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Product update attempt"));

        Mockito.verify(productService, Mockito.times(1)).updateProduct(updatedProduct);
    }

    private String asJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }
}
