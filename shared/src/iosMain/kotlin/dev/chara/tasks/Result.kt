package dev.chara.tasks

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result

fun <V, E> Result<V, E>.isOk(): Boolean = this is Ok
fun <V, E> Result<V, E>.isErr(): Boolean = this is Err
fun <V, E> ok(value: V): Result<V, E> = Ok(value)
fun <V, E> err(error: E): Result<V, E> = Err(error)