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

    fun addTask(task: Task) {
        CoroutineScope(IO).launch {
            mTaskDao.insert(task)
        }
    }
    fun getTask(id:Long):LiveData<Task>{
        return mTaskDao.getTask(id)
    }
}