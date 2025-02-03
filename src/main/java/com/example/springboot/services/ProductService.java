package com.example.springboot.services;

import com.example.springboot.dtos.ProductRecordDto;
import com.example.springboot.exceptions.ProductNotFoundException;
import com.example.springboot.models.ProductModel;
import com.example.springboot.repositories.ProductRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public ProductModel saveProduct(ProductRecordDto productDto){
        var productModel = new ProductModel();
        BeanUtils.copyProperties(productDto, productModel);
        return productRepository.save(productModel);
    }

    public Page<ProductModel> getAllProducts(int page, int size){
        return productRepository.findAll(PageRequest.of(page, size));
    }

    public ProductModel getOneProduct(UUID id){
        return findProductById(id);
    }

    public ProductModel updateProduct(UUID id, ProductRecordDto productDto){
        ProductModel productModel = findProductById(id);

        BeanUtils.copyProperties(productDto, productModel);
        return productRepository.save(productModel);
    }

    public void deleteProduct(UUID id){
        ProductModel productModel = findProductById(id);

        productRepository.delete(productModel);
    }

    private ProductModel findProductById(UUID id){
        return productRepository.findById(id).orElseThrow(ProductNotFoundException::new);
    }
}
