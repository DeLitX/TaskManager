package com.delitx.taskmanager.Activities

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.delitx.taskmanager.Adapters.TaskAdapter
import com.delitx.taskmanager.Dialogs.AddTaskDialog
import com.delitx.taskmanager.POJO.Task
import com.delitx.taskmanager.R
import com.delitx.taskmanager.ViewModels.MainViewModel

open class BaseActivity : AppCompatActivity(), TaskAdapter.TaskInteraction,
    AddTaskDialog.AddTaskInteraction {
    internal lateinit var mViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_main)
    }

    override fun addTask(parentId: Long) {
        val bundle = Bundle()
        bundle.putLong(getString(R.string.extra_parent_id), parentId)
        val addDialog = AddTaskDialog(this)
        addDialog.arguments=bundle
        addDialog.show(supportFragmentManager, getString(R.string.tag_add_task))
    }

    override suspend fun getSubtasksOf(taskId: Long):List<Task> {
        return mViewModel.getOrderedChildrenValue(taskId)
    }

    override fun saveTask(task: Task) {
        mViewModel.saveTask(task)
    }

    override fun goToTask(task: Task) {
        val intent= Intent(this,TaskLayout::class.java)
        intent.putExtra(getString(R.string.extra_task_id),task.id)
        startActivity(intent)
    }
}