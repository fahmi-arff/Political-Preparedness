package com.example.android.politicalpreparedness.database

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
//import com.example.android.politicalpreparedness.database.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.Division
import com.squareup.moshi.Json

import java.util.*

@Entity(tableName = "election_table")
data class ElectionEntities(
        @PrimaryKey val id: Int,
        @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "electionDay") val electionDay: Date,
        @Embedded(prefix = "division_") val division: Division,
        val isSaved: Boolean
) {
}

fun List<ElectionEntities>.asDomainModel(): List<Election> {
    return map {
        Election(
                id = it.id,
                name = it.name,
                electionDay = it.electionDay,
                division = it.division,
                isSaved = it.isSaved
        )
    }
}