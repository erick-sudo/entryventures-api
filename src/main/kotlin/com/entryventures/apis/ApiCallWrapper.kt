package com.entryventures.apis

interface ApiCallWrapper {
    suspend fun <P, E> call(apiCall: suspend () -> P): P?
}