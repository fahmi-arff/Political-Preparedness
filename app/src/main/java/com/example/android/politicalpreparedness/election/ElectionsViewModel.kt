package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.ElectionRepository
import kotlinx.coroutines.launch

class ElectionsViewModel(application: Application) : AndroidViewModel(application) {
    private val database = ElectionDatabase.getInstance(application)
    private val electionRepository = ElectionRepository(database)

    var upcomingElections = electionRepository.upcomingElections

    val savedElections = electionRepository.savedElections

    private val _navigateToVoterInfo = MutableLiveData<Election>()
    val navigateToVoterInfo: LiveData<Election>
        get() = _navigateToVoterInfo

    init {
        viewModelScope.launch {
            try {
                electionRepository.refreshElections()
            } catch (e: Exception) {
                println("Exception upcoming elections : $e.message")
            }
        }
    }

    fun onElectionSelected(election: Election) {
        _navigateToVoterInfo.value = election
    }

    fun onElectionSelectedDone(){
        _navigateToVoterInfo.value = null
    }

}