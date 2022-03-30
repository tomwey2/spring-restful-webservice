package de.tom.demo.springrestfulwebservice.entities.users

import de.tom.demo.springrestfulwebservice.entities.Task
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

    @PostMapping(path = ["/"])
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody user: User) : ResponseMessage = ResponseMessage("Register user: ${user.name}");

    @PostMapping(path = ["/login"])
    @ResponseStatus(HttpStatus.CREATED)
    fun login(@RequestBody data: LoginForm) : String = "Login user: ${data.email}";
}

class ResponseMessage(val message: String)

class LoginForm(val email: String, val password: String)