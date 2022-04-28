package de.tom.demo.taskapp.security

import com.fasterxml.jackson.databind.ObjectMapper
import de.tom.demo.taskapp.entities.LoginResponseMessage
import de.tom.demo.taskapp.entities.ResponseMessage
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Filter to intercept the login request and validate the credentials.
 * If authentication is successful then it returns a LoginResponseMessage with the user details,
 * the access token and the refresh token.
 * If error occurs, handle AuthenticationException with AuthenticationEntryPoint (see: AuthEntryPointJwt).
 */
class UserAuthenticationJwtFilter(authenticationManager: AuthenticationManager) :
    UsernamePasswordAuthenticationFilter(authenticationManager) {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val objectMapper = ObjectMapper()

    override fun attemptAuthentication(request: HttpServletRequest?, response: HttpServletResponse?): Authentication {
        val username = request?.getParameter("email")
        val password = request?.getParameter("password")
        log.info("User $username attempts authentication")
        val authenticationToken = UsernamePasswordAuthenticationToken(username, password)
        return authenticationManager.authenticate(authenticationToken)
    }

    override fun successfulAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        chain: FilterChain?,
        authentication: Authentication?
    ) {
        val user: User = authentication?.principal as User
        val accessToken = createToken(user, 10 * 60 * 1000, request?.requestURL.toString())
        val refreshToken = createToken(user, 30 * 60 * 1000, request?.requestURL.toString())

        val roles: List<String> = user.authorities.map { it.authority }
        val loginResponse = LoginResponseMessage(user.username, roles, accessToken, refreshToken)
        response?.contentType ?: MediaType.APPLICATION_JSON_VALUE
        objectMapper.writeValue(response?.outputStream, loginResponse)
        log.info("Authentication of user ${user.username} as $roles was successful.")
    }

    override fun unsuccessfulAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        failed: AuthenticationException?
    ) {
        log.error("Authentication was unsuccessful. Message: {}", failed?.message);
        response?.status ?: HttpServletResponse.SC_UNAUTHORIZED
        response?.contentType ?: MediaType.APPLICATION_JSON_VALUE
        objectMapper.writeValue(response?.outputStream,
            ResponseMessage("Authentication was unsuccessful", failed?.message)
        )
    }
}

