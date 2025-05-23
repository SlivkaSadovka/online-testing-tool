package org.testingTool.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.testingTool.services.MyUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final MyUserDetailsService myUserDetailsService;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.csrf(csrf -> csrf.ignoringRequestMatchers("/app-controller/**"))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/tests/**")
                    .authenticated()
                    .requestMatchers("/app-controller/**")
                    .permitAll()
                    .requestMatchers("/materials/download/**")
                    .hasAnyRole("GUEST", "ADMIN")
                    .requestMatchers("/materials/upload")
                    .hasRole("ADMIN")
                    .requestMatchers("/guest/**")
                    .hasRole("GUEST")
                    .requestMatchers("/admin/**")
                    .hasRole("ADMIN")
                    .requestMatchers("/test/**")
                    .hasRole("ADMIN")
                    .anyRequest()
                    .authenticated())
        .formLogin(
            login -> login.loginPage("/login").defaultSuccessUrl("/guest/", true).permitAll())
        .build();
  }

  @Bean
  public AuthenticationProvider userAuthenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(myUserDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }

  @Bean
  public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    return http.getSharedObject(AuthenticationManagerBuilder.class)
        .authenticationProvider(userAuthenticationProvider())
        .build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
