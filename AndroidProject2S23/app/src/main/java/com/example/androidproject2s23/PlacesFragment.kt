package com.example.androidproject2s23

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class PlacesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var placesAdapter: PlacesAdapter
    private lateinit var placesList: List<Place>

    companion object {
        private const val ARG_PLACES_LIST = "placesList"

        fun newInstance(placesList: List<Place>): PlacesFragment {
            val fragment = PlacesFragment()
            val args = Bundle()
            args.putSerializable(ARG_PLACES_LIST, ArrayList(placesList))
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            placesList = it.getSerializable(ARG_PLACES_LIST) as? List<Place> ?: emptyList()
        }
    }

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_places, container, false)
        recyclerView = rootView.findViewById(R.id.placesRecyclerView)
        placesAdapter = PlacesAdapter(placesList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = placesAdapter
        return rootView
    }

}
