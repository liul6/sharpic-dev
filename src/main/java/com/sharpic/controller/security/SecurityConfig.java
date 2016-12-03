package com.sharpic.controller.security;


import com.sharpic.domain.User;
import com.sharpic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.List;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserRepository userRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
//                .antMatchers("/","/home").permitAll()
                .antMatchers("/admin").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
            .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login")
                .permitAll();
        http.exceptionHandling().accessDeniedPage("/403");
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .inMemoryAuthentication()
                .withUser("user").password("user").roles("USER")
                .and()
        		.withUser("admin").password("admin").roles("ADMIN");
        System.out.println("###user count = " + userRepository.count());

        List<User> users = userRepository.findAll();
        if (users != null) {
            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);
                auth
                        .inMemoryAuthentication()
                        .withUser(user.getUsername()).password(user.getPassword()).roles(user.getRole());

                System.out.println("###user = " + user.getUsername());
            }
        }
    }
}