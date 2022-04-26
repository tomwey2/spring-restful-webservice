package de.tom.demo.springrestfulwebservice.security

import de.tom.demo.springrestfulwebservice.config.PasswordEncoder
import de.tom.demo.springrestfulwebservice.entities.users.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy


/**
 * The WebSecurityConfig class is annotated with @EnableWebSecurity to enable Spring Security’s web security
 * support and provide the Spring MVC integration. It also extends WebSecurityConfigurerAdapter and overrides
 * a couple of its methods to set some specifics of the web security configuration.
 */
@Configuration
@EnableWebSecurity
class WebSecurityConfig(private val userService: UserService,
                        private val passwordEncoder: PasswordEncoder
): WebSecurityConfigurerAdapter() {


    /**
     * The configure(HttpSecurity) method defines which URL paths should be secured and which should not.
     * Specifically, the / and /hello paths are configured to not require any authentication.
     * All other paths must be authenticated.
     */
    override fun configure(http: HttpSecurity?) {
        val authenticationManager = authenticationManagerBean()
        val authenticationFilter = UserAuthenticationFilter(authenticationManager)
        //userAuthenticationFilter.setFilterProcessesUrl("/api/users/login")
        if (http != null) {

            http.csrf().disable()
            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

            http.authorizeRequests()
                .antMatchers("/", "/hello", "/api/tasks/**",
                    "/api/users/",
                    "/register", "/login").permitAll()
                .antMatchers().hasRole("User")
                .antMatchers().hasRole("Admin")
                .anyRequest().authenticated()

            http.addFilter(authenticationFilter)

        };
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    fun daoAuthenticationProvider(): DaoAuthenticationProvider? {
        val provider = DaoAuthenticationProvider()
        provider.setPasswordEncoder(passwordEncoder.bCryptPasswordEncoder())
        provider.setUserDetailsService(userService)
        return provider
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
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
