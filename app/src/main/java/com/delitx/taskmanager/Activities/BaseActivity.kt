package com.delitx.taskmanager.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.delitx.taskmanager.Adapters.TaskAdapter
import com.delitx.taskmanager.Dialogs.AddTaskDialog
import com.delitx.taskmanager.POJO.Task
import com.delitx.taskmanager.R
import com.delitx.taskmanager.ViewModels.MainViewModel

abstract class BaseActivity : AppCompatActivity(), TaskAdapter.TaskInteraction {
    internal lateinit var mViewModel: MainViewModel
    val GO_TO_TASK_REQUEST_CODE = 2345
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun addTask(parentId: Long, nextId: Long, after: (Task) -> Unit) {
        val bundle = Bundle()
        bundle.putLong(getString(R.string.extra_parent_id), parentId)
        bundle.putLong(getString(R.string.extra_next_id), nextId)
        val addDialog = AddTaskDialog(object : AddTaskDialog.AddTaskInteraction {
            override fun saveTask(task: Task) {
                mViewModel.saveTask(task)
                after(task)
            }

        })
        addDialog.arguments = bundle
        addDialog.show(supportFragmentManager, getString(R.string.tag_add_task))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GO_TO_TASK_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    updateSubtasksOf(data.data.toString().toLong())
                }
            }
        }
    }

    override suspend fun getSubtasksOf(taskId: Long): List<Task> {
        return mViewModel.getOrderedChildrenValue(taskId)
    }

    override fun saveTask(task: Task) {
        mViewModel.saveTask(task)
    }

    override fun goToTask(task: Task) {
        val intent = Intent(this, TaskLayout::class.java)
        intent.putExtra(getString(R.string.extra_task_id), task.id)
        startActivityForResult(intent, GO_TO_TASK_REQUEST_CODE)
    }

    abstract fun updateSubtasksOf(id: Long)
}