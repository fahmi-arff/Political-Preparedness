package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.*
import com.example.android.politicalpreparedness.repository.ElectionRepository
import kotlinx.coroutines.launch
import java.lang.Exception

class VoterInfoViewModel(application: Application) : AndroidViewModel(application) {

    private val database = ElectionDatabase.getInstance(application)
    private val electionRepository = ElectionRepository(database)

    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    private val _openWebUrl = MutableLiveData<String>()
    val openWebUrl: LiveData<String>
        get() = _openWebUrl

    private val _election = MutableLiveData<Election>()
    val election: LiveData<Election>
        get() = _election

    fun getVoterInfo(args: VoterInfoFragmentArgs) {
        val selectedElection = args.selectedElection

        val address: String = if (selectedElection.division.state.isNotEmpty()) {
            "${selectedElection.division.state} ${selectedElection.division.country}"
        } else {
            "alabama"
        }

        viewModelScope.launch {
            try {
                _voterInfo.value = electionRepository.getVoterInfo(selectedElection.id, address)
            } catch (ex: Exception){
                _voterInfo.value = null
            }

            _election.value = selectedElection
        }
    }

    fun openWebUrl(url: String) {
        _openWebUrl.value = url
    }

    fun openWebUrlDone() {
        _openWebUrl.value = null
    }

    fun followElectionButton(election: Election) {
        viewModelScope.launch {
            try {
                if (!election.isSaved) {
                    election.isSaved = true
                    electionRepository.updateSavedElection(election)
                    _election.value = election
                } else {
                    election.isSaved = false
                    electionRepository.updateSavedElection(election)
                    _election.value = election
                }
            } catch (ex: Exception) {
                println("Exception follow button : $ex.message")
            }
        }
    }

}