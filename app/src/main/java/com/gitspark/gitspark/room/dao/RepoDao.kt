package com.gitspark.gitspark.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gitspark.gitspark.model.Repo

@Dao
interface RepoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRepo(repo: Repo)

    @Query("SELECT * FROM repos WHERE name LIKE :search AND isPrivate = :isPrivate ORDER BY fullName ASC")
    fun getReposDefaultOrder(search: String, isPrivate: Boolean): LiveData<List<Repo>>

    @Query("SELECT * FROM repos WHERE name LIKE :search AND isPrivate = :isPrivate ORDER BY datetime(:orderDate) DESC")
    fun getReposOrderByDate(search: String, isPrivate: Boolean, orderDate: String): LiveData<List<Repo>>

    @Query("DELETE FROM repos")
    fun clear()
}