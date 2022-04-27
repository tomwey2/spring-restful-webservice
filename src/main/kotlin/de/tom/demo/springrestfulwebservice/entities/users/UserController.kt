package de.tom.demo.springrestfulwebservice.entities.users

import de.tom.demo.springrestfulwebservice.entities.User
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(val service: UserService) {

    // GET method to get all users from database
    // http://localhost:8080/users/
    @GetMapping(path = ["/"])
    @ResponseStatus(HttpStatus.OK)
    fun getAll(): List<User> = service.getUsers()

    @GetMapping(path = ["/me"])
    @ResponseStatus(HttpStatus.OK)
    fun getMe(): UserDetails {
        val principal: String = SecurityContextHolder.getContext().authentication.principal.toString()
        return service.loadUserByUsername(principal)
    }

}
