package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.repository.RepresentativeRepository
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel : ViewModel() {

    private val representativeRepository = RepresentativeRepository()

    private val _address = MutableLiveData<Address>()
    val address: LiveData<Address>
        get() = _address

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    private val _noRepresentativeFound = MutableLiveData<Boolean>()
    val noRepresentativeFound: LiveData<Boolean>
        get() = _noRepresentativeFound

    private val _buttonRepresentative = MutableLiveData<Address>()
    val buttonRepresentative: LiveData<Address>
        get() = _buttonRepresentative

    init {
        _representatives.value = null
        _noRepresentativeFound.value = false
    }

    fun getAddressFromGeolocation(address: Address?) {
        if (address != null) {
            setAddress(address)
        }
    }

    fun buttonRepresentativeClicked(address: Address) {
        _buttonRepresentative.value = address
    }

    fun setAddress(address: Address) {
        _address.value = address
        viewModelScope.launch {
            getRepresentatives(address)
        }
    }

    fun noRepresentativeFoundDone(){
        _noRepresentativeFound.value = false
    }

    private suspend fun getRepresentatives(address: Address) {
        viewModelScope.launch {
            try {
                val representativeInfo = representativeRepository.getRepresentatives(address)
                if(representativeInfo != null){
                    val offices = representativeInfo.offices
                    val officials = representativeInfo.officials
                    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }
                }
            } catch (e: Exception) {
                _representatives.value = null
                _noRepresentativeFound.value = true
            }
        }

    }
}