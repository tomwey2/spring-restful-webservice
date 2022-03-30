package de.tom.demo.springrestfulwebservice.entities.users

import de.tom.demo.springrestfulwebservice.UserNotFoundException
import de.tom.demo.springrestfulwebservice.entities.User
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserService(val db: UserRepository) {
    fun getUsers(): List<User> = db.findUsers()

    fun getUser(id: String): User = db.findByIdOrNull(id) ?: throw  UserNotFoundException(id)

    fun registerUser(name: String, email: String, password: String): User {
        // TODO: encrypt password
        val user = User(null, name, email, password)
        return db.save(user)
    }

    fun loginUser(email: String, password: String): User {
        val user = db.findUserByEmail(email) ?: throw UserNotFoundException(email)
        // TODO: check password
        return user
    }
}