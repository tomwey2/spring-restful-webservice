package de.tom.demo.taskapp.entities.users

import de.tom.demo.taskapp.Constants
import de.tom.demo.taskapp.CredentialsNotValidException
import de.tom.demo.taskapp.UserAuthorizationFailedException
import de.tom.demo.taskapp.entities.RefreshTokenRequestMessage
import de.tom.demo.taskapp.entities.RefreshTokenResponseMessage
import de.tom.demo.taskapp.entities.RegisterForm
import de.tom.demo.taskapp.entities.User
import de.tom.demo.taskapp.security.createToken
import de.tom.demo.taskapp.security.getUsernameByToken
import de.tom.demo.taskapp.security.validateJwtToken
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
    fun currentUserNameSimple(request: HttpServletRequest): Principal? {
        val principal: Principal = request.userPrincipal
        return principal
    }

    @PostMapping(path = [Constants.PATH_REFRESHTOKEN])
    @ResponseStatus(HttpStatus.CREATED)
    fun register(request: HttpServletRequest, @RequestBody body: RefreshTokenRequestMessage) : RefreshTokenResponseMessage {
        if (!validateJwtToken(body.refreshToken)) {
            throw UserAuthorizationFailedException("RefreshToken is not valid or expired")
        }
        val username = getUsernameByToken(body.refreshToken)
        val user = service.loadUserByUsername(username)
        val accessToken = createToken(user, Constants.ACCESS_TOKEN_EXPIRED_IN_MSEC, request.requestURL.toString())
        val refreshToken = createToken(user, Constants.REFRESH_TOKEN_EXPIRED_IN_MSEC, request.requestURL.toString())
        return RefreshTokenResponseMessage(accessToken, refreshToken)
    }
}
