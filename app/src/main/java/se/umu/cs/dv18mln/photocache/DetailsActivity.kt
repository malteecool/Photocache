package se.umu.cs.dv18mln.photocache

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.android.material.tabs.TabLayout
import se.umu.cs.dv18mln.photocache.Fragments.SliderDetailsFragment
import se.umu.cs.dv18mln.photocache.Fragments.SliderImgFragment
import se.umu.cs.dv18mln.photocache.Fragments.SliderMapFragment
import se.umu.cs.dv18mln.photocache.ImageHandler.CalcActivity
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Activity to handle the detailed view of the cache.
 * The activity is launched when the user chooses/clicks
 * an alternative in the list view.
 *
 * The activity uses a ViewPager to make the user swipe
 * between fragments of the image and details and the map where
 * the location of the cache is set.
 *
 * Google maps API is used to create the map view. The map uses the users
 * position and the user therefor needs to accept this.
 *
 * The activity also implements a button to launch the pre-made camera
 * activity where the user can capture the image.
 *
 * @author dv18mln
 */
class DetailsActivity : AppCompatActivity(){
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_TAKE_PHOTO = 1
    var NUMBER_FRAGMENTS = 3

    private lateinit var mPager: CustomViewPager
    private lateinit var pagerAdapter: ScreenSlidePagerAdapter
    lateinit var cacheData: CacheData
    lateinit var tabLayout: TabLayout
    lateinit var cameraButton: ImageButton
    lateinit var currentPhotoPath: String
    lateinit var fragment: SliderMapFragment
    private val args = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NUMBER_FRAGMENTS = if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
            2
        } else{
            3
        }
        setContentView(R.layout.details_layout)

        cacheData = intent.getParcelableExtra("cacheData") as CacheData

        args.putString("userId", intent.getStringExtra("userId"))
        args.putString("cacheId", cacheData.id)
        args.putByteArray("imgBitmap", cacheData.imgArray)

        setSupportActionBar(findViewById(R.id.details_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = cacheData.title

        initNewPager(0)

        setCameraListener()

    }

    /**
     * Sets the listener to the camera button.
     */
    private fun setCameraListener(){
        cameraButton = findViewById(R.id.camera_button)
        cameraButton.setOnClickListener {
            if(fragment.getUserLocation() != null){
                args.putParcelable("userLocation", fragment.getUserLocation())
                args.putParcelable("markerLocation", fragment.getMarkerLocation())

                photoIntent()
            }
            else{
                Toast.makeText(this, "Getting user location! Please wait...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Initialized a new pager at a specific index. When the activity
     * is first created the index is set to 0.
     * This is done every time the phone orientation is changed to be handled
     * with the new layout. This also sets the tablayout to make the user
     * able to easier change between fragments.
     *
     * The number of fragments changes depending on the layout.
     *
     * @param index The index of the page where the ViewPager should be
     * launched.
     */
    private fun initNewPager(index:Int){

        setSupportActionBar(findViewById(R.id.details_toolbar))
        supportActionBar?.title = cacheData.title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager, cacheData)
        mPager = findViewById(R.id.pager)
        mPager.setSwipePagingEnabled(true)
        mPager.adapter = pagerAdapter
        mPager.currentItem = index
        tabLayout = findViewById(R.id.tabLayout)
        tabLayout.setupWithViewPager(mPager)
        tabLayout.getTabAt(0)?.setIcon(R.drawable.baseline_crop_original_black_18dp)
        tabLayout.getTabAt(1)?.setIcon(R.drawable.baseline_place_black_18dp)
        tabLayout.getTabAt(2)?.setIcon(R.drawable.baseline_info_black_48dp)
        pagerAdapter.notifyDataSetChanged()
        setCameraListener()
    }

    /**
     * Overrides the configurationChanged method.
     *
     * If the phone is put in landscape mode, the tabLayout displays
     * three tabs, an extra with the detailsFragment.
     * The ViewPager is re-initialized after this.
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            NUMBER_FRAGMENTS = 3
            setContentView(R.layout.details_layout)
            initNewPager(mPager.currentItem)
        }
        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            NUMBER_FRAGMENTS = 2
            setContentView(R.layout.details_layout)
            initNewPager(mPager.currentItem)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem):Boolean{
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    /**
     * Method to be run after the camera-activity is done.
     * If the image was captured successfully the directory of
     * the image is passed to the intent, along with the location
     * of the user and the cache.
     *
     * On a successful image capture the resultCode is -1, on a failed
     * capture 0.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result of a successful imagecapture is -1, if the user cancels the result is 0.
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == -1){
            val intent = Intent(this, CalcActivity::class.java)
            intent.putExtra("bundle", args)
            startActivity(intent)
        }
    }

    /**
     * Creates a file where the capture-photo will be stored
     * and launches the camera activity.
     */
    private fun photoIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createPhoto()
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "se.umu.cs.dv18mln.photocache",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    /**
     * Creates a file on the phone where the photo should be stored.
     * The file is named after the date of capture.
     * @return A temporary created file.
     */
    @Throws(IOException::class)
    private fun createPhoto(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.GERMANY).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            args.putString("dir", absolutePath)
        }
    }

    /**
     * Returns the respective fragment to be displayed in the ViewPager.
     * (Deprecated but could not get the new version to work with viewpager).
     */
    private inner class ScreenSlidePagerAdapter(fm: FragmentManager, val cacheData: CacheData) : FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int = NUMBER_FRAGMENTS

        override fun getItem(position: Int): Fragment{
            return when (position) {
                0 -> {
                    SliderImgFragment.newInstance(cacheData)
                }
                1 -> {
                    fragment = SliderMapFragment.newInstance(cacheData)
                    fragment
                }
                else -> {
                    SliderDetailsFragment()
                }
            }
        }

        /**
         * Sets the title of each tab in the tablayout.
         */
        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> "Image"
                1 -> "Map"
                else -> "Details"
            }
        }
    }
}

