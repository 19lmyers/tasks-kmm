package dev.chara.tasks.model

fun new(id: String, title: String) = TaskList(id, title)

fun sortTypes() = TaskList.SortType.values().asList()
fun sortDirections() = TaskList.SortDirection.values().asList()

fun colors() = TaskList.Color.values().asList()