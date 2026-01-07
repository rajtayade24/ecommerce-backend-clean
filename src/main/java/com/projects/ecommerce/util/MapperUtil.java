package com.projects.ecommerce.util;

import com.projects.ecommerce.dto.AddressDto;
import com.projects.ecommerce.dto.CartItemDto;
import com.projects.ecommerce.dto.ProductDto;
import com.projects.ecommerce.dto.request.OrderItemRequest;
import com.projects.ecommerce.dto.request.OrderRequest;
import com.projects.ecommerce.dto.response.OrderItemResponse;
import com.projects.ecommerce.dto.response.OrderResponse;
import com.projects.ecommerce.entity.*;
import com.projects.ecommerce.enums.OrderStatusType;
import com.projects.ecommerce.enums.PaymentMethodType;
import com.projects.ecommerce.repository.OrderRepository;
import com.projects.ecommerce.repository.ProductRepository;
import com.projects.ecommerce.repository.ProductVatiantRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Data
public class MapperUtil {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ProductVatiantRepository productVatiantRepository;
    private final DataUtil dataUtil;
    private final ModelMapper modelMapper;

    public ProductDto mapProductToProductDto(Product product) {
        ProductDto dto = new ProductDto();

        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSlug(product.getSlug());
        dto.setDescription(product.getDescription());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setFeatured(product.isFeatured());
        dto.setOrganic(product.isOrganic());

        // Nutrition
        dto.setNutrition(product.getNutrition());

        // Images
        String baseUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .build()
                .toUriString();

//        dto.setImages(
//                product.getImages()
//                        .stream()
//                        .map(img -> (img != null && img.startsWith("http"))
//                                ? img
//                                : baseUrl + img)
//                        .collect(Collectors.toList()));

        dto.setImages(
                product.getImages()
                        .stream()
                        .map(img -> {
                            String image = img.getImage();
                            return image.startsWith("http")
                                    ? image
                                    : baseUrl + (image.startsWith("/") ? "" : "/") + image;
                        })
                        .toList()
        );


        // Variants
        if (product.getVariants() != null) {
            dto.setVariants(new ArrayList<>(product.getVariants())); // copy existing variants
        }

        // Category ID
        if (product.getCategory() != null) {
            dto.setCategory(product.getCategory().getId());
        }

        long totalStock = product.getVariants()
                .stream()
                .mapToLong(ProductVariant::getStock)
                .sum();
        dto.setInStock(totalStock);

        return dto;
    }

    public CartItemDto mapCartItemToCartItemDto(CartItem item) {
        CartItemDto dto = new CartItemDto();
        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getPrice());
        dto.setTotalPrice(item.getPrice() * item.getQuantity());
        dto.setCreatedAt(item.getCreatedAt());

        // --- Product mapping ---
        Product product = item.getProduct();
        if (product != null) {
            ProductDto pDto = new ProductDto();
            pDto.setId(product.getId());
            pDto.setName(product.getName());
            pDto.setSlug(product.getSlug());
            pDto.setDescription(product.getDescription());
            pDto.setCreatedAt(product.getCreatedAt());
            pDto.setFeatured(product.isFeatured());
            pDto.setOrganic(product.isOrganic());
            pDto.setNutrition(product.getNutrition());
            // Images
            String baseUrl = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .build()
                    .toUriString();

//            pDto.setImages(
//                    product.getImages()
//                            .stream()
//                            .map(img -> img.startsWith("http") ? img : baseUrl + img)
//                            .collect(Collectors.toList())
//            );

            pDto.setImages(
                    product.getImages()
                            .stream()
                            .map(img -> {
                                String image = img.getImage();
                                return image.startsWith("http")
                                        ? image
                                        : baseUrl + (image.startsWith("/") ? "" : "/") + image;
                            })
                            .toList()
            );

            // map category ID
            pDto.setCategory(product.getCategory() != null ? product.getCategory().getId() : null);

            // Do NOT map variants here to avoid recursion
            dto.setProduct(pDto);
        }

        // --- Variant mapping ---
        if (item.getVariant() != null) {
            ProductVariant v = item.getVariant();
            // Either map to a simple DTO or return the entity directly
            ProductVariant variantDto = new ProductVariant();
            variantDto.setId(v.getId());
            variantDto.setPrice(v.getPrice());
            variantDto.setUnit(v.getUnit());
            dto.setVariant(variantDto);
        }

