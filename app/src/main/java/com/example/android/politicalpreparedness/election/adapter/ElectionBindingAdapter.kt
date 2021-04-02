package com.example.android.politicalpreparedness.election.adapter

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.android.politicalpreparedness.R


@BindingAdapter("stateLocation")
fun setStateLocationText(view: TextView, location: String?){
    if(location.isNullOrEmpty()){
        view.text = view.context.getString(R.string.no_voting_locations_available)
    } else {
        view.text = view.context.getString(R.string.voting_locations)
    }
}

@BindingAdapter("ballotLocation")
fun setBallotLocationText(view: TextView, ballot: String?){
    if(ballot.isNullOrEmpty()){
        view.text = view.context.getString(R.string.no_ballot_information_available)
    } else {
        view.text = view.context.getString(R.string.ballot_information)
    }
}

@BindingAdapter("address")
fun setAddressText(view: TextView, address: String?){
    if(address.isNullOrEmpty()){
        view.text = view.context.getString(R.string.no_address_available)
    } else {
        view.text = address
    }
}
