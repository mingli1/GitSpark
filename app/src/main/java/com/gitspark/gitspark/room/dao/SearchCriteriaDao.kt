package com.gitspark.gitspark.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gitspark.gitspark.model.SearchCriteria

@Dao
interface SearchCriteriaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSearchCriteria(sc: SearchCriteria)

    @Query("SELECT * FROM searchcriteria ORDER BY datetime(timestamp) DESC")
    fun getRecentSearches(): LiveData<List<SearchCriteria>>

    @Query(
        """
        DELETE FROM searchcriteria WHERE q NOT IN
        (SELECT q FROM searchcriteria ORDER BY datetime(timestamp) DESC LIMIT 10)
        """
    )
    fun limitSearches()

    @Query("DELETE FROM searchcriteria WHERE q = :q")
    fun deleteSearch(q: String)

    @Query("DELETE FROM searchcriteria")
    fun clear()
}