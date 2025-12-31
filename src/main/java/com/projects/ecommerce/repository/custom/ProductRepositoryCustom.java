package com.projects.ecommerce.repository.custom;

import java.util.List;

public interface ProductRepositoryCustom {
    List<String> suggestProductNames(String q, int limit);
    List<String> suggestProductDescriptionSnippets(String q, int limit);
}