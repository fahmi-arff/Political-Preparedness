package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface ElectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(elections: List<ElectionEntities>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(election: ElectionEntities)

    @Query("SELECT * FROM election_table WHERE electionDay >= :today")
    fun getUpcomingElections(today: Date): LiveData<List<ElectionEntities>>

    @Query("SELECT * FROM election_table WHERE isSaved")
    fun getSavedElections(): LiveData<List<ElectionEntities>>

    @Query("SELECT * FROM election_table WHERE id = :electionId")
    fun getElectionById(electionId: Int): ElectionEntities

    @Query("DELETE FROM election_table WHERE electionDay < :today AND NOT isSaved")
    suspend fun deletePastUnsavedElections(today: Date)
}