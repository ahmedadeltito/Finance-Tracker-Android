package com.ahmedadeltito.financetracker.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Contract for a suspend use-case that returns a single value.
 * Implement [execute] and provide a [dispatcher]; the default invoke handles
 * context switching and Result wrapping.
 */
interface SuspendUseCase<P, R> {
    val dispatcher: CoroutineDispatcher

    suspend operator fun invoke(params: P): Result<R> = try {
        withContext(dispatcher) { Result.success(execute(params)) }
    } catch (e: Exception) {
        Result.error(e)
    }

    suspend fun execute(params: P): R
}

/**
 * Contract for a Flow-based use-case.
 * Implement [execute] returning a plain Flow<R>; the default invoke switches
 * dispatcher and wraps each emission/error into Result.
 */
interface FlowUseCase<P, R> {
    val dispatcher: CoroutineDispatcher

    operator fun invoke(params: P): Flow<Result<R>> = execute(params)
        .map { Result.success(it) }
        .catch { e -> emit(Result.error(e)) }
        .flowOn(dispatcher)

    fun execute(params: P): Flow<R>
}

object NoParameters 