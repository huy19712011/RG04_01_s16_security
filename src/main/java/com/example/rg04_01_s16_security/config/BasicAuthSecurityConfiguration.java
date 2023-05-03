package com.example.rg04_01_s16_security.config;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
//@EnableMethodSecurity
//@EnableMethodSecurity(jsr250Enabled = true)
@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true)
public class BasicAuthSecurityConfiguration {

    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> {
            auth
                    .requestMatchers("users").hasRole("USER")
                    .requestMatchers("admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated();
        });
//        http.formLogin();

        // disable session
        http.sessionManagement(session -> {
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        });

        //disable CSRF
        http.csrf().disable();

        // enable frames
        http.headers().frameOptions().sameOrigin();

        http.httpBasic();
        return http.build();
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//
//        UserDetails user = User
//                .withUsername("user")
//                .password("{noop}123")
//                .roles("USER")
//                .build();
//
//        UserDetails admin = User
//                .withUsername("admin")
//                .password("{noop}123")
//                .roles("ADMIN")
//                .build();
//
//        return new InMemoryUserDetailsManager(user, admin);
//    }

    @Bean
    public DataSource dataSource() {

        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {

        UserDetails user = User
                .withUsername("user")
//                .password("{noop}123")
                .password("123")
                .passwordEncoder(str -> passwordEncoder().encode(str))
                .roles("USER")
                .build();

        UserDetails admin = User
                .withUsername("admin")
//                .password("{noop}123")
                .password("123")
                .passwordEncoder(str -> passwordEncoder().encode(str))
                .roles("ADMIN")
                .build();

        JdbcUserDetailsManager jdbcUserDetailsManager =
                new JdbcUserDetailsManager(dataSource);
        jdbcUserDetailsManager.createUser(user);
        jdbcUserDetailsManager.createUser(admin);

        return jdbcUserDetailsManager;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

}
