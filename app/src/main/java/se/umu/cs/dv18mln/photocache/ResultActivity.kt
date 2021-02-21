package se.umu.cs.dv18mln.photocache

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
class ResultActivity: AppCompatActivity() {

    lateinit var args:Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.result_layout)
        setSupportActionBar(findViewById(R.id.result_toolbar))

        args = intent.getBundleExtra("bundle")

        val btnDone:Button  = findViewById(R.id.buttonDone)
        btnDone.setOnClickListener {
            finish()
        }
        setText()
    }

    /**
     * Sets the information passed in the bundle.
     */
    private fun setText(){
        val txtCalc:TextView = findViewById(R.id.txtScoreSet)
        txtCalc.text = floor(args.get("score") as Double).toString()

        val txtImgScore:TextView = findViewById(R.id.txtImgScore)
        txtImgScore.text = getString(R.string.img_score, (args.get("imageScore") as Double).roundToInt())

        val txtImgpercent:TextView = findViewById(R.id.txtImgpercent)
        txtImgpercent.text = getString(R.string.img_percent, (args.get("imagepercent") as Double).roundToInt())

        val txtPosScore:TextView = findViewById(R.id.txtPosScore)
        txtPosScore.text = getString(R.string.pos_score, (args.get("posScore") as Double).roundToInt())

        val txtPosDistance:TextView = findViewById(R.id.txtPosPercent)
        txtPosDistance.text = getString(R.string.pos_distance, (args.get("distance") as Double).roundToInt())
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