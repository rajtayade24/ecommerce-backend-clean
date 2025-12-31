package com.projects.ecommerce.specification;


import com.projects.ecommerce.entity.Product;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public final class ProductSpecifications {

    private ProductSpecifications() {}

    public static Specification<Product> hasSearch(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) return null;
            String like = "%" + search.trim().toLowerCase() + "%";
            // search in name or description
            return cb.or(
                    cb.like(cb.lower(root.get("name")), like),
                    cb.like(cb.lower(root.get("description")), like)
            );
        };
    }
    // ProductSpecifications.java (add or replace hasCategorySearch)
    public static Specification<Product> hasCategorySearch(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) return null;
            String like = "%" + search.trim().toLowerCase() + "%";

            // Join to category and search category.name or category.description
            Join<Object, Object> categoryJoin = root.join("category", JoinType.LEFT);

            return cb.or(
                    cb.like(cb.lower(root.get("name")), like),
                    cb.like(cb.lower(root.get("description")), like),
                    cb.like(cb.lower(categoryJoin.get("name")), like),
                    cb.like(cb.lower(categoryJoin.get("description")), like)
            );
        };
    }
    public static Specification<Product> hasCategorySlug(String categorySlug) {
        return (root, query, cb) -> {
            if (categorySlug == null || categorySlug.isBlank()) return null;
            // join to category and compare slug
            Join<Object, Object> category = root.join("category");
            return cb.equal(cb.lower(category.get("slug")), categorySlug.trim().toLowerCase());
        };
    }

    public static Specification<Product> hasIsOrganic(Boolean isOrganic) {
        return (root, query, cb) -> {
            if (isOrganic == null) return null;
            return cb.equal(root.get("isOrganic"), isOrganic);
        };
    }

    public static Specification<Product> hasIsFeatured(Boolean isFeatured) {
        return (root, query, cb) -> {
            if (isFeatured == null) return null;
            return cb.equal(root.get("isFeatured"), isFeatured);
        };
    }


//
//    // Compose helper
//    public static Specification<Product> combine(
//            String search,
//            String categorySlug,
//            Boolean isOrganic,
//            Boolean isFeatured
//    ) {   
//        Specification<Product> spec = Specification.where(null);
//        spec = spec.and(hasSearch(search));
//        spec = spec.and(hasCategorySlug(categorySlug));
//        spec = spec.and(isOrganic(isOrganic));
//        spec = spec.and(isFeatured(isFeatured));
//        return spec;
//    }
//}
    /**
     * Combine non-null specs. Starts with a conjunction (always true) to avoid null / ambiguity issues.
     */
    public static Specification<Product> combine(
            String search,
            String categorySlug,
            Boolean isOrganic,
            Boolean isFeatured
    ) {
        // start with an always-true specification (conjunction)
        Specification<Product> spec = (root, query, cb) -> cb.conjunction();

        Specification<Product> s;

        s = hasSearch(search);
        if (s != null) spec = spec.and(s);

        s = hasCategorySlug(categorySlug);
        if (s != null) spec = spec.and(s);

        s = hasIsOrganic(isOrganic);
        if (s != null) spec = spec.and(s);

        s = hasIsFeatured(isFeatured);
        if (s != null) spec = spec.and(s);

        return spec;
    }
}