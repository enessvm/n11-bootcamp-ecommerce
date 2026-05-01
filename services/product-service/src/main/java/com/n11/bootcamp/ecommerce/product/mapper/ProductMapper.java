package com.n11.bootcamp.ecommerce.product.mapper;

import com.n11.bootcamp.ecommerce.product.client.dto.StockStatus;
import com.n11.bootcamp.ecommerce.product.dto.*;
import com.n11.bootcamp.ecommerce.product.entity.Category;
import com.n11.bootcamp.ecommerce.product.entity.Product;
import com.n11.bootcamp.ecommerce.product.entity.ProductImage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class ProductMapper {

    public Product toEntity(CreateProductRequest req, Category category) {
        Product p = new Product();
        p.setName(req.name());
        p.setDescription(req.description());
        p.setBrand(req.brand());
        p.setCategory(category);
        p.setListPrice(req.listPrice());
        p.setPrimaryImageUrl(req.primaryImageUrl());
        applyAdditionalImages(p, req.additionalImageUrls());
        return p;
    }

    public void updateEntity(Product p, UpdateProductRequest req, Category category) {
        p.setName(req.name());
        p.setDescription(req.description());
        p.setBrand(req.brand());
        p.setCategory(category);
        p.setListPrice(req.listPrice());
        p.setPrimaryImageUrl(req.primaryImageUrl());
        applyAdditionalImages(p, req.additionalImageUrls());
    }

    public ProductResponse toResponse(Product p) {
        List<CategoryPathEntry> path = walkCategoryPath(p.getCategory());
        List<ImageEntry> images = p.getAdditionalImages().stream()
                .map(img -> new ImageEntry(img.getUrl(), img.getPosition()))
                .toList();


        List<ImageEntry> allImages = buildImageList(p);

        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getBrand(),
                p.getCategory().getId(),
                path,
                allImages,
                p.getListPrice(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }

    public ProductBatchResponse.BatchEntry toBatchEntry(Product p) {
        return new ProductBatchResponse.BatchEntry(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getBrand(),
                p.getCategory().getId(),
                p.getCategory().getName(),
                p.getPrimaryImageUrl(),
                p.getListPrice(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }

    public ProductDetailResponse toDetailResponse(Product p,
                                                  StockStatus stockStatus,
                                                  boolean stockUnavailable) {
        List<CategoryPathEntry> path = walkCategoryPath(p.getCategory());

        List<ImageEntry> allImages = new ArrayList<>();
        allImages.add(new ImageEntry(p.getPrimaryImageUrl(), 0));
        for (int i = 0; i < p.getAdditionalImages().size(); i++) {
            ProductImage img = p.getAdditionalImages().get(i);
            allImages.add(new ImageEntry(img.getUrl(), i + 1));
        }

        return new ProductDetailResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getBrand(),
                p.getCategory().getId(),
                path,
                allImages,
                p.getListPrice(),
                stockStatus,
                p.getCreatedAt(),
                p.getUpdatedAt(),
                stockUnavailable,
                true,
                true
        );
    }

    private void applyAdditionalImages(Product product, List<String> urls) {
        // Keep the existing collection
        product.getAdditionalImages().clear();
        if (urls == null) return;
        for (int i = 0; i < urls.size(); i++) {
            ProductImage img = new ProductImage();
            img.setProduct(product);
            img.setUrl(urls.get(i));
            img.setPosition(i);
            product.getAdditionalImages().add(img);
        }
    }

    private List<CategoryPathEntry> walkCategoryPath(Category leaf) {
        // Walk parent chain, then reverse so root comes first.
        List<CategoryPathEntry> reversed = new ArrayList<>();
        Category current = leaf;
        while (current != null) {
            reversed.add(new CategoryPathEntry(
                    current.getId(),
                    current.getName(),
                    current.getSlug()
            ));
            current = current.getParent();
        }
        Collections.reverse(reversed);
        return reversed;
    }

    // Primary image is position 0; additional images are 1...n
    private List<ImageEntry> buildImageList(Product product) {
        List<ImageEntry> all = new ArrayList<>();
        all.add(new ImageEntry(product.getPrimaryImageUrl(), 0));
        List<ProductImage> additional = product.getAdditionalImages();
        for (int i = 0; i < additional.size(); i++) {
            all.add(new ImageEntry(additional.get(i).getUrl(), i + 1));
        }
        return all;
    }
}