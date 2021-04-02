package com.example.android.politicalpreparedness.repository

import androidx.lifecycle.Transformations
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.database.asDomainModel
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.*
import com.example.android.politicalpreparedness.utils.getToday
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ElectionRepository(private val electionDatabase: ElectionDatabase) {

    val upcomingElections = Transformations.map(electionDatabase.electionDao.getUpcomingElections(getToday())) {
        it.asDomainModel()
    }

    val savedElections = Transformations.map(electionDatabase.electionDao.getSavedElections()){
        it.asDomainModel()
    }

    suspend fun refreshElections() {
        deletePastUnsavedElections()

        var elections: List<Election>
        withContext(Dispatchers.IO) {
            val electionResponse: ElectionResponse = CivicsApi.retrofitService.getElectionsAsync()
            elections = electionResponse.elections

            savedElections.value.let {
                if (it != null) {
                    if(it.size > 0){
                        elections.forEach{ election ->
                            electionDatabase.electionDao.getElectionById(election.id).let { saved ->
                                if(saved.id == election.id) election.isSaved = saved.isSaved
                            }
                        }
                        electionDatabase.electionDao.insertAll(elections.asDataBaseModel())
                    } else {
                        electionDatabase.electionDao.insertAll(elections.asDataBaseModel())
                    }
                }
            }
        }
    }

    suspend fun getVoterInfo(electionId: Int, address: String): VoterInfoResponse? {
        var voterInfo: VoterInfoResponse? = null
        withContext(Dispatchers.IO) {
            val voterInfoResponse: VoterInfoResponse = CivicsApi.retrofitService.getVoterInfoAsync(electionId, address)
            voterInfo = voterInfoResponse
        }
        return voterInfo
    }

    suspend fun updateSavedElection(election: Election){
        withContext(Dispatchers.IO){
            electionDatabase.electionDao.insert(election.asDatabaseModel())
        }
    }

    private suspend fun deletePastUnsavedElections() {
        withContext(Dispatchers.IO) {
            electionDatabase.electionDao.deletePastUnsavedElections(getToday())
        }
    }
}