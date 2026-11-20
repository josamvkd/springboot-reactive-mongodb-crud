package com.josamtechie.reactive.util;

import com.josamtechie.reactive.dto.ProductDto;
import com.josamtechie.reactive.model.Product;
import org.springframework.beans.BeanUtils;

public class AppUtils
{
    public static ProductDto entityToDto(Product product)
    {
        ProductDto productDto = new ProductDto();
        // copyProperties works only when both entity and dto having same attributes
        BeanUtils.copyProperties(product, productDto);
        return productDto;
    }

    public static Product dtoToEntity(ProductDto productDto)
    {
        Product product = new Product();
        BeanUtils.copyProperties(productDto, product);
        return product;
    }
}
