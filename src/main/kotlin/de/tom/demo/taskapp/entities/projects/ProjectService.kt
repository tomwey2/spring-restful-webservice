package de.tom.demo.taskapp.entities.projects

import de.tom.demo.taskapp.ProjectNotFoundException
import de.tom.demo.taskapp.entities.Project
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ProjectService(val db: ProjectRepository) {
    fun getProjects(): List<Project> = db.findAll()

    fun getProject(id: String): Project = db.findByIdOrNull(id) ?: throw  ProjectNotFoundException(id)

    fun getProjectByName(name: String) = db.findProjectByName(name) ?: throw  ProjectNotFoundException(name)

    fun addProject(project: Project): Project = db.save(project)
}