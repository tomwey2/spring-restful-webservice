package de.tom.demo.taskapp.entities.projects

import de.tom.demo.taskapp.entities.Project
import de.tom.demo.taskapp.entities.User
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectRepository : MongoRepository<Project, String> {
    @Query("{name: '?0'}")
    fun findProjectByName(name: String): Project?
}