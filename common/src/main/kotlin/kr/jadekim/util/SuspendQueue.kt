package kr.jadekim.util

interface SuspendQueue<T> {

    suspend fun size(): Long

    suspend fun isEmpty(): Boolean

    suspend fun pop(): T?

    suspend fun peek(): T?

    suspend fun push(value: T)

    suspend fun push(values: List<T>)

    suspend fun clear()
}