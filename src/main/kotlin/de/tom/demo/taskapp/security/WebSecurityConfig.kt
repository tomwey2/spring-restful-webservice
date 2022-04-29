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


/**
 * The WebSecurityConfig class is annotated with @EnableWebSecurity to enable Spring Security’s web security
 * support and provide the Spring MVC integration. It also extends WebSecurityConfigurerAdapter and overrides
 * a couple of its methods to set some specifics of the web security configuration.
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
        val authenticationManager = authenticationManagerBean()
        val authenticationJwtFilter = UserAuthenticationJwtFilter(authenticationManager)
        val authorizationJwtFilter = UserAuthorizationJwtFilter()

        if (http != null) {

            http.csrf().disable()
            http.exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

            http.authorizeRequests()
                .antMatchers("/register", "/login").permitAll()
                .antMatchers("/hello", "/api/tasks/**").permitAll()
                .antMatchers("/api/users/me").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .antMatchers("/api/users/").hasAnyAuthority("ROLE_ADMIN")
                .anyRequest().authenticated()

            http.addFilterBefore(authenticationJwtFilter, UserAuthenticationJwtFilter::class.java)
            http.addFilterBefore(authorizationJwtFilter, UsernamePasswordAuthenticationFilter::class.java)
        }
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

// http
// .authorizeRequests()
// .antMatchers(
// "/", "/hello", "/api/tasks/**",
// "/api/users/",
// "/api/users/register", "/api/users/login"
// ).permitAll()
// .antMatchers(
//
// ).hasRole("User")
// .antMatchers(
//
// ).hasRole("Admin")
// .anyRequest()
// .authenticated()
// .and()
// .formLogin()
// .and()
// .logout()
// .logoutSuccessUrl("/")
//
