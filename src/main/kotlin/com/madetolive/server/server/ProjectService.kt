package com.madetolive.server.server

import com.madetolive.server.entity.ProjectEntity
import com.madetolive.server.entity.TaskEntity
import com.madetolive.server.entity.UserEntity
import com.madetolive.server.repository.ProjectRepository
import com.madetolive.server.repository.TaskRepository
import org.springframework.stereotype.Service

@Service
class ProjectService(
    private val projectRepository: ProjectRepository
) {

    //TODO
    fun getProjectsByUserId(userId: Long): List<ProjectEntity> {
        return projectRepository.findByUserId(userId)
    }

    //TODO
    fun addProjectForUser(user: UserEntity, project: ProjectEntity): ProjectEntity {
        project.user = user // 🔥 THIS is crucial
        return projectRepository.save(project)
    }
}