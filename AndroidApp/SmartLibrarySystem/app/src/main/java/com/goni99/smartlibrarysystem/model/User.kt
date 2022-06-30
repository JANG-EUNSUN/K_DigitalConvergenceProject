package com.goni99.smartlibrarysystem.model

data class User(
    val id: String,
    val pwd: String,
    val name: String,
    val gender: Boolean,
    val age: Int,
    val phone: String
)
