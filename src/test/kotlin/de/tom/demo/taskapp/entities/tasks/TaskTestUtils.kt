package de.tom.demo.taskapp.entities.tasks

import de.tom.demo.taskapp.config.DataConfiguration
import de.tom.demo.taskapp.entities.Task

object TaskTestUtils {
    /**
     * This comparator is used in unit tests. it compares twi tasks without comparing the
     * fields createdAt and updatedAt. These fields are actualized automated and cannot be preset.
     */
    val taskComparator = Comparator { task: Task, other: Task ->
        if (task.text == other.text
        // TODO: compare other fields
        )
            0 else 1
    }

}
