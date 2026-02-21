package ru.iachnyi.dsr.practice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private CustomAuthenticationProvider authProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.anonymous(AbstractHttpConfigurer::disable).
                httpBasic(Customizer.withDefaults()).
                authorizeHttpRequests(auth -> auth
                        .requestMatchers("/welcome", "/login", "/register").permitAll()
                        .requestMatchers("/friends").authenticated()
                        .requestMatchers("/spendings").authenticated()
                        .requestMatchers("/spending").authenticated()
                        .requestMatchers("/api/register_user").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .requestMatchers("js/**").permitAll()
                        .requestMatchers("icon/**").permitAll()
                        .requestMatchers("css/**").permitAll())

                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/spendings").permitAll()).build();
    }


    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authProvider);
        return authenticationManagerBuilder.build();
    }
}