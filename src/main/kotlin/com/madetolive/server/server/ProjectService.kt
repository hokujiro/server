package com.madetolive.server.server

import com.madetolive.server.entity.ProjectEntity
import com.madetolive.server.entity.UserEntity
import com.madetolive.server.repository.ProjectRepository
import com.madetolive.server.repository.TaskRepository
import org.springframework.stereotype.Service

@Service
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val taskRepository: TaskRepository,
) {

    fun getProjectsByUserId(userId: Long): List<ProjectEntity> {
        return projectRepository.findByUserId(userId)
    }

    fun addProjectForUser(user: UserEntity, project: ProjectEntity): ProjectEntity {
        project.user = user // 🔥 THIS is crucial
        return projectRepository.save(project)
    }

    fun getProjectById(id: Long): ProjectEntity {
        return projectRepository.findById(id)
            .orElseThrow { NoSuchElementException("Project with ID $id not found") }
    }

    fun deleteProjectForUser(user: UserEntity, projectId: Long): Boolean {
        val project = projectRepository.findById(projectId).orElse(null) ?: return false
        if (project.user.id != user.id) return false

        project.tasks?.forEach {
            it.project = null
            taskRepository.save(it) // Persist the change
        }

        projectRepository.delete(project)
        return true
    }
}