package se.umu.cs.dv18mln.photocache


import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Class to store data of each cache.
 * @param title Title of the cache.
 * @param desc The description of the cache.
 * @param imgId The id of the image belonging to the cache.
 * @param x_pos Latitude coordinates of the cache.
 * @param y_pos Longitude coordinates of the cache.
 */

class CacheData(val id: String?) : Parcelable {
    var title: String? = null
    var desc: String? = null
    var imgId: String? = null
    var imgArray: ByteArray? = null
    var xPos: Double = 0.0
    var yPos: Double = 0.0
    var userScore: Int = 0

    constructor(parcel: Parcel) : this(parcel.readString()) {
        title = parcel.readString()
        desc = parcel.readString()
        imgId = parcel.readString()
        imgArray = parcel.createByteArray()
        xPos = parcel.readDouble()
        yPos = parcel.readDouble()
        userScore = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(desc)
        parcel.writeString(imgId)
        parcel.writeByteArray(imgArray)
        parcel.writeDouble(xPos)
        parcel.writeDouble(yPos)
        parcel.writeInt(userScore)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CacheData> {
        override fun createFromParcel(parcel: Parcel): CacheData {
            return CacheData(parcel)
        }

        override fun newArray(size: Int): Array<CacheData?> {
            return arrayOfNulls(size)
        }
    }
}