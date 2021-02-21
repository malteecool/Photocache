package se.umu.cs.dv18mln.photocache

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import java.lang.Math.abs

/**
 * Activity to handle the comparision between the two images and the
 * users positions of the user and the cache.
 * @author dv18mln
 */
class CalcActivity:AppCompatActivity() {

    val MAX_SCORE = 100.0

    lateinit var progressBar: ProgressBar
    lateinit var t:Thread
    val handler:Handler = Handler()
    val args = Bundle()

    /**
     * Retrieves the stored file as a bit map.
     * The comparison between the two images is done on a
     * separate thread to handle Gui-actions simultaneously.
     * The final score is based of the percentage patched with the two images
     * and the distance between the user and the cache/marker.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calc_layout)

        val byteArray = intent.getByteArrayExtra("imgBitmap")

        val bitmap1: Bitmap = BitmapFactory.decodeFile(intent.getStringExtra("dir"))
        val bitmap2: Bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

        progressBar = findViewById(R.id.progCalc)

        t = Thread(Runnable {
            var score = matchImage(bitmap1, bitmap2)
            score += matchPosition()
            args.putDouble("score", score)

            launchResult()
        })
        t.start()
    }

    /**
     * Matches two bitmaps.
     * Does not matter which order the bitmaps are passed,
     * the percentage similarity is calculated from the pixel-color
     * difference of the two images. If the two images differs in size, the
     * larger image will be compressed to fit the smaller.
     * The percentage matched is stored in a bundle to be presented in the
     * result.
     *
     * The score calculation could be considerably improved by changing the way
     * the two images is compared. In the way they are compared now two images
     * with a slightly similar color will get a high percentage match and thereby
     * a great score.
     *
     * @param img1 The first image as a bitmap.
     * @param img2 The second image, which will be compared to the first.
     * @return The resulting score after the images is compared.
     */
    private fun matchImage(img1: Bitmap, img2: Bitmap):Double{
        val width = img1.width
        val height = img1.height
        val width2 = img2.width
        val height2 = img2.height

        var resizedImg1: Bitmap = img1
        var resizedImg2: Bitmap = img2

        if(width != width2 || height != height2){
            //rescale image to fit the smaller of the two.
            //first image is larger
            if(width > width2 || height > height2){
                resizedImg1 = Bitmap.createScaledBitmap(img1, width2, height2, true)
                resizedImg2 = img2
            }
            //second image is larger
            else{
                resizedImg1 = img1
                resizedImg2 = Bitmap.createScaledBitmap(img2, width, height, true)
            }
        }
        progressBar.max = resizedImg1.height
        var diff = 0L
        for(y in 0 until resizedImg1.height){
            for(x in 0 until resizedImg2.width){
                diff += pixelDiff(resizedImg1.getPixel(x,y), resizedImg2.getPixel(x,y))
            }
            handler.post(Runnable {
                progressBar.progress = y
            })
        }
        val maxDiff = 3L * 255 * width * height
        args.putDouble("imagepercent", (100.0 * diff/maxDiff))
        args.putDouble("imageScore", (MAX_SCORE * 100 * diff/maxDiff))
        return (MAX_SCORE * 100 * diff/maxDiff)
    }

    /**
     * Method to compare two rgb colors.
     * These rgb values represents a pixel in each of the
     * images to be compared.
     *
     * @see <a href="https://en.wikipedia.org/wiki/Color_difference">
     *     https://en.wikipedia.org/wiki/Color_difference</a>
     *
     * @param rgb1 Color ones rbg values as int.
     * @param rgb2 Color twos rbg values as int.
     * @return The modulus value of the combined sum of the colors.
     */
    private fun pixelDiff(rgb1: Int, rgb2:Int):Int{
        val r1 = (rgb1 shr 16)  and 0xff
        val g1 = (rgb1 shr 8)  and 0xff
        val b1 = (rgb1)  and 0xff
        val r2 = (rgb2 shr 16)  and 0xff
        val g2 = (rgb2 shr 8)  and 0xff
        val b2 = (rgb2)  and 0xff
        return (abs(r1-r2) + abs(g1-g2) + abs(b1 - b2))
    }

    /**
     * Method to get the distance between the user and the cache.
     * The users position is stored in the bundle passed to the class.
     *
     * The closer the user is the marked-location the more points will be given.
     * If the user is more than 1000m (1km) from the location
     *
     * @return The score of the distance to the location.
     */
    private fun matchPosition():Double{
        val userPos = intent.getParcelableExtra<LatLng>("userLocation")
        val markerPos = intent.getParcelableExtra<LatLng>("markerLocation")

        val results = FloatArray(3)

        Location.distanceBetween(userPos.latitude,
            userPos.longitude, markerPos.latitude,
            markerPos.longitude, results)

        val distance = results[0].toDouble()
        val score = 1000.0-distance
        args.putDouble("distance", distance)
        args.putDouble("posScore", score)
        return score
    }

    /**
     * Method to start the result activity with the bundle where
     * all values of the calculation are stored.
     *
     * When the intent is started this activity is finished.
     * This is done to make the user return to the "detailsactivity" once
     * finished with the result activity.
     */
    private fun launchResult(){
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("bundle", args)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        this.finish()
    }

    /**
     * Overrides the back-button to warn the user if the button is pressed
     * in the middle of the calculation.
     */
    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm")
        builder.setMessage("Do you want to abort the calculation?")

        builder.setPositiveButton("Yes", DialogInterface.OnClickListener {
                dialogInterface: DialogInterface, i: Int ->
            super.onBackPressed()
        })
        builder.setNegativeButton("No", DialogInterface.OnClickListener {
                dialogInterface: DialogInterface, i: Int ->
            dialogInterface.dismiss()
        })

        val alertDialog = builder.create()
        alertDialog.show()
    }

}