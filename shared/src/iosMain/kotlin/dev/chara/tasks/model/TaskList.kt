package dev.chara.tasks.model

fun new(id: String, title: String) = TaskList(id, title)

fun colors() = TaskList.Color.values().asList()