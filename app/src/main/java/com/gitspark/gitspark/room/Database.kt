package com.gitspark.gitspark.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gitspark.gitspark.model.AuthUser
import com.gitspark.gitspark.room.dao.AuthUserDao

@Database(entities = [AuthUser::class], version = 1)
abstract class Database : RoomDatabase() {

    abstract fun authUserDao(): AuthUserDao
}