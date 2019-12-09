package com.gitspark.gitspark.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gitspark.gitspark.model.AuthUser
import com.gitspark.gitspark.model.SearchCriteria
import com.gitspark.gitspark.room.dao.AuthUserDao
import com.gitspark.gitspark.room.dao.SearchCriteriaDao

@Database(entities = [AuthUser::class, SearchCriteria::class], version = 1)
@TypeConverters(ListConverter::class)
abstract class Database : RoomDatabase() {

    abstract fun authUserDao(): AuthUserDao

    abstract fun searchCriteriaDao(): SearchCriteriaDao
}