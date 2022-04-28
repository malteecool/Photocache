package se.umu.cs.dv18mln.photocache

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import se.umu.cs.dv18mln.photocache.Fragments.HighscoreFragment
import se.umu.cs.dv18mln.photocache.Fragments.ListFragment
import se.umu.cs.dv18mln.photocache.Fragments.NotificationFragment


/**
 * The main activity of the application.
 *
 * The available caches are hardcoded but could eventually be store remotely in
 * some database with cache data tuples.
 *
 * The activity implements a bottomNavigationbar to make the user
 * able to switch between fragments. The user can switch between
 * the main page, containing the available caches to find, a notification page
 * where close caches and news related information can be found. And finally a
 * highscore-list where the top players are listed. The last two is not yet
 * implemented due to lack of time and no access to a functioning database.
 * (Hopefully in the future).
 *
 * The activity also uses a toolbar where the user can find simple information
 * and rules of how to play the game.
 */
class MainActivity : AppCompatActivity() {

    lateinit var storage: FirebaseStorage
    lateinit var mAuth: FirebaseAuth
    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var fragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        val user: FirebaseUser? = mAuth.currentUser
        if (user != null) {
            getData(user)
        }
        else {
            signInAnonymously()
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        setSupportActionBar(findViewById(R.id.main_toolbar))
        supportActionBar?.title = null
        supportActionBar?.setIcon(R.drawable.baseline_home_24)

        val navigationItem = BottomNavigationView.OnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.fragment_container,
                        fragment,
                        fragment.javaClass.simpleName
                    ).commit()
                    supportActionBar?.setIcon(R.drawable.baseline_home_24)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_notifications -> {
                    val fragmentNote = NotificationFragment.newInstance()
                    supportFragmentManager.beginTransaction().replace(
                        R.id.fragment_container,
                        fragmentNote,
                        fragmentNote.javaClass.simpleName
                    ).commit()
                    supportActionBar?.title = null
                    supportActionBar?.setIcon(R.drawable.baseline_notifications_24)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_highscore -> {
                    val fragmentScore = HighscoreFragment.newInstance()
                    supportFragmentManager.beginTransaction().replace(
                        R.id.fragment_container,
                        fragmentScore,
                        fragmentScore.javaClass.simpleName
                    ).commit()
                    supportActionBar?.setIcon(0)
                    supportActionBar?.title = getString(R.string.fragmentHighscore)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItem)
    }

    private fun initFragment(cacheData: ArrayList<String>, userId: String){
        fragment = ListFragment.newInstance(cacheData, userId)
        supportFragmentManager.beginTransaction().replace(
            R.id.fragment_container,
            fragment,
            fragment.javaClass.simpleName
        ).commit()
    }

    fun hideLoadingBar(hide: Boolean?):Boolean{
        val progbar = findViewById<ProgressBar>(R.id.mainProgressbar)

        if(hide != null && hide == true)
            progbar.visibility = View.GONE

        return progbar.visibility == View.GONE
    }

    private fun signInAnonymously(){
        mAuth.signInAnonymously().addOnSuccessListener(this,
            OnSuccessListener<AuthResult?> {
                println("Not signed in")
                getData(null)
            })
            .addOnFailureListener(this,
                OnFailureListener { exception ->
                    Log.e(
                        "MainActivity",
                        "signFailed****** ",
                        exception
                    )
                })
    }

    /**
     * Gets the data from firebase.
     */
    private fun getData(user: FirebaseUser?){
        val databaseReference: DatabaseReference = Firebase.database.reference
        storage = Firebase.storage

        val query = databaseReference.child("cache").limitToLast(5)
        query.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val cacheDataString = ArrayList<String>()
                for ((i, ds) in dataSnapshot.children.withIndex()){
                    // Assigning values from database.
                    val cacheId = ds.key
                    cacheDataString.add(cacheId!!)
                    if (i == dataSnapshot.childrenCount.toInt()-1) initFragment(cacheDataString, user!!.uid)

                }
            }
            override fun onCancelled(dataBaseError: DatabaseError) {
                Toast.makeText(this@MainActivity,"Could not fetch database", Toast.LENGTH_LONG).show()
            }
        })
    }

    /**
     * Retrieves score of the user
     */

    private fun getUserScore(user: FirebaseUser?, cache:CacheData){

        val databaseReference: DatabaseReference = Firebase.database.reference
        if(user != null){
            val userQuery = databaseReference.child("users").child(user.uid)
            userQuery.addValueEventListener(object: ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(!dataSnapshot.exists()){
                        cache.id?.let { userQuery.child(it).setValue(0) }
                        cache.userScore = 0
                    }
                    else{
                        val temp = cache.id?.let { dataSnapshot.child(it).value }
                        cache.userScore = (temp as Long).toInt()
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MainActivity,"Could not fetch database", Toast.LENGTH_LONG).show()
                }
            })
        }
        else{
            Toast.makeText(this@MainActivity,"User is null.", Toast.LENGTH_LONG).show()
        }

    }

    /**
     * Overrides the backbutton to ask the user if the backpress was
     * intentional and actually wants to leave the application.
     */
    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm")
        builder.setMessage("Are you sure?")

        builder.setPositiveButton(
            "Yes",
            DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int ->
                super.onBackPressed()
            })
        builder.setNegativeButton(
            "No",
            DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
            })

        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    /**
     * Launches the "Settings" menu.
     */
    override fun onOptionsItemSelected(item: MenuItem):Boolean{

        if(item.itemId == R.id.action_settings){
            launchSettings()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Initiates the popup. The window contains simple information
     * about the application and its intentions.
     *
     * The user can dismiss the window by clicking on it.
     */
    private fun showHelpPopUp(){
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popUpView = inflater.inflate(R.layout.popup_help, null)

        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true

        val popupWindow = PopupWindow(popUpView, width, height, focusable)
        popupWindow.elevation = 20F
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0)
        popUpView.setOnClickListener{
            popupWindow.dismiss()
        }
    }

    /**
     * Launching the intent.
     */
    private fun launchSettings(){

        val intent = Intent(this, SettingsActivity::class.java)
        startActivityForResult(intent, 1)

    }

}
