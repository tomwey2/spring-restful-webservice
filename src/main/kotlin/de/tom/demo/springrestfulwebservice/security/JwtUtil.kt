package de.tom.demo.springrestfulwebservice.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import java.util.*

val algorithm: Algorithm? = Algorithm.HMAC256("secret")

fun createToken(user: User, expired: Long, url: String) =
    JWT.create()
        .withSubject(user.username)
        .withExpiresAt(Date(System.currentTimeMillis() + expired))
        .withIssuer(url)
        .withClaim("roles", user.authorities.map(GrantedAuthority::getAuthority))
        .sign(algorithm)


fun verifyToken(token: String): UsernamePasswordAuthenticationToken {
    val verifier: JWTVerifier = JWT.require(algorithm).build()
    val decodedJwt: DecodedJWT = verifier.verify(token)
    val username = decodedJwt.subject
    val roles = decodedJwt.getClaim("roles").asArray(String.javaClass)
    val authorities: kotlin.collections.Collection<SimpleGrantedAuthority> =
        Array(roles.size) { i -> SimpleGrantedAuthority(roles[i].toString()) }.toList()
    return UsernamePasswordAuthenticationToken(username, null, authorities)
}

