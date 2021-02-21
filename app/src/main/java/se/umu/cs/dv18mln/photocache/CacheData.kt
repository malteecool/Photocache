package se.umu.cs.dv18mln.photocache

import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 * Class to store data of each cache.
 * @param title Title of the cache.
 * @param desc The description of the cache.
 * @param imgId The id of the image belonging to the cache.
 * @param x_pos Latitude coordinates of the cache.
 * @param y_pos Longitude coordinates of the cache.
 */

class CacheData(
    val title: String?, val desc: String?, val imgId: String, val imgArray: ByteArray?, val x_pos:Double,
    val y_pos:Double): Serializable{

}