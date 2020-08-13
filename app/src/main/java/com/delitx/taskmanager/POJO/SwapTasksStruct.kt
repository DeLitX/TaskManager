package com.delitx.taskmanager.POJO

data class SwapTasksStruct(
    var task1: Task,
    var task2: Task,
    var previousTask1: Task?,
    var previousTask2: Task?
)