package com.projects.ecommerce.config;

import com.projects.ecommerce.dto.ProductDto;
import com.projects.ecommerce.entity.Product;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.addMappings(new PropertyMap<Product, ProductDto>() {
            @Override
            protected void configure() {
                // Map Category object to category ID
                map(source.getCategory() != null ? source.getCategory().getId() : null, destination.getCategory());

                // Skip variants to avoid recursion
                skip(destination.getVariants());
            }
        });

//             Now ModelMapper will not touch addresses at all
//        // DTO → Entity
//        modelMapper.addMappings(new PropertyMap<UserDto, User>() {
//            @Override
//            protected void configure() {
//                skip(destination.getAddresses()); // ignore addresses
//            }
//        });
//        // Entity → DTO
//        modelMapper.addMappings(new PropertyMap<User, UserDto>() {
//            @Override
//            protected void configure() {
//                skip(destination.getAddress()); // DTO has only one address
//            }
//        });
        return modelMapper;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


//    @Bean
//    UserDetailsService userDetailsService() {
//        UserDetails user1 = User.withUsername("admin")
//                .password(passwordEncoder().encode("Raj@24"))
//                .roles("ADMIN")
//                .build();
//        return new InMemoryUserDetailsManager(user1);
//    }

}
