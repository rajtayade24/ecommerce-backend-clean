package com.projects.ecommerce.repository.custom.impl;


import com.projects.ecommerce.repository.custom.CategoryRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class CategoryRepositoryImpl implements CategoryRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<String> suggestCategoryNamesOrDescriptions(String q, int limit) {
        if (q == null || q.isBlank()) return Collections.emptyList();
        String like = "%" + q.trim().toLowerCase() + "%";

        // category names
        List<String> names = em.createQuery(
                        "SELECT DISTINCT c.name FROM Category c WHERE LOWER(c.name) LIKE :like ORDER BY c.name",
                        String.class)
                .setParameter("like", like)
                .setMaxResults(limit)
                .getResultList();

        // category descriptions (prefix them with the category name)
        List<String> descs = em.createQuery(
                        "SELECT DISTINCT CONCAT(c.name, ' — ', substring(c.description, 1, 120)) FROM Category c " +
                                "WHERE c.description IS NOT NULL AND LOWER(c.description) LIKE :like",
                        String.class)
                .setParameter("like", like)
                .setMaxResults(limit)
                .getResultList();

        // merge giving priority to names
        names.addAll(descs);
        return names;
    }
}