package com.delitx.taskmanager.Database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.delitx.taskmanager.POJO.Task

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {
    companion object {
        private var mInstance: TaskDatabase? = null
        fun get(application: Application): TaskDatabase {
            if (mInstance == null) {
                mInstance = Room.databaseBuilder(
                    application,
                    TaskDatabase::class.java, "task_database"
                ).fallbackToDestructiveMigration().build()
            }
            return mInstance!!
        }
    }

    abstract fun getTaskDao(): TaskDao
}