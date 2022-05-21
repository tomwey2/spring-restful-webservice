package de.tom.demo.taskapp.entities.users

import de.tom.demo.taskapp.Constants
import de.tom.demo.taskapp.entities.User
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(Constants.PATH_USERS)
class UserController(val service: UserService) {

    // GET method to get all users from database
    // http://localhost:8080/users/
    @GetMapping(path = [""])
    @ResponseStatus(HttpStatus.OK)
    fun getAll(): List<User> = service.getUsers()

    @GetMapping(path = ["/{id}"])
    @ResponseStatus(HttpStatus.OK)
    fun getUserById(@PathVariable id: String): User = service.getUser(id)

    @GetMapping(path = ["/me"])
    @ResponseStatus(HttpStatus.OK)
    fun getMe(): UserDetails {
        val principal: String = SecurityContextHolder.getContext().authentication.principal.toString()
        return service.loadUserByUsername(principal)
    }

}
