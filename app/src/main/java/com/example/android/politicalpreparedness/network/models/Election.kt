package com.example.android.politicalpreparedness.network.models

import android.os.Parcelable
import androidx.room.*
import com.example.android.politicalpreparedness.database.ElectionEntities
import com.squareup.moshi.*
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Election(
        val id: Int,
        val name: String,
        val electionDay: Date,
        @Json(name = "ocdDivisionId") val division: Division,
        var isSaved: Boolean = false
) : Parcelable

fun List<Election>.asDataBaseModel(): List<ElectionEntities> {
    return map {
        ElectionEntities(
                id = it.id,
                name = it.name,
                electionDay = it.electionDay,
                division = it.division,
                isSaved = it.isSaved
        )
    }
}

fun Election.asDatabaseModel():ElectionEntities{
    return ElectionEntities(
            id = this.id,
            name=this.name,
            electionDay = this.electionDay,
            division = this.division,
            isSaved = this.isSaved
    )
}