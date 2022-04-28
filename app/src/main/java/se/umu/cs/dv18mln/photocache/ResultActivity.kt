package se.umu.cs.dv18mln.photocache

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.math.floor
import kotlin.math.roundToInt

/**
 * Activity to present the calculated score and values.
 * The values that is represented are the total score,
 * the score and percentage of the image comparison and
 * the score and distance between the user and the marked
 * location.
 *
 * values should be put in a bundle passed to the intent.
 */
class ResultActivity : AppCompatActivity() {

    lateinit var args: Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.result_layout)
        setSupportActionBar(findViewById(R.id.result_toolbar))

        args = intent.getBundleExtra("bundle")

        val btnDone: Button = findViewById(R.id.buttonDone)
        btnDone.setOnClickListener {
            finish()
        }
        setText()

        if (args.getString("userId") != null) {
            updateScore()
        }
    }

    /**
     * Sets the information passed in the bundle.
     */
    private fun setText() {
        val txtCalc: TextView = findViewById(R.id.txtScoreSet)
        txtCalc.text = floor(args.get("score") as Double).toString()

        val txtImgScore: TextView = findViewById(R.id.txtImgScore)
        txtImgScore.text =
            getString(R.string.img_score, (args.get("imageScore") as Double).roundToInt())

        val txtImgpercent: TextView = findViewById(R.id.txtImgpercent)
        txtImgpercent.text =
            getString(R.string.img_percent, (args.get("imagepercent") as Double).roundToInt())

        val txtPosScore: TextView = findViewById(R.id.txtPosScore)
        txtPosScore.text =
            getString(R.string.pos_score, (args.get("posScore") as Double).roundToInt())

        val txtPosDistance: TextView = findViewById(R.id.txtPosPercent)
        txtPosDistance.text =
            getString(R.string.pos_distance, (args.get("distance") as Double).roundToInt())
    }


    private fun updateScore() {

        val userId = args.getString("userId")
        val cacheId = args.getString("cacheId")
        val score = args.getDouble("score")

        val databaseReference: DatabaseReference = Firebase.database.reference
        val userQuery = databaseReference.child("users").child(userId!!)
        userQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // The value check needs to be done here since we need to retrieve the old value from
                // the db.
                if ((dataSnapshot.child(cacheId!!).value as Long).toInt() < score.toInt()) {
                    userQuery.child(cacheId).setValue(score.toInt())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ResultActivity, "Could not fetch database", Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

    /**
     * Re-sets the text in the new layout when the orientation
     * changes.
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setText()
    }

}