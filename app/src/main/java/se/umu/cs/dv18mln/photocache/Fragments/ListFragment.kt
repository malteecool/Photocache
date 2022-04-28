package se.umu.cs.dv18mln.photocache.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import se.umu.cs.dv18mln.photocache.*


/**
 * Custom list fragments to be displayed in the list view of the main activity.
 */
class ListFragment() : Fragment() {

    lateinit var storage: FirebaseStorage
    lateinit var cacheData: ArrayList<CacheData>
    lateinit var userId: String


    companion object {
        fun newInstance(cacheDataString: ArrayList<String>, userId: String): Fragment {
            val args = Bundle()
            args.putStringArrayList("cacheData", cacheDataString)
            args.putString("id", userId)
            val f = ListFragment()
            f.arguments = args
            return f
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = arguments?.getString("id") as String
        val cacheData_id = arguments?.getStringArrayList("cacheData") as ArrayList<String>

        getData(cacheData_id, userId)

    }

    /**
     * Starts the "detailsactivity" when a fragment is pressed.
     * The intent is launched with the data stored in the cachedata-class.
     */
    private fun startDetails(cacheData: CacheData, userId: String) {
        val intent = Intent(requireContext(), DetailsActivity::class.java)
        intent.putExtra("cacheData", cacheData)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }

    private fun getData(cacheData_id: ArrayList<String>, user: String) {
        val databaseReference: DatabaseReference = Firebase.database.reference
        storage = Firebase.storage

        val cacheData = ArrayList<CacheData>()
        var i = 0
        for (id in cacheData_id) {
            val query = databaseReference.child("cache").child(id)
            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    // Assigning values from database.
                    val cacheId = dataSnapshot.child("id").value.toString()
                    val cache = CacheData(cacheId)
                    cache.title = dataSnapshot.child("title").value as String
                    cache.imgId = dataSnapshot.child("imageID").value as String
                    cache.desc = dataSnapshot.child("desc").value as String

                    val coords_as_string = dataSnapshot.child("coords").value as String
                    //Toast.makeText(context, coords_as_string, Toast.LENGTH_SHORT).show()
                    val coords = coords_as_string.split(',')
                    cache.xPos = coords[0].toDouble()
                    cache.yPos = coords[1].toDouble()

                    getUserScore(user, cache)

                    cacheData.add(cache)

                    val httpRef = storage.getReferenceFromUrl(
                        "gs://photocache-fc3d8.appspot.com/CacheImages/${cache.imgId}"
                    )

                    val maxDownloadSizeBytes: Long = 8 * 1024 * 1024 // 1 MB
                    httpRef.getBytes(maxDownloadSizeBytes).addOnSuccessListener {
                        cache.imgArray = it

                        if (i == cacheData_id.size - 1) setData(cacheData, user)

                        i++

                    }.addOnFailureListener {

                        it.printStackTrace()

                    }

                }

                override fun onCancelled(dataBaseError: DatabaseError) {
                    Toast.makeText(context, "Could not fetch database", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    private fun setData(cacheData: ArrayList<CacheData>, userId: String) {

        (activity as MainActivity?)!!.hideLoadingBar(true)

        val listView = requireView().findViewById<ListView>(R.id.listView)
        val listAdapter = CustomListView(
            requireActivity(), cacheData.toTypedArray()
        )
        listView.adapter = listAdapter

        listView.setOnItemClickListener() { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
            startDetails(cacheData[position], userId)
        }
    }

    /**
     * Retrieves score of the user
     */

    private fun getUserScore(user: String?, cache: CacheData) {

        val databaseReference: DatabaseReference = Firebase.database.reference
        if (user != null) {
            val userQuery = databaseReference.child("users").child(user).child(cache.id!!)
            userQuery.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    //Toast.makeText(context, "Value changed!", Toast.LENGTH_SHORT).show()
                    if (!dataSnapshot.exists()) {
                        //cache.id?.let { userQuery.child(it).setValue(0) }
                        cache.userScore = 0
                    } else {
                        val temp = cache.id.let { dataSnapshot.value }
                        cache.userScore = (temp as Long).toInt()
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Could not fetch database", Toast.LENGTH_LONG)
                        .show()
                }
            })
        } else {
            Toast.makeText(requireContext(), "User is null.", Toast.LENGTH_LONG).show()
        }

    }

}
