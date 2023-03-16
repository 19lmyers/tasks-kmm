package dev.chara.tasks.data.cache

import com.benasher44.uuid.uuid4
import dev.chara.tasks.model.Task

fun Task.isDirty() = id.startsWith("temp_")

fun newDirtyId() = "temp_" + uuid4()