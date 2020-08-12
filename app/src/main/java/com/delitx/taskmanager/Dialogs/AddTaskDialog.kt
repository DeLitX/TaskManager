package com.delitx.taskmanager.Dialogs

import android.widget.Toast
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.delitx.taskmanager.POJO.Task
import com.delitx.taskmanager.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText

class AddTaskDialog(private val mInteraction: AddTaskInteraction) : BottomSheetDialogFragment() {
    private lateinit var v: View
    private var parentId: Long = -1
    private var nextId: Long = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.dialog_add_task, container, false)

        bindFragment()
        return v
    }

    private fun bindFragment() {
        val doneBtn: TextView = v.findViewById(R.id.done_button)
        val cancelBtn: TextView = v.findViewById(R.id.cancel_button)
        val name: TextInputEditText = v.findViewById(R.id.task_name)
        val description: TextInputEditText = v.findViewById(R.id.task_description)
        doneBtn.setOnClickListener {
            if (name.text == null || name.text?.toString()?.trim() == "") {
                Toast.makeText(v.context, getString(R.string.name_not_empty), Toast.LENGTH_LONG)
                    .show()
            } else {
                doneBtn.setOnClickListener {}
                val bundle = this.arguments
                if (bundle != null) {
                    parentId = bundle.getLong(getString(R.string.extra_parent_id))
                    nextId = bundle.getLong(getString(R.string.extra_next_id))
                }
                mInteraction.saveTask(
                    Task(
                        name = name.text.toString(),
                        description = description.text.toString(),
                        childOf = parentId,
                        isHaveChildren = false,
                        goesBefore = nextId
                    )
                )
                dismiss()
            }
        }
        cancelBtn.setOnClickListener {
            dismiss()
        }
    }

    interface AddTaskInteraction {
        fun saveTask(task: Task)
    }
}