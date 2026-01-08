package com.projects.ecommerce.service.impl;

import com.projects.ecommerce.dto.CartItemDto;
import com.projects.ecommerce.dto.ProductDto;
import com.projects.ecommerce.dto.request.CreateProductDto;
import com.projects.ecommerce.dto.response.CloudinaryUploadResult;
import com.projects.ecommerce.entity.CartItem;
import com.projects.ecommerce.entity.Category;
import com.projects.ecommerce.entity.Product;
import com.projects.ecommerce.entity.ProductImage;
import com.projects.ecommerce.repository.CartItemRepository;
import com.projects.ecommerce.repository.ProductRepository;
import com.projects.ecommerce.service.CategoryService;
import com.projects.ecommerce.service.CloudService;
import com.projects.ecommerce.service.ProductService;

import com.projects.ecommerce.service.UserService;
import com.projects.ecommerce.specification.ProductSpecifications;
import com.projects.ecommerce.util.MapperUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final CategoryService categoryService;
    private final CartItemRepository cartItemRepository;
    private final UserService service;
    private final MapperUtil mapper;
    private final CloudService cloudinaryService;

    @Override
    public ProductDto create(@Valid CreateProductDto dto, MultipartFile[] images) {
        log.info("Creating new product: {}", dto.getName());

        // 1) Validate category
        Category category = categoryService.getCategoryById(dto.getCategory());
        //                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + dto.getCategory()));

        // 2) Map DTO → Entity
        Product product = modelMapper.map(dto, Product.class);
        product.setCategory(category);

        // 3) Generate slug from product name
        String slug = dto.getName()
                .trim()
                .toLowerCase()
                .replaceAll("\\s+", "-")
                .replaceAll("[^a-z0-9\\-]", "");
        product.setSlug(slug);

//        // 2. Handle image uploads
//        if (images != null && images.length > 0) {
//            try {
//                List<String> urls = Arrays.stream(images)
//                        .filter(img -> img != null && !img.isEmpty()) // avoid null/empty
//                        .map(cloudinaryService::upload)
//                        .collect(Collectors.toList());
//                product.setImages(urls);
//                log.info("Uploaded URLs: {}", urls);
//
//            } catch (Exception e) {
//                throw new RuntimeException("Failed to upload product images: " + e.getMessage(), e);
//            }
//        } else {
//            log.info("No images provided for product");
//        }

        // 2. Handle image uploads
        if (images != null && images.length > 0) {
            try {
                List<ProductImage> productImages = Arrays.stream(images)
                        .filter(img -> img != null && !img.isEmpty())
                        .map(file -> {
                            CloudinaryUploadResult result = cloudinaryService.upload(file);

                            ProductImage image = new ProductImage();
                            image.setImage(result.getImage());
                            image.setPublicId(result.getPublicId());
                            image.setProduct(product); // IMPORTANT (bidirectional)

                            return image;
                        })
                        .toList();

                product.setImages(productImages);

                log.info(
                        "Uploaded {} images for product {}",
                        productImages.size(),
                        product.getName()
                );

            } catch (Exception e) {
                throw new RuntimeException("Failed to upload product images", e);
            }
        } else {
            log.info("No images provided for product");
        }

        product.getVariants().forEach(v -> {
            v.setLabel(v.getValue().toString() + v.getUnit());
            v.setSku(
                    category.getName().trim().toUpperCase()
                            + "-"
                            + product.getName().trim().toUpperCase()
                            + "-"
                            + v.getValue().toString().toUpperCase()
                            + v.getUnit().trim().toUpperCase()
            );
        });
        ///         product.setVariants(dto.getVariants()); already done
        ////         now for bidirectional relationship add product_id in product_verient table
        if (product.getVariants() != null) {
            product.getVariants().forEach(variant -> variant.setProduct(product));
        }

        // save without reassigning product
        Product saved = productRepository.save(product);

        return mapper.mapProductToProductDto(saved);
    }

    @Override
    public ProductDto update(Long id, @Valid CreateProductDto dto, MultipartFile[] images) {
        log.info("updating product: {}", dto.getName());

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setFeatured(dto.isFeatured());
        product.setOrganic(dto.isOrganic());
        product.setNutrition(dto.getNutrition());

        if (dto.getCategory() != null) {
            Category category = categoryService.getCategoryById(dto.getCategory());
            product.setCategory(category);
        }
        String slug = dto.getName()
                .trim()
                .toLowerCase()
                .replaceAll("\\s+", "-")
                .replaceAll("[^a-z0-9\\-]", "");
        product.setSlug(slug);

        // 2. Handle image uploads
        if (images != null && images.length > 0) {
            try {
                List<ProductImage> productImages = Arrays.stream(images)
                        .filter(img -> img != null && !img.isEmpty())
                        .map(file -> {
                            CloudinaryUploadResult result = cloudinaryService.upload(file);

                            ProductImage image = new ProductImage();
                            image.setImage(result.getImage());
                            image.setPublicId(result.getPublicId());
                            image.setProduct(product); // IMPORTANT (bidirectional)

                            return image;
                        })
                        .toList();

                product.setImages(productImages);

                log.info(
                        "Uploaded {} images for product {}",
                        productImages.size(),
                        product.getName()
                );

            } catch (Exception e) {
                throw new RuntimeException("Failed to upload product images", e);
            }
        } else {
            log.info("No images provided for product");
        }

        if (dto.getVariants() != null) {
            product.getVariants().clear();

            dto.getVariants().forEach(variant -> {
                variant.setProduct(product);
                product.getVariants().add(variant);
            });
        }
        Product updated = productRepository.save(product);

        return mapper.mapProductToProductDto(updated);
    }


    @Override
    public Page<ProductDto> getProducts(Pageable pageable) {
        Page<Product> productPages = productRepository.findAll(pageable);
        return productPages.map(mapper::mapProductToProductDto);
    }

    @Override
    public Page<ProductDto> getProducts(String search, Pageable pageable) {
        Page<Product> products;

        if (search != null && !search.isBlank()) {
            products = productRepository
                    .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search, pageable);
        } else {
            products = productRepository.findAll(pageable);
        }

        return products.map(mapper::mapProductToProductDto);
    }

    @Override
    public CartItemDto addToCart(ProductDto dto) {
        Product product = productRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + dto.getId()));

        CartItem cartItem = modelMapper.map(dto, CartItem.class);
        cartItem.setId(null);

        CartItem savedCartItem = cartItemRepository.save(cartItem);

        return modelMapper.map(savedCartItem, CartItemDto.class);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    @Override
    public ProductDto getProductDtoById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return mapper.mapProductToProductDto(product);
    }

    @Override
    public Page<ProductDto> getProducts(String search, String categorySlug, Boolean isOrganic, Boolean isFeatured, String sortMode, Pageable pageable) {
        Specification<Product> spec = ProductSpecifications.combine(search, categorySlug, isOrganic, isFeatured);

        // adjust sort if sortMode specified
        Pageable effectivePageable = pageable;
        if (sortMode != null) {
            Sort sort;
            if ("mostOrdered".equalsIgnoreCase(sortMode)) {
                // sort by ordersInPastMonth desc then fallback to createdAt
                sort = Sort.by(Sort.Order.desc("ordersInPastMonth"), Sort.Order.desc("createdAt"));
            } else { // "new" or default
                sort = Sort.by(Sort.Order.desc("createdAt"));
            }
            effectivePageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        }

        Page<Product> page = productRepository.findAll(spec, effectivePageable);
        return mapProductPageToDtoPage(page, effectivePageable);
    }

    // Helper: map entity page -> dto page
    private Page<ProductDto> mapProductPageToDtoPage(Page<Product> productPage, Pageable pageable) {
        var dtoList = productPage.getContent()
                .stream()
                .map(mapper::mapProductToProductDto)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, productPage.getTotalElements());
    }

    @Override
    public Long countProducts() {
        return productRepository.count();
    }

    @Override
    public void delete(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));

        // Delete stored image first
        if (product.getImages() != null) {
            try {
                product.getImages().stream()
                        .filter(img -> img.getPublicId() != null && !img.getPublicId().isEmpty())
                        .forEach(img -> service.deleteFile(img.getPublicId()));
            } catch (Exception e) {
                throw new RuntimeException("Failed to delete image: " + e.getMessage(), e);
            }
        }


        // Delete category from DB
        productRepository.delete(product);
        log.info("Category deleted successfully with id: {}", id);

    }
}
