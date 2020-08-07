package com.delitx.taskmanager

import com.delitx.taskmanager.POJO.Task

class Algorithms {
    fun orderTasks(tasks: MutableList<Task>): List<Task> {
        var result = mutableListOf<Task>()
        //this indexMinus is for decreasing next index after deleting item in list
        //unfortunately, index of for loop is val
        var indexMinus = 0
        for (k in 0 until tasks.size) {
            //decreasing index by number of deleted items
            var i = k - indexMinus
            if (tasks.isNotEmpty()) {
                val temp = mutableListOf<Task>()
                temp.add(tasks[i])
                var nextId = tasks[i].goesBefore
                tasks.removeAt(i)
                indexMinus++

                //in while loop we search all items which located after item we added in temp list
                //and add them to temp list

                while (nextId > 0) {
                    val index = tasks.indexOfFirst { it.id == nextId }
                    if (index == -1) {
                        break
                    }
                    temp.add(tasks[index])
                    nextId = tasks[index].goesBefore
                    tasks.removeAt(index)
                }
                //we add temp to result because during first step we reach the end
                //and during next steps we reach exactly the start of the result list
                result = (temp+result).toMutableList()
            }
        }
        return result
    }
}