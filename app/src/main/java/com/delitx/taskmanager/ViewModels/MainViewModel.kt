package com.delitx.taskmanager.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.delitx.taskmanager.Algorithms
import com.delitx.taskmanager.POJO.SwapTasksStruct
import com.delitx.taskmanager.POJO.Task
import com.delitx.taskmanager.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

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
        mRepository.insertTask(task)
    }

    fun getTask(id: Long): LiveData<Task> {
        return mRepository.getTask(id)
    }

    fun removeTask(task: Task) {
        CoroutineScope(IO).launch {
            val previousTask:Task? = mRepository.getTaskBefore(task.id)
            previousTask?.goesBefore = task.goesBefore
            mRepository.removeTask(task)
            mRepository.insertTask(previousTask)
        }
    }

    fun swapTasks(task1: Task, task2: Task) {
        CoroutineScope(IO).launch {
            val previousTask1:Task? = mRepository.getTaskBefore(task1.id)
            val previousTask2:Task? = mRepository.getTaskBefore(task2.id)
            val bunch =Algorithms().swapTasks(SwapTasksStruct( task1,task2,previousTask1,previousTask2))
            mRepository.insertTask(bunch.task1)
            mRepository.insertTask(bunch.task2)
            mRepository.insertTask(bunch.previousTask1)
            mRepository.insertTask(bunch.previousTask2)
        }
    }

}