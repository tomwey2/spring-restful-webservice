package de.tom.demo.taskapp.entities.users

import de.tom.demo.taskapp.Constants
import de.tom.demo.taskapp.CredentialsNotValidException
import de.tom.demo.taskapp.entities.RegisterForm
import de.tom.demo.taskapp.entities.User
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.servlet.http.HttpServletRequest


@RestController
@RequestMapping("")
class AuthController(val service: UserService) {

    @PostMapping(path = [Constants.PATH_REGISTER])
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody body: RegisterForm) : User =
        if (body.name.isEmpty() || body.email.isEmpty() || body.password.isEmpty())
            throw CredentialsNotValidException("add credential fields: name, email, password")
        else
            service.registerUser(body.name, body.email, body.password)

    @GetMapping(path = ["/username"])
    @ResponseBody
    fun currentUserNameSimple(request: HttpServletRequest): String? {
        val principal: Principal = request.userPrincipal
        return principal.getName()
    }
}
