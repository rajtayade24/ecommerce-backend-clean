package com.projects.ecommerce.repository.custom;

import java.util.List;

public interface CategoryRepositoryCustom {
    List<String> suggestCategoryNamesOrDescriptions(String q, int limit);
}