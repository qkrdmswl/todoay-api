package com.todoay.api.global.config;

import com.todoay.api.domain.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final ProfileService profileService;

    @Override
    public void configure(WebSecurity web){
        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/login", "/signup", "/profile").permitAll()  // 누구나 접근 가능
                    .antMatchers("/").hasRole("USER")  // USER, ADMIN만 접근 가능
                    .antMatchers("/admin").hasRole("ADMIN")  // ADMIN만
                    .anyRequest().authenticated()  // 나머지 요청들은 권한의 종류에 상관없이 권한이 있어야 접근

                .and()
                    .formLogin()
                        .loginPage("/login")  // 로그인 페이지 링크
                        .defaultSuccessUrl("/")  // 로그인 성공 후 리다이렉트 주소
                .and()
                    .logout()
                        .logoutSuccessUrl("/login")  // 로그아웃 성공시 리다이렉트 주소
                        .invalidateHttpSession(true);

    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(profileService).passwordEncoder(new BCryptPasswordEncoder());
    }
}