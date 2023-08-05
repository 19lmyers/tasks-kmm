package dev.chara.tasks.shared.data

import com.benasher44.uuid.uuid4
import dev.chara.tasks.shared.model.Task

internal fun Task.isDirty() = id.startsWith("temp_")

internal fun newDirtyId() = "temp_" + uuid4()
