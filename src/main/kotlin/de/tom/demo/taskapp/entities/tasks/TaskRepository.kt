package de.tom.demo.taskapp.entities.tasks

import de.tom.demo.taskapp.entities.Task
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

/**
 *  JPA Repository: to interact with H2 database
 */
@Repository
interface TaskRepository : MongoRepository<Task, String>
