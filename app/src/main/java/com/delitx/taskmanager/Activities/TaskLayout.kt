package com.delitx.taskmanager.Activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.CheckBox
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delitx.taskmanager.Adapters.TaskAdapter
import com.delitx.taskmanager.POJO.Task
import com.delitx.taskmanager.R
import com.delitx.taskmanager.ViewModels.MainViewModel
import com.google.android.material.textfield.TextInputEditText

class TaskLayout : BaseActivity() {
    private var mTaskId: Long = 0
    private var mTask: Task? = null
    private lateinit var mRecycler: RecyclerView
    private lateinit var mAddTask: TextView
    private lateinit var mName: TextInputEditText
    private lateinit var mDescription: TextInputEditText
    private lateinit var mCompletedState: CheckBox
    private val mAdapter = TaskAdapter(this)
    private var mIsGetFromDB = true
    private var mIsGetMainTaskFromDB = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_layout)
        mViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mTaskId = intent.getLongExtra(getString(R.string.extra_task_id), 0)
        bindActivity()
        mViewModel.getTask(mTaskId).observe(this, Observer {
            if (mIsGetMainTaskFromDB) {
                mTask = it
                setMainTask(it)
                mIsGetMainTaskFromDB=false
            }
        })
    }

    override fun onPause() {
        super.onPause()
        mIsGetFromDB = true
        mIsGetMainTaskFromDB = true
    }

    private fun setMainTask(task: Task) {
        mName.setText(task.name)
        mDescription.setText(task.description)
        mCompletedState.isChecked = task.isCompleted
    }

    private fun bindActivity() {
        setUpRecycler()
        mAddTask = findViewById(R.id.add_subtask)
        mAddTask.setOnClickListener {
            addTask(mTaskId, mAdapter.getList()[0].id){
                mAdapter.submitList(listOf(it)+mAdapter.currentList)
            }
        }
        mName = findViewById(R.id.task_name)
        mDescription = findViewById(R.id.task_description)
        mCompletedState = findViewById(R.id.complete_indicator)
        mCompletedState.setOnCheckedChangeListener { compoundButton, b ->
            if (mTask != null) {
                mTask!!.isCompleted = b
                mViewModel.saveTask(mTask!!)
            }
        }
        mName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (mTask != null) {
                    if (mTask != null) {
                        mViewModel.saveTask(mTask!!)
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                mTask?.name = p0.toString()
            }

        })
        mDescription.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (mTask != null) {
                    if (mTask != null) {
                        mViewModel.saveTask(mTask!!)
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                mTask?.description = p0.toString()
            }

        })
    }

    private fun setUpRecycler() {
        mRecycler = findViewById(R.id.tasks_recycler)
        mRecycler.layoutManager = LinearLayoutManager(this)
        mRecycler.adapter = mAdapter
        mViewModel.getOrderedChildrenOf(mTaskId).observe(this, Observer {
            if (mIsGetFromDB) {
                mAdapter.setList(it)
                mIsGetFromDB = false
            }
        })
    }
}
