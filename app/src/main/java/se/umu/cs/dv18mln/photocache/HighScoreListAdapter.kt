package se.umu.cs.dv18mln.photocache

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class HighScoreListAdapter(context: Activity, val highscoreData: Array<HighscoreData>) :
    ArrayAdapter<HighscoreData>(context, R.layout.listview_highscore, highscoreData) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var rowView: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            rowView = inflater.inflate(R.layout.listview_highscore, convertView, true)
            viewHolder = ViewHolder(rowView)
            rowView.tag = viewHolder
        } else {
            rowView = convertView
            viewHolder = rowView.tag as ViewHolder
        }

        viewHolder.txtUsername.text = highscoreData[position].username
        viewHolder.txtHighscore.text = highscoreData[position].score.toString()
        return rowView
    }

    private class ViewHolder(v: View) {
        val txtUsername = v.findViewById<TextView>(R.id.username)
        val txtHighscore = v.findViewById<TextView>(R.id.highscore)

    }


}