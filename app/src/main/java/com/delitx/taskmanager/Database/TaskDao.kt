package com.delitx.taskmanager.Database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.delitx.taskmanager.POJO.Task

@Dao
interface TaskDao {
    @Query("delete from task")
    fun clearTasks()

    @Query("select * from task where childOf=:id")
    fun getChildrenOf(id: Long): LiveData<List<Task>>

    @Query("select * from task where childOf=:id")
    fun getChildrenValue(id: Long): List<Task>

    @Query("select * from task where task.id=:id")
    fun getTask(id: Long): LiveData<Task>

    @Query("select * from task where goesBefore=:taskId")
    fun getTaskBefore(taskId: Long): Task

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(task: Task)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tasks: List<Task>)

    @Delete
    fun delete(task: Task)

    @Delete
    fun delete(tasks: List<Task>)
}