package de.tom.demo.taskapp.security

import de.tom.demo.taskapp.config.PasswordEncoder
import de.tom.demo.taskapp.entities.users.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.util.*


/**
 * The WebSecurityConfig class is annotated with @EnableWebSecurity to enable Spring Security’s web security
 * support and provide the Spring MVC integration. It also extends WebSecurityConfigurerAdapter and overrides
 * a couple of its methods to set some specifics of the web security configuration.
 * It provides @HttpSecurity configurations to configure cors, csrf, session management, rules for protected
 * resources.
 */
@Configuration
@EnableWebSecurity
class WebSecurityConfig(private val userService: UserService,
                        private val passwordEncoder: PasswordEncoder,
                        private val unauthorizedHandler: AuthEntryPointJwt
): WebSecurityConfigurerAdapter() {


    /**
     * configure(HttpSecurity) method defines which URL paths should be secured and which should not.
     * Specifically, the / and /hello paths are configured to not require any authentication.
     * All other paths must be authenticated.
     */
    override fun configure(http: HttpSecurity?) {
        // this is standard
        val authenticationManager = authenticationManagerBean()
        // extended with two filter for authentication and authorisation management to handle/check the JWT
        val authenticationJwtFilter = UserAuthenticationJwtFilter(authenticationManager)
        val authorizationJwtFilter = UserAuthorizationJwtFilter()

        if (http != null) {

            http.cors()
            http.csrf().disable()
            http.exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

            http.authorizeRequests()
                .antMatchers("/register", "/login", "/refreshtoken")
                    .permitAll()
                .antMatchers("/hello")
                    .permitAll()
                .antMatchers("/api/users", "/api/users/me", "/api/tasks/**")
                    .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .antMatchers("/api/users/**")
                    .hasAnyAuthority("ROLE_ADMIN")
                .anyRequest().authenticated()

            http.addFilterBefore(authenticationJwtFilter, UserAuthenticationJwtFilter::class.java)
            http.addFilterBefore(authorizationJwtFilter, UsernamePasswordAuthenticationFilter::class.java)
        }
    }

    /**
     * The CORS configuration checks against the actual origin, HTTP methods, and headers of a given request.
     * By default the application does not permit any cross-origin requests. If client and server run both at
     * localhost it must be configured explicitly to indicate what should be allowed.
     * Here the local client application at localhost:3000 can request the server.
     */
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = Arrays.asList("http://localhost:3000")
        configuration.allowedMethods = Arrays.asList("GET", "POST", "OPTIONS", "DELETE", "PUT")
        configuration.allowedHeaders = Arrays.asList(
            "Content-Type",
            "content-type",
            "Authorization",
            "x-requested-with",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Headers",
            "x-auth-token",
            "x-app-id",
            "Origin",
            "Accept",
            "X-Requested-With",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        )
        configuration.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(daoAuthenticationProvider())
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    @Bean
    fun daoAuthenticationProvider(): DaoAuthenticationProvider? {
        val provider = DaoAuthenticationProvider()
        provider.setPasswordEncoder(passwordEncoder.bCryptPasswordEncoder())
        provider.setUserDetailsService(userService)
        return provider
    }
}


