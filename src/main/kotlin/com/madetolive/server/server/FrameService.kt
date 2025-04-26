package com.madetolive.server.server

import com.madetolive.server.entity.FrameEntity
import com.madetolive.server.entity.UserEntity
import com.madetolive.server.repository.FrameRepository
import org.springframework.stereotype.Service

@Service
class FrameService(
    private val frameRepository: FrameRepository
) {

    fun getFramesListByUserId(userId: Long): List<FrameEntity> {
        return frameRepository.findByUserId(userId)
    }

    fun addFrameForUser(user: UserEntity, frame: FrameEntity): FrameEntity {
        frame.user = user // 🔥 THIS is crucial
        return frameRepository.save(frame)
    }

    fun deleteFrameForUser(user: UserEntity, taskId: Long): Boolean {
        val task = frameRepository.findById(taskId).orElse(null) ?: return false
        if (task.user?.id != user.id) return false

        frameRepository.delete(task)
        return true
    }

    fun addFramesForUser(user: UserEntity, frames: List<FrameEntity>): List<FrameEntity> {
        return frameRepository.saveAll(frames)
    }

    fun deleteFramesForUser(user: UserEntity, frameIds: List<Long>): Boolean {
        val frames = frameRepository.findAllById(frameIds)
        val userOwnsAll = frames.all { it?.user?.id == user.id }
        if (!userOwnsAll) return false

        frameRepository.deleteAll(frames)
        return true
    }
}