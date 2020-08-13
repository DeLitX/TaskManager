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
    RecyclerView.Adapter<SubtaskAdapter.SubtaskViewHolder>() {
    var currentList = listOf<Task>()

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
            mCheckedState.setOnCheckedChangeListener { compoundButton, b ->
                if (mTask != null) {
                    mTask!!.isCompleted = b
                    mInteraction.saveTask(mTask!!)
                }
            }
            mAddSubtask.visibility = View.GONE
            mBody.setOnClickListener {
                if (mTask != null) {
                    mInteraction.goToTask(mTask!!)
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

    fun setList(list: List<Task>) {
        currentList = list
        notifyDataSetChanged()
    }

    fun getFirstTask(): Task {
        return if (currentList.isNotEmpty()) currentList[0] else Task(id = 0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubtaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.task_item, parent, false)
        return SubtaskViewHolder(view, mInteraction)
    }

    override fun onBindViewHolder(holder: SubtaskViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun getItemCount(): Int {
        return currentList.size
    }
}