package se.umu.cs.dv18mln.photocache.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import se.umu.cs.dv18mln.photocache.HighScoreListAdapter
import se.umu.cs.dv18mln.photocache.HighscoreData
import se.umu.cs.dv18mln.photocache.R

/**
 * Fragment to be used to display highscores.
 * The highscores are stored using Google
 * Firebase API.
 *
 */
class HighscoreFragment : Fragment() {
    private lateinit var databaseReference: DatabaseReference
    private var highscoreArray = arrayListOf<HighscoreData>()

    companion object {
        fun newInstance(): Fragment {
            return HighscoreFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_highscore, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.lblCouldNotLoad).visibility = View.GONE

        // To be changed.
        val querySize = 20

        handleData(view, querySize)

    }

    private fun setAdapter(view: View) {
        val listView = view.findViewById<ListView>(R.id.highscore_listview)
        highscoreArray.sortBy { it.score }
        if (isAdded) {
            val highscoreAdapter = HighScoreListAdapter(
                requireActivity(),
                highscoreArray.reversed().toTypedArray()
            )
            listView.adapter = highscoreAdapter
        }
    }

    private fun onSuccess(view: View) {
        view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
        view.findViewById<ListView>(R.id.highscore_listview).visibility = View.VISIBLE
    }

    private fun onFailure(view: View) {
        view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
        view.findViewById<TextView>(R.id.lblCouldNotLoad).visibility = View.VISIBLE
    }

    /**
     * Gets highscore from db.
     */
    private fun handleData(view: View, querySize: Int) {
        databaseReference = Firebase.database.reference
        val query = databaseReference.child("highscore")
            .orderByValue()
            .limitToLast(querySize)

        query.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {

                highscoreArray.add(
                    HighscoreData(
                        dataSnapshot.key as String,
                        (dataSnapshot.value as Long).toInt()
                    )
                )
                setAdapter(view)
                onSuccess(view)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val score = dataSnapshot.value
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.key
                for (data in highscoreArray) {
                    if (data.username == user) {
                        highscoreArray.remove(data)
                        break
                    }
                }
                setAdapter(view)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val score = dataSnapshot.value
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    context, "Failed to load highscores.",
                    Toast.LENGTH_SHORT
                ).show()
                onFailure(view)
            }
        })
    }

}


