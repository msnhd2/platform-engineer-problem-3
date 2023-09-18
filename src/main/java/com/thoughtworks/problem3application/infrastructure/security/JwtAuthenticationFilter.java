package com.thoughtworks.problem3application.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.servlet.support.RequestContextUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Objects;

@Component
public class JwtAuthenticationFilter extends GenericFilterBean {

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		final HttpServletRequest request = (HttpServletRequest) servletRequest;
		final HttpServletResponse response = (HttpServletResponse) servletResponse;
		final String authHeader = request.getHeader("Authorization");

		if ("OPTIONS".equals(request.getMethod())) {
			response.setStatus(HttpServletResponse.SC_OK);
			filterChain.doFilter(request, response);
			return;
		}

		if (!"/api/users".equals(request.getRequestURI())) {
			filterChain.doFilter(request, response);
			return;
		}

		if (Objects.isNull(authHeader) || !authHeader.startsWith("Bearer ")) {
			throw new ServletException("Access denied");
		}

		final Environment environment = RequestContextUtils.findWebApplicationContext(request).getEnvironment();
		String jwtSecret = environment.getProperty("jwt.secret");
		final String token = authHeader.substring(7);
		Claims claims = Jwts.parser().setSigningKey(
				jwtSecret)
				.parseClaimsJws(token).getBody();
		request.setAttribute("claims", claims);

		filterChain.doFilter(request, response);
	}
}
