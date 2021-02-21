package se.umu.cs.dv18mln.photocache.Fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import coil.api.load
import com.bumptech.glide.Glide
import se.umu.cs.dv18mln.photocache.CacheData
import se.umu.cs.dv18mln.photocache.R

/**
 * Fragment to store the image fragment. The image is displayed in the
 * DetailsActivity. The image is passed as an id.
 *
 * Update: The ImgFragment also implements a scrollview
 * to be able to show the image and details in the same fragment.
 * This is used in the portrait mode and to separate a scrollview
 * and the MapFragment which did not cooperate when in the same view.
 */
class SliderImgFragment : Fragment() {

    /**
     * Uses companion object and new instance to not skip
     * re-set the image on every changed fragment.
     */
    companion object{
        fun newInstance(cacheData: CacheData):Fragment{
            val args = Bundle()
            args.putSerializable("cacheData", cacheData)
            val s = SliderImgFragment()
            s.arguments = args
            return s
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_image_page, container, false)
    }

    /**
     * Image id is stored in the arguments passed to the newInstance.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        super.onActivityCreated(savedInstanceState)
        val imgView = requireView().findViewById<ImageView>(R.id.imgView)
        val cacheData = arguments?.get("cacheData") as CacheData

        if(cacheData.imgArray != null){
            val bitmap = BitmapFactory.decodeByteArray(cacheData.imgArray, 0,
                cacheData.imgArray!!.size)
            Glide.with(this).load(bitmap).into(imgView)
        }

        requireView().findViewById<TextView>(R.id.txtDetails).text = cacheData.desc

    }

}