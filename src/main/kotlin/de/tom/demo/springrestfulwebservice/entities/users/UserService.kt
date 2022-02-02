package de.tom.demo.springrestfulwebservice.entities.users

import de.tom.demo.springrestfulwebservice.entities.User
import org.springframework.stereotype.Service

@Service
class UserService(val db: UserRepository) {
    fun getUsers(): List<User> = db.findUsers()
}