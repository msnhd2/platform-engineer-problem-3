package com.thoughtworks.problem3application.configurations;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.thoughtworks.problem3application.infrastructure.security.JwtAuthenticationFilter;

@Configuration
public class JwtConfig {
	@Bean
	public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilter() {

		FilterRegistrationBean<JwtAuthenticationFilter> filter = new FilterRegistrationBean<JwtAuthenticationFilter>();
		filter.setFilter(new JwtAuthenticationFilter());

		filter.addUrlPatterns("/api/users", "/api/users/*");
		return filter;
	}
}
