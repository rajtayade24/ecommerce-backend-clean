    package com.projects.ecommerce.entity;

    import jakarta.persistence.*;
    import lombok.Getter;
    import lombok.Setter;

    import java.util.ArrayList;
    import java.util.List;

    @Getter
    @Setter
    @Embeddable //@Embeddable must NOT have @Table.
    public class ProductNutrition {

        @Column(name = "nutrition_calories")
        private String calories;

        // keep these as strings to preserve units like "0.9g"
        @Column(name = "nutrition_protein")
        private String protein;

        @Column(name = "nutrition_carbs")
        private String carbs;
    
        @Column(name = "nutrition_fiber")
        private String fiber;

        // vitamins as a collection table (product_id will be join column by parent entity)
        @ElementCollection(fetch = FetchType.EAGER)
        @CollectionTable(name = "product_nutrition_vitamins", joinColumns = @JoinColumn(name = "product_id"))
        @Column(name = "vitamin")
        private List<String> vitamins = new ArrayList<>();
    }
