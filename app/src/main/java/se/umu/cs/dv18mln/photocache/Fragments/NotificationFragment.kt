package se.umu.cs.dv18mln.photocache.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import se.umu.cs.dv18mln.photocache.R

/**
 * The view holding notifications.
 * (Coming soon.)
 */
class NotificationFragment:Fragment() {

    companion object{
        fun newInstance():Fragment{
            return NotificationFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_notification, container, false)

}