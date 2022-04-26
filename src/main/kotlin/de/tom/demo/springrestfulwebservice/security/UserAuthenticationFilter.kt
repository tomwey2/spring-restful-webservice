package de.tom.demo.springrestfulwebservice.security

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class UserAuthenticationFilter(authenticationManager: AuthenticationManager) :
    UsernamePasswordAuthenticationFilter(authenticationManager) {

    override fun attemptAuthentication(request: HttpServletRequest?, response: HttpServletResponse?): Authentication {
        println(request)
        val username = request?.getParameter("email")
        val password = request?.getParameter("password")
        println("attemptAuthentication: username=$username password=$password")
        val authenticationToken = UsernamePasswordAuthenticationToken(username, password)
        return authenticationManager.authenticate(authenticationToken)
    }

    override fun successfulAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        chain: FilterChain?,
        authentication: Authentication?
    ) {
        println("successfulAuthentication")
        val user: User = authentication?.principal as User
        val accessToken = createToken(user, 10 * 60 * 1000, request?.requestURL.toString())
        val refreshToken = createToken(user, 30 * 60 * 1000, request?.requestURL.toString())

        val tokens = mapOf(
            "access_token" to accessToken,
            "refresh_token" to refreshToken)
        response?.contentType ?: MediaType.APPLICATION_JSON_VALUE
        val objectMapper = ObjectMapper()
        objectMapper.writeValue(response?.outputStream, tokens)
    }
}