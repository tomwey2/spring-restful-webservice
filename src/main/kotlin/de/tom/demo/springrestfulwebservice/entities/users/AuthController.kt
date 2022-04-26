package de.tom.demo.springrestfulwebservice.entities.users

import de.tom.demo.springrestfulwebservice.CredentialsNotValidException
import de.tom.demo.springrestfulwebservice.entities.User
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("")
class AuthController(val service: UserService) {

    @PostMapping(path = ["/register"])
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody body: RegisterForm) : String =
        if (body.name.isEmpty() || body.email.isEmpty() || body.password.isEmpty())
            throw CredentialsNotValidException("add credential fields: name, email, password")
        else
            service.registerUser(body.name, body.email, body.password)

    @PostMapping(path = ["/login"])
    @ResponseStatus(HttpStatus.CREATED)
    fun login(@RequestBody body: LoginForm) : User =
        if (body.email.isEmpty() || body.password.isEmpty())
            throw CredentialsNotValidException("add credential fields: email, password")
        else
            service.loginUser(body.email, body.password)

}

data class LoginForm(val email: String, val password: String)
data class RegisterForm(val name: String, val email: String, val password: String)