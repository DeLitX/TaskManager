package com.delitx.taskmanager.Adapters

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
    }) {
    class TaskViewHolder(
        private val v: View,
        private val mInteraction: TaskInteraction
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
                    mInteraction.addTask(mTask!!.id, mAdapter.getFirstTask().id) {
                        mAdapter.submitList(listOf(it) + mAdapter.currentList)
                    }
                }
            }
            mName.onFocusChangeListener = View.OnFocusChangeListener { view, b ->
                if (!b && mTask != null) {
                    mTask!!.name = mName.text.toString()
                    mInteraction.saveTask(mTask!!)
                }
            }
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
        return TaskViewHolder(view, mInteraction)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface TaskInteraction {
        fun addTask(parentId: Long, nextId: Long, after: (Task) -> Unit)
        fun saveTask(task: Task)
        fun goToTask(task: Task)
        suspend fun getSubtasksOf(taskId: Long): List<Task>
    }

}