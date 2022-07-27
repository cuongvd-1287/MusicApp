package com.example.musicapp.dataSource

interface OnResultListener<T> {
    fun onSuccess(data: T)
}
