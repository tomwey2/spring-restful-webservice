package de.tom.demo.springrestfulwebservice.entities.users

import de.tom.demo.springrestfulwebservice.CredentialsNotValidException
import de.tom.demo.springrestfulwebservice.entities.RegisterForm
import de.tom.demo.springrestfulwebservice.entities.User
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("")
class AuthController(val service: UserService) {

    @PostMapping(path = ["/register"])
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody body: RegisterForm) : User =
        if (body.name.isEmpty() || body.email.isEmpty() || body.password.isEmpty())
            throw CredentialsNotValidException("add credential fields: name, email, password")
        else
            service.registerUser(body.name, body.email, body.password)

}
