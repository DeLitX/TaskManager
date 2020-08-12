package com.delitx.taskmanager.Activities

import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delitx.taskmanager.Adapters.TaskAdapter
import com.delitx.taskmanager.R

class MainActivity : BaseActivity() {
    private lateinit var mRecycler: RecyclerView
    private lateinit var mAddTask: TextView
    private val mAdapter = TaskAdapter(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindActivity()
    }

    override fun updateSubtasksOf(id: Long) {
        mAdapter.updateSubtasksOf(id)
    }

    private fun bindActivity() {
        setUpRecycler()
        mAddTask = findViewById(R.id.add_task)
        mAddTask.setOnClickListener {
            addTask(-1, if (mAdapter.currentList.isNotEmpty()) mAdapter.currentList[0].id else 0) {
                mAdapter.setList(listOf(it) + mAdapter.currentList)
            }
        }
    }

    private fun setUpRecycler() {
        mRecycler = findViewById(R.id.tasks_recycler)
        mRecycler.layoutManager = LinearLayoutManager(this)
        mRecycler.adapter = mAdapter
        mViewModel.getOrderedChildrenOf(-1).observe(this, Observer {
            mAdapter.setList(it)
        })
    }
}
