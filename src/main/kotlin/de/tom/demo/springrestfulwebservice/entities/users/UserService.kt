package de.tom.demo.springrestfulwebservice.entities.users

import de.tom.demo.springrestfulwebservice.CredentialsNotValidException
import de.tom.demo.springrestfulwebservice.UserAlreadyExistException
import de.tom.demo.springrestfulwebservice.UserNotFoundException
import de.tom.demo.springrestfulwebservice.config.PasswordEncoder
import de.tom.demo.springrestfulwebservice.entities.User
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserService(val db: UserRepository, val passwordEncoder: PasswordEncoder): UserDetailsService {
    private val encoder = passwordEncoder.bCryptPasswordEncoder()

    fun getUsers(): List<User> = db.findUsers()

    fun getUser(id: String): User = db.findByIdOrNull(id) ?: throw  UserNotFoundException(id)

    fun registerUser(name: String, email: String, password: String): String {
        // check if user exists
        if (db.findUserByEmail(email) == null) {
            // hash the password, because here is it open
            val hashedPassword = encoder.encode(password)
            // create the user and save it in the database
            val user = db.save(User(null, name, email, hashedPassword))
            // create a jwt and send it back to the client
            return generateToken(user.id)
        } else
            throw UserAlreadyExistException(email)
    }

    fun loginUser(email: String, password: String): User {
        println("loginUser: email=$email password=$password")
        val user = db.findUserByEmail(email) ?: throw UserNotFoundException(email)
        return if (encoder.matches(password, user.password))
            user
        else
            throw CredentialsNotValidException("Password is not correct")
    }

    fun generateToken(id: String?): String {
        return "123"
    }

    /**
     * Authenticate the user. This function is used by the spring boot security system.
     */
    override fun loadUserByUsername(username: String?): UserDetails {
        println("load user: $username")
        val email = username ?: throw CredentialsNotValidException("email is required")
        val user = db.findUserByEmail(email) ?: throw UserNotFoundException(email)
        return org.springframework.security.core.userdetails.User(
            user.email,
            user.password,
            true,
            true,
            true,
            true,
            listOf(SimpleGrantedAuthority("USER"))
        );
    }

}