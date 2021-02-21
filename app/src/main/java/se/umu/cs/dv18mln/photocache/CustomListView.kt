package se.umu.cs.dv18mln.photocache


import android.app.Activity
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import coil.api.load

/**
 * Class to represent a custom listview where the data of the pre-made caches
 * are stored. Each row in the list have a image, title and short description
 * of the cache.
 *
 * @author dv18mln
 */
class CustomListView(context: Activity, val cacheData: Array<CacheData>)
    : ArrayAdapter<CacheData>(context, R.layout.listview_layout, cacheData) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var rowView: View
        val viewHolder: ViewHolder

        if(convertView == null){
            val inflater = LayoutInflater.from(context)
            rowView = inflater.inflate(R.layout.listview_layout, convertView, true)
            viewHolder = ViewHolder(rowView)
            rowView.tag = viewHolder
        }
        else{
            rowView = convertView
            viewHolder = rowView.tag as ViewHolder
        }

        viewHolder.txtTitle.text = cacheData[position].title
        viewHolder.txtDesc.text = cacheData[position].desc
        if(cacheData[position].imgArray != null){
            val bitmap = BitmapFactory.decodeByteArray(cacheData[position].imgArray, 0,
                cacheData[position].imgArray!!.size)
            viewHolder.imgView.load(bitmap)
        }

        return rowView
    }

    private class ViewHolder(v: View){
        val txtTitle = v.findViewById<TextView>(R.id.txtTitle)
        val txtDesc = v.findViewById<TextView>(R.id.txtDesc)
        val imgView = v.findViewById<ImageView>(R.id.imgView)

    }

}