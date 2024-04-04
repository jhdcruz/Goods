package io.github.jhdcruz.memo.data.task

data class Task(
    val uid: String? = null,

    val priority: Int = 0,
    val category: String? = null,

    val title: String = "",

    /**
     * This serves as a brief (one-liner) description of the task,
     * and is different from the body which contains
     * the detailed information about the task.
     */
    val description: String? = null,

    /**
     * This serves as the detailed information about the task,
     * and is different from the description which contains
     * a brief (one-liner) description of the task.
     */
    val body: String? = null,

    val tags: List<String>? = null,

    @field:JvmField
    val isCompleted: Boolean = false,

    val created: String = "",
    val updated: String? = null,
)

