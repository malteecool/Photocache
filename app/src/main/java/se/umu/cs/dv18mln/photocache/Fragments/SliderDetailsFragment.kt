package se.umu.cs.dv18mln.photocache.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import se.umu.cs.dv18mln.photocache.CacheData
import se.umu.cs.dv18mln.photocache.R

/**
 * Fragment to hold the details of a cache. Displayed in the
 * DetailsActivity.
 */
class SliderDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

}