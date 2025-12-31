//package com.projects.ecommerce;
//
//
//import com.projects.ecommerce.dto.request.CreateProductDto;
//import com.projects.ecommerce.entity.ProductNutrition;
//import com.projects.ecommerce.entity.ProductVariant;
//import com.projects.ecommerce.service.UserService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.test.web.servlet.MockMvc;
//import tools.jackson.databind.ObjectMapper;
//
//import java.math.BigDecimal;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
//public class ProductTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//
//    @Autowired
//    private UserService searchService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//
//
//
//    @Test
//    void testCreateCarrotProduct() throws Exception {// ---------- NUTRITION ----------
//        // ---------- NUTRITION ----------
//        ProductNutrition nutrition = new ProductNutrition();
//        nutrition.setCalories("97 kcal per 100 g");
//        nutrition.setProtein("2.2 g per 100 g");
//        nutrition.setCarbs("23 g per 100 g");
//        nutrition.setFiber("10.4 g per 100 g");
//        nutrition.setVitamins(Arrays.asList("Vitamin C", "Vitamin A", "Iron", "Potassium"));
//
//// ---------- VARIANTS ----------
//        ProductVariant v1 = new ProductVariant();
//        v1.setValue(BigDecimal.valueOf(0.25));
//        v1.setUnit("kg");
//        v1.setPrice(120.00);
//        v1.setStock(60);
//
//        ProductVariant v2 = new ProductVariant();
//        v2.setValue(BigDecimal.valueOf(0.5));
//        v2.setUnit("kg");
//        v2.setPrice(220.00);
//        v2.setStock(35);
//
//        ProductVariant v3 = new ProductVariant();
//        v3.setValue(BigDecimal.valueOf(1.0));
//        v3.setUnit("kg");
//        v3.setPrice(400.00);
//        v3.setStock(20);
//
//// ---------- MAIN DTO ----------
//        CreateProductDto dto = new CreateProductDto();
//        dto.setCategory(9L);
//        dto.setName("Passion Fruit");
//        dto.setSlug("passion-fruit");
//        dto.setDescription("Fresh aromatic passion fruits with tangy-sweet pulp.");
//        dto.setFeatured(false);
//        dto.setOrganic(true);
//        dto.setNutrition(nutrition);
//        dto.setVariants(Arrays.asList(v1, v2, v3));
//
//        // Convert DTO → JSON
//        String dtoJson = objectMapper.writeValueAsString(dto);
//
//        // ---------- JSON PART ----------
//        MockMultipartFile productPart = new MockMultipartFile(
//                "product",
//                "product.json",
//                "application/json",
//                dtoJson.getBytes()
//        );
//
//        byte[] imageBytes = Files.readAllBytes(Paths.get("C:\\Users\\rajta\\OneDrive\\Desktop\\My data\\images\\products\\Exotic-Selection\\Passion Fruit.webp"));
////        byte[] imageBytes = Files.readAllBytes(Paths.get("C:\Users\rajta\OneDrive\Desktop\My data\images\products\Exotic-Selection\dragon-fruits.webp"));
////        byte[] imageBytes = Files.readAllBytes(Paths.get("C:\Users\rajta\OneDrive\Desktop\My data\images\products\Exotic-Selection\Passion Fruit.webp"));
//
//        MockMultipartFile image = new MockMultipartFile(
//                "images",                 // request part name
//                "carrot.jpg",             // file name
//                "image/jpeg",             // content type
//                imageBytes                // actual bytes
//        );
//
//        // ---------- SEND REQUEST ----------
//        mockMvc.perform(
//                multipart("/products/admin")
//                        .file(productPart)
//                        .file(image)
//                        .contentType(MediaType.MULTIPART_FORM_DATA)
//                        .accept(MediaType.APPLICATION_JSON)
//        ).andExpect(status().isCreated());
//    }
//}