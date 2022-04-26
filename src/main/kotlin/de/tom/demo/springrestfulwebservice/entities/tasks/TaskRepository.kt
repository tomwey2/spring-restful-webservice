package de.tom.demo.springrestfulwebservice.entities.tasks

import de.tom.demo.springrestfulwebservice.entities.Task
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 *  JPA Repository: to interact with H2 database
 */
@Repository
interface TaskRepository : CrudRepository<Task, String> {
    @Query("select * from tasks")
    fun findTasks(): List<Task>

}
