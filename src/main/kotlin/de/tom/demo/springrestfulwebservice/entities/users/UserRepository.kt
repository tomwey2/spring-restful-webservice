package de.tom.demo.springrestfulwebservice.entities.users

import de.tom.demo.springrestfulwebservice.entities.Task
import de.tom.demo.springrestfulwebservice.entities.User
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository  : CrudRepository<User, String> {
    @Query("select * from users")
    fun findUsers(): List<User>

    @Query("select * from users where email = ?1")
    fun findUserByEmail(email: String): User?
}