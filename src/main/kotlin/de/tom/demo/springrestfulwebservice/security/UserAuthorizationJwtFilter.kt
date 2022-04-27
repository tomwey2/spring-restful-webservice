package de.tom.demo.springrestfulwebservice.security

import com.fasterxml.jackson.databind.ObjectMapper
import de.tom.demo.springrestfulwebservice.entities.ResponseMessage
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 *  Filter to handle authorization process. It will intercept the requests for protected operations,
 *  load its JWT token and validate it. If it is a valid token the filter let the request go on,
 *  otherwise it will throw an authorization error.
 */
class UserAuthorizationJwtFilter : OncePerRequestFilter() {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val objectMapper = ObjectMapper()

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        log.info("Authorize request: ${request.servletPath}")
        /*
        – get JWT from the Authorization header (by removing Bearer prefix)
        – if the request has JWT, validate it, parse username from it
        – from username, get UserDetails to create an Authentication object
        – set the current UserDetails in SecurityContext using setAuthentication(authentication) method.
        */
        if (request.servletPath == "/login") {
            filterChain.doFilter(request, response);
            return
        }

        val jwt: String? = parseJwt(request)
        if (jwt == null) {
            filterChain.doFilter(request, response);
            return
        }

        try {
            log.info("Authorize request with JWT: ${jwt}")

            validateJwtToken(jwt)
            val authentication: UsernamePasswordAuthenticationToken = verifyToken(jwt)
            authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
            SecurityContextHolder.getContext().authentication = authentication
            filterChain.doFilter(request, response)

            log.info("Authorisation successful")
        } catch (e: Exception) {
            log.error(e.message)
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            objectMapper.writeValue(response.outputStream,
                ResponseMessage("Authorisation failed", e.message)
            )
        }
    }

    fun parseJwt(request: HttpServletRequest): String? {
        val authorizationHeader = request.getHeader(AUTHORIZATION)
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            val token = authorizationHeader.substring("Bearer ".length)
            return token;
        }
        return null;
    }
}