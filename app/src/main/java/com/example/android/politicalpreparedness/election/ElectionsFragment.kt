package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.network.models.Election

class ElectionsFragment: Fragment() {

    private lateinit var viewModel: ElectionsViewModel
    private lateinit var viewModelFactory: ElectionsViewModelFactory
    private lateinit var binding: FragmentElectionBinding

    private lateinit var upcomingElectionsAdapter: ElectionListAdapter
    private lateinit var savedElectionsAdapter: ElectionListAdapter

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_election, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModelFactory = ElectionsViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(ElectionsViewModel::class.java)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        upcomingElectionsAdapter = ElectionListAdapter(ElectionListener { election ->
            viewModel.onElectionSelected(election)
        })

        savedElectionsAdapter = ElectionListAdapter(ElectionListener { election ->
            viewModel.onElectionSelected(election)
        })

        binding.upcomingElectionsRecyclerView.adapter = upcomingElectionsAdapter
        binding.savedElectionsRecyclerView.adapter = savedElectionsAdapter

        viewModel.savedElections.observe(viewLifecycleOwner) {
            savedElectionsAdapter.submitList(it)
        }
        viewModel.upcomingElections.observe(viewLifecycleOwner){
            upcomingElectionsAdapter.submitList(it)
        }


        viewModel.navigateToVoterInfo.observe(viewLifecycleOwner) { election ->
            if (election != null) {
                navigateToVoterInfoFragment(election)
                viewModel.onElectionSelectedDone()
            }
        }
    }

      private fun navigateToVoterInfoFragment(election: Election) {
        findNavController().navigate(
                ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(election))
    }
}