package com.delitx.taskmanager.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.delitx.taskmanager.Algorithms
import com.delitx.taskmanager.POJO.Task
import com.delitx.taskmanager.Repository

class MainViewModel(private val app: Application) : AndroidViewModel(app) {
    private val mRepository: Repository = Repository(app)

    fun getOrderedChildrenOf(id: Long): LiveData<List<Task>> {
        return Transformations.map(mRepository.getChildrenOf(id)) { tasks ->
            Algorithms().orderTasks(
                tasks.toMutableList()
            )
        }
    }

    suspend fun getOrderedChildrenValue(id: Long): List<Task> {
        return Algorithms().orderTasks(mRepository.getChildrenValue(id).toMutableList())
    }

    fun saveTask(task: Task) {
        mRepository.addTask(task)
    }

    fun getTask(id: Long): LiveData<Task> {
        return mRepository.getTask(id)
    }

}