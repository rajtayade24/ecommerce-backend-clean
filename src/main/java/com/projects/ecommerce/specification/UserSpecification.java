package com.projects.ecommerce.specification;

import com.projects.ecommerce.entity.User;
import org.springframework.data.jpa.domain.Specification;

public final class UserSpecification {

    private UserSpecification() {}

    /**
     * Search across name, email, username, phone
     */
    public static Specification<User> hasSearch(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) return null;

            String like = "%" + search.trim().toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("name")), like),
                    cb.like(cb.lower(root.get("email")), like),
                    cb.like(cb.lower(root.get("mobile")), like)
            );
        };
    }

    /**
     * Filter by active status
     */
    public static Specification<User> isActive(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) return null;
            return cb.equal(root.get("active"), active);
        };
    }

    /**
     * Combine non-null specifications
     */
    public static Specification<User> combine(String search, Boolean active) {

        Specification<User> spec =
                (root, query, cb) -> cb.conjunction(); // always true

        Specification<User> s;

        s = hasSearch(search);
        if (s != null) spec = spec.and(s);

        s = isActive(active);
        if (s != null) spec = spec.and(s);

        return spec;
    }
}
