//package com.projects.ecommerce;
//
////import com.projects.ecommerce.repo.custom.ProductRepositoryCustom;
////import com.projects.ecommerce.repo.custom.CategoryRepositoryCustom;
//import com.projects.ecommerce.entity.User;
//import com.projects.ecommerce.repository.custom.CategoryRepositoryCustom;
//import com.projects.ecommerce.repository.custom.ProductRepositoryCustom;
//import com.projects.ecommerce.service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class SearchServiceTest {
//
//    @Mock
//    private ProductRepositoryCustom productRepoCustom;
//
//    @Mock
//    private CategoryRepositoryCustom categoryRepoCustom;
//
//    @InjectMocks
//    private UserService searchService;
//
//    @BeforeEach
//    void setUp() {
//        // MockitoExtension handles init
//    }
//
//    @Test
//    void shouldReturnEmptyList_whenSearchIsNull() {
//        List<String> result = searchService.suggestKeywords(null, 10);
//
//        assertThat(result).isEmpty();
//        verifyNoInteractions(productRepoCustom, categoryRepoCustom);
//    }
//
//    @Test
//    void shouldReturnEmptyList_whenSearchIsBlank() {
//        List<String> result = searchService.suggestKeywords("   ", 10);
//
//        assertThat(result).isEmpty();
//        verifyNoInteractions(productRepoCustom, categoryRepoCustom);
//    }
//
//    @Test
//    void shouldMergeAndDeduplicateSuggestions_andRespectLimit() {
//        // given
//        String q = "app";
//        int limit = 5;
//
//        when(productRepoCustom.suggestProductNames(q, limit))
//                .thenReturn(List.of("Apple", "Apple Juice"));
//
//        when(productRepoCustom.suggestProductDescriptionSnippets(q, limit))
//                .thenReturn(List.of(
//                        "Fresh apple from farm",
//                        "Apple juice made naturally",
//                        "Apple" // duplicate on purpose
//                ));
//
//        when(categoryRepoCustom.suggestCategoryNamesOrDescriptions(q, limit))
//                .thenReturn(List.of(
//                        "Apples",
//                        "Fresh Fruits",
//                        "Apple Juice" // duplicate on purpose
//                ));
//
//        // when
//        List<String> result = searchService.suggestKeywords(q, limit);
//
//        // then
//        assertThat(result)
//                .hasSize(limit) // respects limit
//                .containsExactly(
//                        "Apple",
//                        "Apple Juice",
//                        "Fresh apple from farm",
//                        "Apple juice made naturally",
//                        "Apples"
//                );
//
//        verify(productRepoCustom).suggestProductNames(q, limit);
//        verify(productRepoCustom).suggestProductDescriptionSnippets(q, limit);
//        verify(categoryRepoCustom).suggestCategoryNamesOrDescriptions(q, limit);
//    }
//
//    @Test
//    void shouldIgnoreNullAndBlankValues() {
//        String q = "veg";
//        int limit = 10;
//
//        when(productRepoCustom.suggestProductNames(q, limit))
//                .thenReturn(List.of("Vegetables", null, "  "));
//
//        when(productRepoCustom.suggestProductDescriptionSnippets(q, limit))
//                .thenReturn(List.of("Fresh vegetables"));
//
//        when(categoryRepoCustom.suggestCategoryNamesOrDescriptions(q, limit))
//                .thenReturn(List.of("", "Organic Veggies"));
//
//        List<String> result = searchService.suggestKeywords(q, limit);
//
//        assertThat(result)
//                .containsExactly(
//                        "Vegetables",
//                        "Fresh vegetables",
//                        "Organic Veggies"
//                );
//    }
//}
