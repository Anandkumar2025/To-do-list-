package com.example.myapplication1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val context: Context,
    private val database: Database1,
    private val tasks: List<Task>,
    private val onTaskDeleted: (Task, Int) -> Unit,
    private val onTaskEdited: (Task, Int) -> Unit,
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.task_title)
        val description: TextView = itemView.findViewById(R.id.task_description)
        val datetime: TextView = itemView.findViewById(R.id.task_due)
        val deleteButton: ImageView = itemView.findViewById(R.id.deletebutton)
        val switch: SwitchCompat = itemView.findViewById(R.id.switch1)
        val editButton: ImageView = itemView.findViewById(R.id.editbutton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_card, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.title.text = task.title
        holder.description.text = task.description
        holder.datetime.text = "Due: ${task.datetime}"
        holder.switch.isChecked = task.status
        holder.switch.isEnabled=task.status

        holder.switch.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                task.status = false
                val updateResult = database.updateTaskStatus(task.title, false)
                if (updateResult > 0) {
                    notifyItemChanged(position)
                    Toast.makeText(context, "Task is Completed", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Error in updating", Toast.LENGTH_SHORT).show()
                }
            }
        }

        holder.deleteButton.setOnClickListener {
            onTaskDeleted(task, position)
        }

        holder.editButton.setOnClickListener {
            onTaskEdited(task,position)
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }
}
