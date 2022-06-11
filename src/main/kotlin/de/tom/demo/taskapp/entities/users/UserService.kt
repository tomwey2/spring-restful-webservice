package de.tom.demo.taskapp.entities.users

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

@Service
class UserService(private val db: UserRepository, private val passwordEncoder: PasswordEncoder): UserDetailsService {
    private val encoder = passwordEncoder.bCryptPasswordEncoder()
    private val log = LoggerFactory.getLogger(this::class.java)

    fun getUsers(): List<User> = db.findAll()

    fun getUserById(id: String): User = db.findByIdOrNull(id) ?: throw UserNotFoundException(id)

    fun getUserByUsername(username: String): User {
        log.debug("Load user ($username) from database")
        return (if (username.contains("@")) db.findUserByEmail(username) else db.findUserByUsername(username))
            ?: throw UserNotFoundException(username)
    }

    fun registerUser(name: String, username: String, email: String, password: String,
                     roles: List<String> = listOf("ROLE_USER")): User {
        log.info("Register user $username with roles $roles")
        // check if user exists
        if (db.findUserByUsername(username) == null) {
            // hash the password, because here is it open
            val hashedPassword = encoder.encode(password)
            // create the user and save it in the database
            return db.save(User(null, name, username, hashedPassword, email, roles))
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
        val user = getUserByUsername(username)

        // returns a user detail object
        // TODO: save the Boolean values and authority info in database and fill them into the object
        return org.springframework.security.core.userdetails.User(
            user.username,
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
        return getUserByUsername(principal)
    }

}