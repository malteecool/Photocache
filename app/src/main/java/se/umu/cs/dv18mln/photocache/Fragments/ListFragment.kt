package se.umu.cs.dv18mln.photocache.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import androidx.fragment.app.Fragment
import okhttp3.Cache
import se.umu.cs.dv18mln.photocache.CacheData
import se.umu.cs.dv18mln.photocache.CustomListView
import se.umu.cs.dv18mln.photocache.DetailsActivity
import se.umu.cs.dv18mln.photocache.R

/**
 * Custom list fragments to be displayed in the list view of the main activity.
 */
class ListFragment(cacheData: Array<CacheData>) : Fragment() {

    private var cache = cacheData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listView = view.findViewById<ListView>(R.id.listView)
        val listAdapter = CustomListView(
            requireActivity(),cache)
        listView.adapter = listAdapter

        listView.setOnItemClickListener(){
                _: AdapterView<*>?, _: View?, position: Int, _: Long ->
            startDetails(cache[position])
        }
    }

    /**
     * Starts the "detailsactivity" when a fragment is pressed.
     * The intent is launched with the data stored in the cachedata-class.
     */
    private fun startDetails(cacheData: CacheData){
        val intent = Intent(requireContext(), DetailsActivity::class.java)
        intent.putExtra("cacheData",cacheData)
        startActivity(intent)
    }

}
