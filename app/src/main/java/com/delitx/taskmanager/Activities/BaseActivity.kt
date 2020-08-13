package com.delitx.taskmanager.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delitx.taskmanager.Adapters.TaskAdapter
import com.delitx.taskmanager.Dialogs.AddTaskDialog
import com.delitx.taskmanager.POJO.Task
import com.delitx.taskmanager.R
import com.delitx.taskmanager.ViewModels.MainViewModel

abstract class BaseActivity : AppCompatActivity(), TaskAdapter.TaskInteraction {
    internal lateinit var mViewModel: MainViewModel
    internal lateinit var mRecycler: RecyclerView
    internal val mAdapter = TaskAdapter(this)
    internal var mTaskId: Long = -1
    internal var mIsGetFromDB = true
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
                mIsGetFromDB=true
                //after(task)
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

    override fun removeTask(task: Task) {
        mViewModel.removeTask(task)
    }

    override fun swapTasks(task1: Task, task2: Task) {
        mViewModel.swapTasks(task1, task2)
    }

    override suspend fun getSubtasksOf(taskId: Long): List<Task> {
        return mViewModel.getOrderedChildrenValue(taskId)
    }

    override fun saveTask(task: Task) {
        mViewModel.saveTask(task)
        mIsGetFromDB = true
    }

    override fun goToTask(task: Task) {
        val intent = Intent(this, TaskLayout::class.java)
        intent.putExtra(getString(R.string.extra_task_id), task.id)
        startActivityForResult(intent, GO_TO_TASK_REQUEST_CODE)
    }

    internal fun setUpRecycler() {
        mRecycler = findViewById(R.id.tasks_recycler)
        mRecycler.layoutManager = LinearLayoutManager(this)
        mRecycler.adapter = mAdapter
        mViewModel.getOrderedChildrenOf(mTaskId).observe(this, Observer {
            if (mIsGetFromDB) {
                mAdapter.setList(it)
                mIsGetFromDB = false
            }
        })
        val swipesHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return makeMovementFlags(
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                    ItemTouchHelper.START or ItemTouchHelper.END
                )
            }

            override fun isLongPressDragEnabled(): Boolean {
                return true
            }

            override fun isItemViewSwipeEnabled(): Boolean {
                return true
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                mAdapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
                return true
            }

            override fun onMoved(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                fromPos: Int,
                target: RecyclerView.ViewHolder,
                toPos: Int,
                x: Int,
                y: Int
            ) {
                mViewModel.swapTasks(
                    mAdapter.currentList[fromPos],
                    mAdapter.currentList[toPos]
                )
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                mViewModel.removeTask(mAdapter.currentList[viewHolder.adapterPosition])
                mAdapter.onItemDelete(viewHolder.adapterPosition)
            }

        })
        swipesHelper.attachToRecyclerView(mRecycler)
    }

    abstract fun updateSubtasksOf(id: Long)
}