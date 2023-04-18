package dev.chara.tasks.model

fun new(id: String, title: String) = TaskList(id, title)

fun colors() = TaskList.Color.values().asList()
fun icons() = TaskList.Icon.values().asList()

fun sortTypes() = TaskList.SortType.values().asList()
fun sortDirections() = TaskList.SortDirection.values().asList()