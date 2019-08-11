package com.gitspark.gitspark.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gitspark.gitspark.model.AuthUser

@Dao
interface AuthUserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAuthUser(user: AuthUser)

    @Query("SELECT * FROM user LIMIT 1")
    fun getAuthUser(): LiveData<AuthUser>

    @Query("DELETE FROM user")
    fun clear()
}