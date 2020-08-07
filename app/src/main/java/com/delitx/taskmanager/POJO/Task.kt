package com.delitx.taskmanager.POJO

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task")
data class Task(
    var name:String="",
    var description:String="",
    var childOf:Long=-1,
    var isCompleted:Boolean=false,
    var isHaveChildren:Boolean=false,
    @PrimaryKey(autoGenerate = true)
    var id:Long=0,
    //param to order items correctly
    var goesBefore:Long=0
)