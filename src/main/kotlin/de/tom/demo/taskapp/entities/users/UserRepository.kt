package de.tom.demo.taskapp.entities.users

import de.tom.demo.taskapp.entities.User
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository  : MongoRepository<User, String> {
    @Query("{email: '?0'}")
    fun findUserByEmail(email: String): User?
}