package com.hnue.commerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
public class SecurityConfig {
    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource){
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        jdbcUserDetailsManager.setUsersByUsernameQuery("select email, password, enable from users where email=?");
        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery("select email, role from users where email=?");
        return jdbcUserDetailsManager;
    }

    private final String[] publicUrl =
            {"/", "/about", "/images/**", "/css/**", "/js/**",
            "/register", "/process-register", "/shop/**",
            "/forgot","/otp","/reset"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.authorizeHttpRequests(configurer ->
                        configurer
                                .requestMatchers(publicUrl).permitAll()
                                .requestMatchers("/admin/**").hasRole("admin")
                                .anyRequest().authenticated()
                )
                .formLogin(login ->
                        login
                                .loginPage("/account")
                                .loginProcessingUrl("/authenticateTheUser")
                                .defaultSuccessUrl("/", true)
                                .permitAll()
                )
                .logout(logout ->
                        logout
                                .logoutSuccessUrl("/")
                                .permitAll()
                                .invalidateHttpSession(true)
                                .clearAuthentication(true)
                )
                .exceptionHandling(configurer ->
                        configurer
                                .accessDeniedPage("/access-denied")
                )
                .csrf(csrf->csrf.disable())
                ;

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
