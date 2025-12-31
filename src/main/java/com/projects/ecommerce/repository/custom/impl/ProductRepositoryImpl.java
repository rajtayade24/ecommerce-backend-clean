package com.projects.ecommerce.repository.custom.impl;


import com.projects.ecommerce.repository.custom.ProductRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<String> suggestProductNames(String q, int limit) {
        if (q == null || q.isBlank()) return Collections.emptyList();
        String like = "%" + q.trim().toLowerCase() + "%";

        // JPQL distinct product names
        List<String> names = em.createQuery(
                        "SELECT DISTINCT p.name FROM Product p WHERE LOWER(p.name) LIKE :like ORDER BY p.name",
                        String.class)
                .setParameter("like", like)
                .setMaxResults(limit)
                .getResultList();
        return names;
    }

    @Override
    public List<String> suggestProductDescriptionSnippets(String q, int limit) {
        if (q == null || q.isBlank()) return Collections.emptyList();

        String lowerQ = q.trim().toLowerCase();
        // Try Postgres-specific snippet extraction for better UX.
        try {
            String sql = """
                SELECT DISTINCT
                   CASE
                     WHEN position(lower(:q) in lower(description)) > 0 THEN
                       substring(description from greatest(position(lower(:q) in lower(description)) - 30, 1) for 120)
                     ELSE
                       substring(description from 1 for 120)
                   END AS snippet
                FROM products
                WHERE description IS NOT NULL
                  AND lower(description) LIKE :like
                LIMIT :limit
                """;

            Query nativeQuery = em.createNativeQuery(sql);
            nativeQuery.setParameter("q", lowerQ);
            nativeQuery.setParameter("like", "%" + lowerQ + "%");
            nativeQuery.setParameter("limit", limit);

            @SuppressWarnings("unchecked")
            List<String> snippets = nativeQuery.getResultList();
            // Optionally prefix with product name: we'd need to join product name; keep snippet only for now
            return snippets;
        } catch (Exception ex) {
            // If native SQL fails (non-Postgres DB), fallback to a simple JPQL returning beginning of description
            List<String> fallback = em.createQuery(
                            "SELECT DISTINCT substring(p.description, 1, 120) FROM Product p " +
                                    "WHERE LOWER(p.description) LIKE :like", String.class)
                    .setParameter("like", "%" + lowerQ + "%")
                    .setMaxResults(limit)
                    .getResultList();
            return fallback;
        }
    }
}
