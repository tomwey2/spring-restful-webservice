package de.tom.demo.taskapp.entities.users

import de.tom.demo.taskapp.Constants
import de.tom.demo.taskapp.CredentialsNotValidException
import de.tom.demo.taskapp.UserAlreadyExistException
import de.tom.demo.taskapp.UserNotFoundException
import de.tom.demo.taskapp.config.PasswordEncoder
import de.tom.demo.taskapp.entities.User
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserService(private val db: UserRepository, private val passwordEncoder: PasswordEncoder): UserDetailsService {
    private val encoder = passwordEncoder.bCryptPasswordEncoder()
    private val log = LoggerFactory.getLogger(this::class.java)

    fun getUsers(): List<User> = db.findAll()

    fun getUser(id: String): User = db.findByIdOrNull(id) ?: throw UserNotFoundException(id)

    fun getUserByEmail(email: String): User = db.findUserByEmail(email) ?: throw UserNotFoundException(email)
    fun registerUser(name: String, email: String, password: String, roles: List<String> = listOf("ROLE_USER")): User {
        log.info("Register user $name with roles $roles")
        // check if user exists
        if (db.findUserByEmail(email) == null) {
            // hash the password, because here is it open
            val hashedPassword = encoder.encode(password)
            // create the user and save it in the database
            return db.save(User(null, name, hashedPassword, email, roles))
        } else
            throw UserAlreadyExistException(email)
    }

    /**
     * Authenticate the user. This function is used by the spring boot security system.
     */
    override fun loadUserByUsername(username: String?): UserDetails {
        if (username == null || username.isEmpty()) {
            throw CredentialsNotValidException("Username (email) is required")
        }
        log.debug("Load user ($username) from database")
        val user = db.findUserByEmail(username) ?: throw UserNotFoundException(username)

        // returns a user detail object
        // TODO: save the Boolean values and authority info in database and fill them into the object
        return org.springframework.security.core.userdetails.User(
            user.email,
            user.password,
            true,
            true,
            true,
            true,
            user.roles.map{SimpleGrantedAuthority(it) }
        )
    }

    fun getLoggedInUser(): User {
        val principal = SecurityContextHolder.getContext().authentication.principal.toString()
        return getUserByEmail(principal)
    }

}