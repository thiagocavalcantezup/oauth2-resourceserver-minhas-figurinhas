package br.com.zup.edu.minhasfigurinhas.configs;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class ResourceServerConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http.cors()
            .and()
                .csrf().disable()
                .httpBasic().disable()
                .rememberMe().disable()
                .formLogin().disable()
                .logout().disable()
                .requestCache().disable()
                .headers().frameOptions().deny()
            .and()
                .sessionManagement().sessionCreationPolicy(STATELESS)
            .and()
                .authorizeRequests()
                .antMatchers(POST, "/api/albuns").hasAuthority("SCOPE_albuns:write")
                .antMatchers(POST, "/api/albuns/*/figurinhas").hasAuthority("SCOPE_albuns:write")
                .antMatchers(GET, "/api/albuns").hasAuthority("SCOPE_albuns:read")
                .antMatchers(GET, "/api/albuns/*").hasAuthority("SCOPE_albuns:read")
                .anyRequest().authenticated()
            .and()
                .oauth2ResourceServer().jwt();
        // @formatter:on

        return http.build();
    }

}
