package de.tom.demo.springrestfulwebservice.security

import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class UserAuthorizationFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (request.servletPath == "/login") {
            filterChain.doFilter(request, response)
        } else {
            val authorizationHeader = request.getHeader(AUTHORIZATION)
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                val token = authorizationHeader.substring("Bearer ".length)
            }
        }
    }
}