package se.umu.cs.dv18mln.photocache.Fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import se.umu.cs.dv18mln.photocache.CacheData
import se.umu.cs.dv18mln.photocache.R

/**
 * Fragment to represent a map. The map used is the google
 * map along with its API.
 *
 * The map sets a marker on the location where the picture of
 * the cache is taken. The map also uses the users location
 * to display the device location on the map.
 *
 * @author dv18mln
 */
class SliderMapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var cacheData: CacheData
    private lateinit var fusedLocation: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var userMarker: Marker
    private lateinit var imageMarker: Marker
    private val requestingLocationUpdates = true

    /**
     * Using companion object to not have to re-create the map
     * on new instances of the PageSlider.
     */
    companion object {
        fun newInstance(cacheData: CacheData): SliderMapFragment {
            val args = Bundle()
            args.putParcelable("cacheData", cacheData)
            val s = SliderMapFragment()
            s.arguments = args
            return s
        }
    }

    /**
     * Sets the user locaion and starts a request to update
     * the users position every 10 seconds.
     * These requests are stopped when the application
     * becomes in the background or closed.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocation = LocationServices.getFusedLocationProviderClient(requireActivity())
        locationRequest = getLocationRequest()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    if(::userMarker.isInitialized){
                        userMarker.remove()
                    }

                    setMarker(location)
                }
            }
        }
    }

    /**
     * Sets the fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_map_page, container, false)
    }

    /**
     * Initializes the mapFragment.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    /**
     * Calls the start of location updates if the app becomes active.
     */
    override fun onResume() {
        super.onResume()
        if(requestingLocationUpdates) startLocationUpdates()
    }

    /**
     * Starts the requests of the updates of the user position.
     */
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocation.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }

    /**
     * Calls the stop the requests if the application becomes inactive.
     */
    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    /**
     * Stops/removes the location updates.
     */
    private fun stopLocationUpdates() {
        fusedLocation.removeLocationUpdates(locationCallback)
    }

    /**
     * To be able to use the application/this activity the user
     * need to accept that the application uses the devices position.
     *
     * If the user accepts these requests the application goes on like normal.
     * If the user denies, the activity calls finish and the user is returned
     * to the main activity with a message.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission was granted, proceed to the normal flow.
            return
        } else {
            Toast.makeText(this.context, "Need permission to display the map",
                Toast.LENGTH_LONG).show()
            requireActivity().finish()
        }
    }

    /**
     * Checks if the permission is already granted.
     *
     * @return True if the user have accepted all permissions, else
     * false and the requestPermissionResult will be called.
     */
    private fun isPermissionGiven():Boolean{
        return (ActivityCompat.checkSelfPermission(requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager
            .PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    /**
     * Return a request of the users location on a specifed interval.
     * The interval is set to 10 000 ms (10 sec).
     */
    private fun getLocationRequest():LocationRequest{
        return LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000
            fastestInterval = 5000
        }
    }

    /**
     * Sets the marker of the users position. The marker used is custom made
     * icon. The marker is removed and set every time the location is updated
     * through the locationRequest.
     */
    private fun setMarker(location: Location){
        val icon = BitmapDescriptorFactory.fromBitmap(Bitmap
            .createScaledBitmap(BitmapFactory.decodeResource(this.resources,
                R.drawable.maps_user_icon
            ), 50, 50 , false))

        userMarker = mMap.addMarker(
            MarkerOptions()
                .position(LatLng(location.latitude, location.longitude))
                .title("Your location")
                .icon(icon))

    }

    /**
     * Checks if the permission to use the users location has been granted,
     * else ask for it.
     *
     * Sets the marker of the cache. The coordinates is passed through a
     * cacheData object.
     *
     * The camera of the map is also moved to the location of the cache.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        if(!isPermissionGiven()){
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION),
                PackageManager.PERMISSION_GRANTED)
        }

        cacheData = arguments?.get("cacheData") as CacheData
        mMap = googleMap
        val marker = LatLng(cacheData.xPos, cacheData.yPos)
        imageMarker = mMap.addMarker(MarkerOptions().position(marker).title("Location of image"))
        val cpos = CameraPosition.Builder().target(marker).zoom(14.toFloat()).build()
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cpos))
    }

    /**
     * Gets the location of the users position as a LatLng.
     * @return The user location.
     */
    fun getUserLocation():LatLng?{
        if(!::userMarker.isInitialized){
            return null
        }
        return userMarker.position
    }

    /**
     * @return The location of the cahce as LatLng.
     */
    fun getMarkerLocation():LatLng{
        return imageMarker.position
    }

}