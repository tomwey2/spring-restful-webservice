package de.tom.demo.springrestfulwebservice.entities.users

import de.tom.demo.springrestfulwebservice.entities.User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(val service: UserService) {

    // GET method to get all users from database
    // http://localhost:8080/users/
    @GetMapping(path = ["/users"])
    fun getAll(): List<User> = service.getUsers()

}