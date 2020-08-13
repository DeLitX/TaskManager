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
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.delitx.taskmanager.Activities.BaseActivity
import com.delitx.taskmanager.POJO.Task
import com.delitx.taskmanager.R
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class TaskAdapter(private val mInteraction: TaskInteraction) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    var currentList = mutableListOf<Task>()

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
                        mAdapter.setList(listOf(it) + mAdapter.currentList)
                    }
                }
            }
            mName.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                    if (mTask != null) {
                        if (mTask!!.name != p0.toString()) {
                            mInteraction.saveTask(mTask!!)
                        }
                    }
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (mTask?.name != p0.toString()) {
                        mTask?.name = p0.toString()
                    }
                }

            })
            mCheckedState.setOnCheckedChangeListener { compoundButton, b ->
                if (mTask != null) {
                    mTask!!.isCompleted = b
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
                    mAdapter.setList(list)
                }
            }
        }
    }

    fun setList(tasks: List<Task>) {
        currentList = tasks.toMutableList()
        notifyDataSetChanged()
    }

    fun updateSubtasksOf(id: Long) {
        notifyItemChanged(currentList.indexOf(currentList.findLast { it.id == id }))
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view, mInteraction)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    interface TaskInteraction {
        fun addTask(parentId: Long, nextId: Long, after: (Task) -> Unit)
        fun saveTask(task: Task)
        fun goToTask(task: Task)
        fun removeTask(task: Task)
        fun swapTasks(task1: Task, task2: Task)
        suspend fun getSubtasksOf(taskId: Long): List<Task>
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition..toPosition-1) {
                Collections.swap(currentList, i, i+1)
            }
        } else {
            for (i in fromPosition..toPosition+1) {
                Collections.swap(currentList, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition,toPosition)
    }

    fun onItemDelete(position: Int) {
        mInteraction.removeTask(currentList[position])
        currentList.removeAt(position)
        notifyItemRemoved(position)
    }

}