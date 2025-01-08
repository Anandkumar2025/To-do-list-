    package com.example.myapplication1

    import android.annotation.SuppressLint
    import android.app.DatePickerDialog
    import android.app.TimePickerDialog
    import androidx.appcompat.app.AppCompatActivity
    import android.os.Bundle
    import android.widget.Button
    import android.widget.Toast
    import android.view.LayoutInflater
    import android.view.WindowManager
    import androidx.appcompat.app.AlertDialog
    import android.widget.EditText
    import androidx.recyclerview.widget.LinearLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import java.util.Calendar


    class MainActivity : AppCompatActivity() {

        private lateinit var recyclerView: RecyclerView
        private lateinit var taskAdapter: TaskAdapter
        private lateinit var database: Database1

        private val taskList = mutableListOf<Task>()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
            setContentView(R.layout.activity_main)

            recyclerView = findViewById(R.id.recyclerview)
            val taskButton = findViewById<Button>(R.id.task)

            database= Database1(this)

            setupRecyclerView()
            loadTasksFromDatabase()

            taskButton.setOnClickListener {
                showAddTaskDialog()
            }
        }

        private fun setupRecyclerView() {
            taskAdapter = TaskAdapter(context = this,
                database=database,
                tasks=taskList,
                onTaskEdited = {task,position->
                    showEditTaskDialog(task,position)
                },onTaskDeleted = { task, position ->
                    deleteTaskFromDatabase(task, position)
                })
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = taskAdapter
        }

        private fun loadTasksFromDatabase(){
            taskList.clear()
            taskList.addAll(database.getAllTasks())
            prioritizeTasksByDate()
        }

        private fun showTimeDatePicker(datetimeInput: EditText) {
            val calendar = Calendar.getInstance()
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendar.get(Calendar.MINUTE)
            val currentYear = calendar.get(Calendar.YEAR)
            val currentMonth = calendar.get(Calendar.MONTH)
            val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val formattedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                    val timePickerDialog = TimePickerDialog(
                        this,
                        { _, hourOfDay, minute ->
                            val formattedTime = String.format("%02d:%02d", hourOfDay, minute)
                            val datetime = "$formattedDate $formattedTime"
                            datetimeInput.setText(datetime)
                        },
                        currentHour,
                        currentMinute,
                        false
                    )
                    timePickerDialog.window?.setBackgroundDrawableResource(R.drawable.card_input_back)
                    timePickerDialog.show()
                },
                currentYear,
                currentMonth,
                currentDay
            )
            datePickerDialog.window?.setBackgroundDrawableResource(R.drawable.card_input_back)
            datePickerDialog.show()
        }

        @SuppressLint("NotifyDataSetChanged")
        private fun prioritizeTasksByDate() {
            val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
            val sortedTasks = taskList.sortedByDescending { task ->
                try {
                    dateFormat.parse(task.datetime)?.time
                } catch (e: Exception) {
                    Long.MIN_VALUE
                }
            }
            taskList.clear()
            taskList.addAll(sortedTasks)
            taskAdapter.notifyDataSetChanged()
        }

        private fun showAddTaskDialog() {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.card_input, null)
            val titleInput = dialogView.findViewById<EditText>(R.id.task_title_input)
            val descriptionInput = dialogView.findViewById<EditText>(R.id.task_description_input)
            val datetimeInput = dialogView.findViewById<EditText>(R.id.task_due_input)

            datetimeInput.setOnClickListener {
                showTimeDatePicker(datetimeInput)
            }
            val dialog = AlertDialog.Builder(this)
                .setTitle("Add Task")
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton("Add") { _, _ ->
                    val title = titleInput.text.toString()
                    val description = descriptionInput.text.toString()
                    val datetime = datetimeInput.text.toString()

                    if (title.isNotEmpty() && datetime.isNotEmpty() || description.isNotEmpty()) {
                        val newTask = Task(
                            id = 0,
                            title = title,
                            description = description,
                            datetime = datetime,
                            status = true
                        )
                        val result = database.addTask(newTask)

                        if (result != -1L) {
                            newTask.id = result.toInt()
                            taskList.add(newTask)
                            taskAdapter.notifyItemInserted(taskList.size - 1)
                            prioritizeTasksByDate()

                            Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to add task", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .create()

            dialog.window?.setBackgroundDrawableResource(R.drawable.card_input_back)
            dialog.show()
        }


        private fun showEditTaskDialog(task: Task, position: Int) {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.card_input, null)
            val titleInput = dialogView.findViewById<EditText>(R.id.task_title_input)
            val descriptionInput = dialogView.findViewById<EditText>(R.id.task_description_input)
            val datetimeInput = dialogView.findViewById<EditText>(R.id.task_due_input)

            datetimeInput.setOnClickListener {
                showTimeDatePicker(datetimeInput)
            }

            titleInput.setText(task.title)
            descriptionInput.setText(task.description)
            datetimeInput.setText(task.datetime)

            val dialog = AlertDialog.Builder(this)
                .setTitle("Edit Task")
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton("Save") { _, _ ->
                    val updatedTitle = titleInput.text.toString()
                    val updatedDescription = descriptionInput.text.toString()
                    val updatedDatetime = datetimeInput.text.toString()


                    if (updatedTitle.isNotEmpty() && updatedDatetime.isNotEmpty() || updatedDescription.isNotEmpty()) {
                        val updatedTask = Task(
                            id = task.id,
                            title = updatedTitle,
                            description = updatedDescription,
                            datetime = updatedDatetime,
                            status = task.status
                        )
                        val result = database.updateTask(updatedTask, updatedTask.id)

                        if (result > 0) {
                            taskList[position] = updatedTask
                            taskAdapter.notifyItemChanged(position)
                            prioritizeTasksByDate()
                            Toast.makeText(this, "Task Updated ", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Error in updating", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .create()

            dialog.window?.setBackgroundDrawableResource(R.drawable.card_input_back)
            dialog.show()
        }

        private fun deleteTaskFromDatabase(task: Task, position: Int) {
            val result = database.deleteTask(task.title)
            if (result > 0) {
                taskList.removeAt(position)
                taskAdapter.notifyItemRemoved(position)
                Toast.makeText(this, "Deleted Successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error deleting task", Toast.LENGTH_SHORT).show()
            }
        }

    }




