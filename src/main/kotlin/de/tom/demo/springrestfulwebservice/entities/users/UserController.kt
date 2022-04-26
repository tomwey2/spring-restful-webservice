package de.tom.demo.springrestfulwebservice.entities.users

import de.tom.demo.springrestfulwebservice.entities.User
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(val service: UserService) {

    // GET method to get all users from database
    // http://localhost:8080/users/
    @GetMapping(path = ["/"])
    @ResponseStatus(HttpStatus.OK)
    fun getAll(): List<User> = service.getUsers()

    /*
    @GetMapping(path = ["/me"])
    @ResponseStatus(HttpStatus.OK)
    fun getMe(@RequestHeader("authorization") header: String): User {
        if (header.isNullOrEmpty() && !header.startsWith("Bearer"))
            throw CredentialsNotValidException("no token found")
        val token = header.split(" ")[1]

    }
     */
}

class ResponseMessage(val message: String)

