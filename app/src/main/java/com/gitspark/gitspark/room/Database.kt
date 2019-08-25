package com.gitspark.gitspark.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gitspark.gitspark.model.AuthUser
import com.gitspark.gitspark.room.dao.AuthUserDao

@Database(entities = [AuthUser::class], version = 1)
@TypeConverters(ListConverter::class)
abstract class Database : RoomDatabase() {

    abstract fun authUserDao(): AuthUserDao
}