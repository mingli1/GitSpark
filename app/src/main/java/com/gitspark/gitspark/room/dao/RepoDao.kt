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

    @Query("SELECT * FROM repos WHERE UPPER(name) LIKE UPPER(:search) ORDER BY fullName ASC")
    fun getAllReposDefaultOrder(search: String): LiveData<List<Repo>>

    @Query("SELECT * FROM repos WHERE UPPER(name) LIKE UPPER(:search) ORDER BY datetime(repoPushedAt) DESC")
    fun getAllReposOrderByPushed(search: String): LiveData<List<Repo>>

    @Query("SELECT * FROM repos WHERE UPPER(name) LIKE UPPER(:search) ORDER BY datetime(repoCreatedAt) DESC")
    fun getAllReposOrderByCreated(search: String): LiveData<List<Repo>>

    @Query("SELECT * FROM repos WHERE UPPER(name) LIKE UPPER(:search) ORDER BY datetime(repoUpdatedAt) DESC")
    fun getAllReposOrderByUpdated(search: String): LiveData<List<Repo>>

    @Query("SELECT * FROM repos WHERE UPPER(name) LIKE UPPER(:search) AND isPrivate = 1 ORDER BY fullName ASC")
    fun getPrivateReposDefaultOrder(search: String): LiveData<List<Repo>>

    @Query("SELECT * FROM repos WHERE UPPER(name) LIKE UPPER(:search) AND isPrivate = 1 ORDER BY datetime(repoPushedAt) DESC")
    fun getPrivateReposOrderByPushed(search: String): LiveData<List<Repo>>

    @Query("SELECT * FROM repos WHERE UPPER(name) LIKE UPPER(:search) AND isPrivate = 1 ORDER BY datetime(repoCreatedAt) DESC")
    fun getPrivateReposOrderByCreated(search: String): LiveData<List<Repo>>

    @Query("SELECT * FROM repos WHERE UPPER(name) LIKE UPPER(:search) AND isPrivate = 1 ORDER BY datetime(repoUpdatedAt) DESC")
    fun getPrivateReposOrderByUpdated(search: String): LiveData<List<Repo>>

    @Query("DELETE FROM repos")
    fun clear()
}