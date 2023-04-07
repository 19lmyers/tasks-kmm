package dev.chara.tasks.bridge

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result

fun <V, E> Result<V, E>.isOk(): Boolean = this is Ok
fun <V, E> Result<V, E>.isErr(): Boolean = this is Err
fun <V, E> success(value: V): Result<V, E> = Ok(value)
fun <V, E> failure(error: E): Result<V, E> = Err(error)