        return dto;
    }

    public Address copyAddressFromDto(AddressDto dto) {
        return Address.builder()
                .line1(dto.getLine1())
                .line2(dto.getLine2())
                .primaryAddress(dto.isPrimaryAddress())
                .name(dto.getName())
                .phone(dto.getPhone())
                .city(dto.getCity())
                .state(dto.getState())
                .pincode(dto.getPincode())
                .country(dto.getCountry() == null ? "INDIA" : dto.getCountry())
                .build();
    }

    public AddressDto copyAddressFromEntity(Address src) {
        return AddressDto.builder()
                .name(src.getName())
                .phone(src.getPhone())
                .city(src.getCity())
                .state(src.getState())
                .pincode(src.getPincode())
                .country(src.getCountry())
                .build();
    }

    public BigDecimal nvl(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    public String generateOrderNumber() {
        // Simple readable order number: ORD-YYYYMMDD-HHMMSS-<random4>
        String ts = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int rnd = new Random().nextInt(9000) + 1000;
        return "ORD-" + ts + "-" + rnd;
    }

    public Order toOrderEntity(OrderRequest req, User user) {
        if (req == null)
            throw new IllegalArgumentException("OrderRequest is required");

        Order order = Order.builder()
                .user(user)
                .customer(user.getName())
                .orderNumber(generateOrderNumber()) // implement generateOrderNumber()
                .shippingTotal(nvl(req.getShippingTotal()))
                .currency(req.getCurrency() == null ? "USD" : req.getCurrency())
                .status(OrderStatusType.PENDING)
                .paymentMethod(req.getPaymentMethod() == null
                        ? PaymentMethodType.STRIPE
                        : PaymentMethodType.valueOf(req.getPaymentMethod()))
                .build();

        Address snapshotAddress;
        if (req.getShippingAddress() != null) {
            snapshotAddress = copyAddressFromDto(req.getShippingAddress());
        } else {
            throw new IllegalArgumentException("shipping address missing");
        }
        order.setShippingAddress(snapshotAddress);

        // Build order items from DB data (server authoritative)
        List<OrderItem> items = new ArrayList<>();
        for (OrderItemRequest i : req.getItems()) {
            if (i.getVariantId() == null)
                throw new IllegalArgumentException("variantId required");
            if (i.getQuantity() == null || i.getQuantity() <= 0)
                throw new IllegalArgumentException("quantity > 0 required");

            ProductVariant variant = productVatiantRepository.findById(i.getVariantId())
                    .orElseThrow(() -> new RuntimeException(
                            "variant not found: " + i.getVariantId()));

            Product product = variant.getProduct();
            BigDecimal unitPrice = nvl(BigDecimal.valueOf(variant.getPrice()));

            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(i.getQuantity())).setScale(2,
                    RoundingMode.HALF_UP);

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .productId(product.getId())
                    .productName(product.getName())
                    .variantId(variant.getId())
                    .variantLabel(variant.getLabel())
                    .sku(variant.getSku())
                    .quantity(i.getQuantity())
                    .unitPrice(unitPrice)
                    .lineTotal(lineTotal)
                    .build();

            items.add(item);
        }

        // server compute totals
        BigDecimal itemsTotal = items.stream()
                .map(OrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        order.setItems(items);
        order.setItemsTotal(itemsTotal);

        // Calculate shipping/tax/discount on server — DO NOT USE client provided totals
        BigDecimal shippingTotal = dataUtil.calculateShippingTotal(req, items, snapshotAddress);
        BigDecimal taxTotal = dataUtil.calculateTaxTotal(req, items, snapshotAddress);
        BigDecimal discountTotal = dataUtil.calculateDiscountTotal(req, items);

        order.setShippingTotal(shippingTotal);
        order.setTaxTotal(taxTotal);
        order.setDiscountTotal(discountTotal);

        BigDecimal grand = itemsTotal
                .add(shippingTotal)
                .add(taxTotal)
                .subtract(discountTotal)
                .setScale(2, RoundingMode.HALF_UP);
        order.setTotalAmount(grand);

        return order;
    }

    public OrderResponse mapOrderToResponse(Order order) {
        if (order == null)
            return null;

        return OrderResponse.builder()
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus() != null ? order.getStatus().name() : null)
                .customer(order.getCustomer())

                .itemsTotal(order.getItemsTotal())
                .shippingTotal(order.getShippingTotal())
                .taxTotal(order.getTaxTotal())
                .discountTotal(order.getDiscountTotal())
                .totalAmount(order.getTotalAmount())

                .currency(order.getCurrency())
                .paymentMethod(
                        order.getPaymentMethod() != null
                                ? order.getPaymentMethod().name()
                                : null)

                .stripeSessionId(order.getStripeSessionId())
                .stripePaymentIntentId(order.getStripePaymentIntentId())

                .paidAt(order.getPaidAt())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())

                .shippingAddress(copyAddressFromEntity(order.getShippingAddress()))

                .items(mapItems(order.getItems()))
                .build();
    }

    public List<OrderItemResponse> mapItems(List<OrderItem> items) {
        if (items == null)
            return List.of();

        return items.stream().map(this::mapOrderItemToResponse)
                .collect(Collectors.toList());
    }

    public OrderItemResponse mapOrderItemToResponse(OrderItem item) {
        Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Images
        String baseUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .build()
                .toUriString();

//        if (product.getImages() != null && !product.getImages().isEmpty()) {
//            String img = product.getImages().get(0);
//            image = img.startsWith("http") ? img : baseUrl + img;
//        }
        String image = Objects.requireNonNull(product.getImages()
                .stream()
                .findFirst()
                .map(img -> img.getImage().startsWith("http") ? img : baseUrl + img)
                .orElse(null)).toString();



        return OrderItemResponse.builder()
                .productId(item.getProductId())
                .productName(item.getProductName())
                .variantId(item.getVariantId())
                .variantLabel(item.getVariantLabel())
                .sku(item.getSku())
                .unitPrice(item.getUnitPrice())
                .quantity(item.getQuantity())
                .lineTotal(item.getLineTotal())
                .image(image) // fill later if needed from product service
                .build();
    }

}
