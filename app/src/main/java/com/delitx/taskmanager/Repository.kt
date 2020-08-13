package com.delitx.taskmanager

import android.app.Application
import androidx.lifecycle.LiveData
import com.delitx.taskmanager.Database.TaskDao
import com.delitx.taskmanager.Database.TaskDatabase
import com.delitx.taskmanager.POJO.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class Repository(private val app: Application) {
    private val mTaskDao: TaskDao

    init {
        val database = TaskDatabase.get(app)
        mTaskDao = database.getTaskDao()
    }

    fun getChildrenOf(id: Long): LiveData<List<Task>> {
        return mTaskDao.getChildrenOf(id)
    }

    suspend fun getChildrenValue(id: Long): List<Task> {
        return mTaskDao.getChildrenValue(id)
    }

    fun insertTask(task: Task?) {
        CoroutineScope(IO).launch {
            if (task != null) {
                mTaskDao.insert(task)
            }
        }
    }

    fun insertTasks(tasks: List<Task>) {
        CoroutineScope(IO).launch {
            mTaskDao.insert(tasks)
        }
    }

    fun getTask(id: Long): LiveData<Task> {
        return mTaskDao.getTask(id)
    }

    suspend fun getTaskBefore(taskId: Long): Task {
        return mTaskDao.getTaskBefore(taskId)
    }

    fun removeTask(task: Task) {
        mTaskDao.delete(task)
        CoroutineScope(IO).launch {
            val removeList= getALLSubtasksOf(task)
            mTaskDao.delete(removeList)
        }
    }
    private suspend fun getALLSubtasksOf(task:Task):List<Task>{
        val list=mTaskDao.getChildrenValue(task.id)
        val result= mutableListOf<Task>()
        result+=list
        for(i in list){
            result+=getALLSubtasksOf(i)
        }
        return result
    }
}