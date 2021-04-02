package com.example.android.politicalpreparedness.repository

import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class RepresentativeRepository {

    suspend fun getRepresentatives(address: Address): RepresentativeResponse? {
        var representativeInfo: RepresentativeResponse? = null
            withContext(Dispatchers.IO) {
                representativeInfo = CivicsApi.retrofitService.getRepresentativesAsync(address.toFormattedString() )
            }
        return representativeInfo
    }
}