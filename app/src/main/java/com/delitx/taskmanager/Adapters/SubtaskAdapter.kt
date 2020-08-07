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
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.delitx.taskmanager.POJO.Task
import com.delitx.taskmanager.R
import com.google.android.material.textfield.TextInputEditText

class SubtaskAdapter(private val mInteraction: TaskAdapter.TaskInteraction) :
    ListAdapter<Task, SubtaskAdapter.SubtaskViewHolder>(object : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.isCompleted == newItem.isCompleted &&
                    oldItem.childOf == newItem.childOf &&
                    oldItem.name == newItem.name &&
                    ((oldItem.description.trim() == "") == (newItem.description.trim() == "")) &&
                    oldItem.isHaveChildren == newItem.isHaveChildren
        }
    }) {
    class SubtaskViewHolder(
        private val v: View,
        private val mInteraction: TaskAdapter.TaskInteraction
    ) : RecyclerView.ViewHolder(v) {
        private val mName: TextInputEditText = v.findViewById(R.id.task_name)
        private val mBody: LinearLayout = v.findViewById(R.id.task_body)
        private val mCheckedState: CheckBox = v.findViewById(R.id.complete_indicator)
        private val mSubtaskIndicator: ImageView = v.findViewById(R.id.subtask_indicator)
        private val mDescriptionIndicator: ImageView = v.findViewById(R.id.description_indicator)
        private val mAddSubtask: TextView = v.findViewById(R.id.add_subtask)
        private var mTask: Task? = null

        init {
            mAddSubtask.visibility = View.GONE
            mBody.setOnClickListener {
                if (mTask != null) {
                    mInteraction.goToTask(mTask!!)
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
            mSubtaskIndicator.visibility = if (task.isHaveChildren) {
                View.VISIBLE
            } else {
                View.GONE
            }
            mDescriptionIndicator.visibility = if (task.description.trim() == "") {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubtaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.task_item, parent, false)
        return SubtaskViewHolder(view, mInteraction)
    }

    override fun onBindViewHolder(holder: SubtaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}