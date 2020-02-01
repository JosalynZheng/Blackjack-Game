package com.example.cse438.blackjack

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.cse438.blackjack.model.Player
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_leaderboard.*
import kotlinx.android.synthetic.main.leader_list_item.view.*
import com.example.cse438.blackjack.MainActivity

class LeaderboardActivity : AppCompatActivity() {

    var leaderList = ArrayList<Player>()
    val adapter = LeaderAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        leaderboard_list.layoutManager = LinearLayoutManager(this)
        leaderboard_list.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        leaderboard_list.adapter = adapter

        val db = FirebaseFirestore.getInstance()
        db.collection("users").orderBy("win", Query.Direction.DESCENDING).addSnapshotListener {
            snapshots, error ->
            val leaders = ArrayList<Player>()

            for (doc in snapshots?.documents!!) {
                var player = Player()
                for (data in doc.data!!) {
                    player.assignValue(data.key, data.value)
                }
                leaderList.add(player)
            }

            runOnUiThread {
                adapter.notifyDataSetChanged()
            }
        }


        back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }




    inner class LeaderAdapter: RecyclerView.Adapter<LeaderAdapter.LeaderViewHolder>() {

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): LeaderViewHolder {
            val view = LayoutInflater.from(p0.context).inflate(R.layout.leader_list_item, p0, false)
            return LeaderViewHolder(view)
        }


        override fun onBindViewHolder(p0: LeaderViewHolder, p1: Int) {
            p0.firstName.text = "First Name: " + leaderList[p1].getFirstName()
            p0.lastName.text = "Last Name: " + leaderList[p1].getLastName()
            p0.winTimes.text = "Wins: " + leaderList[p1].getWinTimes()
            p0.loseTimes.text = "Lose: " + leaderList[p1].getLoseTimes()
            if (leaderList[p1].getLoseTimes() == 0) {
                if (leaderList[p1].getWinTimes() == 0) {
                    p0.ratio.text = "Ratio: 0"
                } else {
                    p0.ratio.text = "Ratio: Never lose"
                }
            } else {
                p0.ratio.text = "Ratio: " + "%.1f".format(leaderList[p1].getWinTimes().toFloat() / leaderList[p1].getLoseTimes().toFloat())
            }
        }


        override fun getItemCount(): Int {
            return leaderList.size
        }



        inner class LeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val leader = itemView
            var firstName: TextView = itemView.first_name
            var lastName: TextView = itemView.last_name
            var winTimes: TextView = itemView.win_times
            var loseTimes: TextView = itemView.lose_times
            var ratio: TextView = itemView.ratio
        }


        fun addLeader(leaders: List<Player>) {
            leaderList.apply {
                clear()
                val sortedLeaders = leaders.sortedWith(comparator = kotlin.Comparator { o1, o2 ->
                    var ratio = {
                        it: Player ->
                        when {
                            it.getLoseTimes() == 0 -> when (it.getWinTimes()) {
                                0 -> 0f
                                else -> Float.MAX_VALUE
                            }
                            else -> it.getWinTimes().toFloat() / it.getLoseTimes().toFloat()
                        }
                    }

                    ratio(o1).compareTo(ratio(o2))
                }).asReversed()

                addAll(sortedLeaders)
            }
            notifyDataSetChanged()
        }
    }
}
