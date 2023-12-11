package uk.ac.tees.mad.q2252114

data class Task(
    val id: Long,
    val title: String,
    val description: String,
    val dueDate: Long,
    val isCompleted: Boolean,
    val location: String?
)
