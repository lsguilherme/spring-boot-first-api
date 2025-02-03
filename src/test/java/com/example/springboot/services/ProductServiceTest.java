package com.example.springboot.services;

import com.example.springboot.dtos.ProductRecordDto;
import com.example.springboot.exceptions.ProductNotFoundException;
import com.example.springboot.models.ProductModel;
import com.example.springboot.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductService productService;

    private UUID productId;
    private ProductModel productModel;

    @BeforeEach
    public void setup() {
        productId = UUID.randomUUID();
        productModel = new ProductModel();

        productModel.setIdProduct(productId);
        productModel.setName("Teste");
        productModel.setValue(BigDecimal.valueOf(123));
    }

    @Test
    public void testeRetornoDeUmProduto() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(productModel));

        ProductModel result = productService.getOneProduct(productId);

        assertNotNull(result);
        assertEquals(productId, result.getIdProduct());
        assertEquals("Teste", result.getName());
        assertEquals( BigDecimal.valueOf(123), result.getValue());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    public void testGetOneProduct_ProductNotFound() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> {
            productService.getOneProduct(productId);
        });
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    public void testGetAllProduct(){
        List<ProductModel> productModels = Arrays.asList(productModel, productModel);

        Page<ProductModel> paginaMock = new PageImpl<>(
                productModels,
                PageRequest.of(0,2),
                productModels.size()
        );

        when(productRepository.findAll(PageRequest.of(0,2))).thenReturn(paginaMock);

        Page<ProductModel> resultado = productService.getAllProducts(0, 2);

        assertNotNull(resultado);
        assertEquals(2, resultado.getContent().size());
        assertEquals("Teste", resultado.getContent().get(0).getName());

        verify(productRepository).findAll(PageRequest.of(0, 2));
    }

    @Test
    public void testSaveProduct() {
        ProductRecordDto productDto = new ProductRecordDto("New Product", BigDecimal.valueOf(200.0));
        when(productRepository.save(any(ProductModel.class))).thenReturn(productModel);

        ProductModel result = productService.saveProduct(productDto);

        assertNotNull(result);
        assertEquals(productId, result.getIdProduct());
        assertEquals("Teste", result.getName());
        assertEquals(BigDecimal.valueOf(123), result.getValue());
        verify(productRepository, times(1)).save(any(ProductModel.class));
    }

    @Test
    public void testUpdateProduct() {
        ProductRecordDto productDto = new ProductRecordDto("Updated Product", BigDecimal.valueOf(300.0));
        when(productRepository.findById(productId)).thenReturn(Optional.of(productModel));
        when(productRepository.save(any(ProductModel.class))).thenReturn(productModel);

        ProductModel result = productService.updateProduct(productId, productDto);

        assertNotNull(result);
        assertEquals(productId, result.getIdProduct());
        assertEquals("Updated Product", result.getName());
        assertEquals(BigDecimal.valueOf(300.0), result.getValue());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(any(ProductModel.class));
    }

    @Test
    public void testDeleteProduct() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(productModel));
        doNothing().when(productRepository).delete(productModel);

        productService.deleteProduct(productId);

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).delete(productModel);
    }
}