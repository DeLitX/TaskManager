package com.delitx.taskmanager.Adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.delitx.taskmanager.POJO.Task
import com.delitx.taskmanager.R
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskAdapter(private val mInteraction: TaskInteraction) :
    ListAdapter<Task, TaskAdapter.TaskViewHolder>(object :
        DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.isCompleted == newItem.isCompleted &&
                    oldItem.childOf == newItem.childOf &&
                    oldItem.name == newItem.name &&
                    ((oldItem.description.trim() == "") == (newItem.description.trim() == ""))
        }
    }), TaskAdapterInteraction {
    class TaskViewHolder(
        private val v: View,
        private val mInteraction: TaskInteraction,
        private val mAdapterInteraction: TaskAdapterInteraction
    ) :
        RecyclerView.ViewHolder(v) {
        private val mName: TextInputEditText = v.findViewById(R.id.task_name)
        private val mBody: LinearLayout = v.findViewById(R.id.task_body)
        private val mCheckedState: CheckBox = v.findViewById(R.id.complete_indicator)
        private val mAddSubtask: TextView = v.findViewById(R.id.add_subtask)
        private val mRecycler: RecyclerView = v.findViewById(R.id.subtasks_recycler)
        private val mDescriptionIndicator: ImageView = v.findViewById(R.id.description_indicator)
        private val mAdapter: SubtaskAdapter = SubtaskAdapter(mInteraction)
        private var mTask: Task? = null

        init {
            mRecycler.layoutManager = LinearLayoutManager(v.context)
            mRecycler.adapter = mAdapter
            mBody.setOnClickListener {
                if (mTask != null) {
                    mInteraction.goToTask(mTask!!)
                }
            }
            mAddSubtask.setOnClickListener {
                if (mTask != null) {
                    mInteraction.addTask(mTask!!.id)
                }
            }
            mName.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                    if (mTask != null) {
                        mInteraction.saveTask(mTask!!)
                    }
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    mTask?.name = p0.toString()
                }

            })
        }

        fun bind(task: Task) {
            mTask = task
            mName.setText(task.name)
            mCheckedState.isChecked = task.isCompleted
            mDescriptionIndicator.visibility = if (task.description.trim() == "") {
                View.GONE
            } else {
                View.VISIBLE
            }
            CoroutineScope(Default).launch {
                val list = mInteraction.getSubtasksOf(task.id)
                withContext(Main) {
                    mAdapter.submitList(list)
                }
            }
        }
    }

    fun getList(): List<Task> {
        return currentList
    }

    fun setList(tasks: List<Task>) {
        submitList(tasks)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view, mInteraction, this)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface TaskInteraction {
        fun addTask(parentId: Long)
        fun saveTask(task: Task)
        fun goToTask(task: Task)
        suspend fun getSubtasksOf(taskId: Long): List<Task>
    }

    override fun getFirstTask(): Task {
        return currentList[0]
    }
}

interface TaskAdapterInteraction {
    fun getFirstTask(): Task
}