package de.tom.demo.taskapp.entities.projects

import de.tom.demo.taskapp.entities.Project
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/projects")
class ProjectController(val service: ProjectService) {

    // GET method to get all projects from database
    // http://localhost:8080/api/projects/
    @GetMapping(path = ["/"])
    @ResponseStatus(HttpStatus.OK)
    fun getAll(): List<Project> = service.getProjects()
}