package com.gitspark.gitspark.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gitspark.gitspark.model.Repo

@Dao
interface RepoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRepo(repo: Repo)

    @Query("SELECT * FROM repos WHERE name LIKE :search AND private = :isPrivate ORDER BY fullName ASC")
    fun getReposDefaultOrder(search: String = "", isPrivate: Boolean = false)

    @Query("SELECT * FROM repos WHERE name LIKE :search AND private = :isPrivate ORDER BY datetime(:orderDate) DESC")
    fun getReposOrderByDate(search: String = "", isPrivate: Boolean = false, orderDate: String)

    @Query("DELETE FROM repos")
    fun clear()
}