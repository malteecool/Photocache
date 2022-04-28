package se.umu.cs.dv18mln.photocache.Fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import se.umu.cs.dv18mln.photocache.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

}