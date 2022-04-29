package de.tom.demo.taskapp.security

import com.fasterxml.jackson.databind.ObjectMapper
import de.tom.demo.taskapp.entities.ResponseMessage
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Catch the AuthenticationException and give back an error message.
 */
@Component
class AuthEntryPointJwt : AuthenticationEntryPoint {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val objectMapper = ObjectMapper()

    override fun commence(request: HttpServletRequest?, response: HttpServletResponse?,
        authException: AuthenticationException?
    ) {
        log.error("Unauthorized error. Message: {}", authException?.message)
        response?.status ?: HttpServletResponse.SC_UNAUTHORIZED
        response?.contentType ?: MediaType.APPLICATION_JSON_VALUE
        objectMapper.writeValue(response?.outputStream,
            ResponseMessage("Unauthorized error", authException?.message))
    }
}
