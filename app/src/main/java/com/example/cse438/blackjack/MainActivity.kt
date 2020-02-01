package com.example.cse438.blackjack

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.GestureDetectorCompat
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import com.example.cse438.blackjack.fragment.NoConnectionFragment
import com.example.cse438.blackjack.model.Card
import com.example.cse438.blackjack.model.Dealer
import com.example.cse438.blackjack.model.Player
import com.example.cse438.blackjack.util.CardRandomizer
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val player = Player()
    private val dealer = Dealer()
    private var cardList = ArrayList<Card>()
    private var cardListID = ArrayList<Int>()
    private val documentReference = FirebaseFirestore.getInstance().document("users/${firebaseAuth?.currentUser?.uid}")
    private var playerCards = ArrayList<ImageView>()
    private var dealerCards = ArrayList<ImageView>()
    private var backID = 0
    private lateinit var mDetector : GestureDetectorCompat
    private var resultCode = 0 // 1 for win, 2 for tie, 3 for lose
    private var playerIndex = 0
    private var dealerIndex = 0
    private var flag: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mDetector = GestureDetectorCompat(this, MyGestureListener())

        // check internet connection
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        // Load Fragment into View
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()

        if (networkInfo == null) {
            Log.e("NETWORK", "not connected")
            ft.add(R.id.frag_placeholder, NoConnectionFragment())
        }

        // check authorization
        if (firebaseAuth?.currentUser == null) {
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
        } else {

        }

        // display win and lose times
        displayWinAndLose()

        // add event listener to log out button
        logout_button.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
        }

        // add event listener to new game button
        new_game_button.setOnClickListener {
            finish()
            startActivity(intent)
        }

        leaderboard_button.setOnClickListener {
            var intent = Intent(this, LeaderboardActivity::class.java)
            startActivity(intent)
        }

    }


    override fun onResume() {
        super.onResume()
        displayWinAndLose()
        flag = false
        val randomizer = CardRandomizer()
        cardList = ArrayList<Card>()
        cardListID = ArrayList()
        playerIndex = 0
        dealerIndex = 0
        playerCards = arrayListOf(playerCard1, playerCard2, playerCard3, playerCard4, playerCard5)
        dealerCards = arrayListOf(dealerCard1, dealerCard2, dealerCard3, dealerCard4, dealerCard5)
        // get a list of IDs using randomizer
        cardListID = randomizer.getIDs(this)
        // transfer ID to cards
        for (i in cardListID.indices) {
            // find the name of the card
            val cardName = resources.getResourceEntryName(cardListID[i])
            // we've got a whole list of cards
            cardList.add(Card(cardName))
        }
        // distribute cards to player/ dealer
        for (i in 0 until 4) {
            // get a random card from the list
            val rand = Random().nextInt(cardList.size)
            val nextCard = cardList[rand]
            // distribute the card to player first
            if (i == 0 || i == 2) {
                // add card and calculate score
                player.addCard(nextCard)
                // display score on layout
                playerScore.text = "Score: " + player.getScore()
                // put this card on the layout
                playerCards[playerIndex++].setImageResource(cardListID[rand])
            } else {
                // distribute the card to dealer and calculate score
                dealer.addCard(nextCard)
                // set the first card back on top
                if (i/2 == 0) {
                    dealerCards[dealerIndex++].setImageResource(R.drawable.back)
                    // we need to flip the card at the end, so we have to store this ID
                    backID = cardListID[rand]
                } else {
                    // put this card on the layout
                    dealerCards[dealerIndex++].setImageResource(cardListID[rand])
                }
            }
            // after we distributed a card, we need to remove it from the list
            cardList.removeAt(rand)
            cardListID.removeAt(rand)
        }

    }

    // deal with gesture
    private inner class MyGestureListener: GestureDetector.SimpleOnGestureListener() {
        private var swipedDistance = 150
        override fun onDoubleTap(e: MotionEvent?): Boolean {
            stand()
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            // we want our motion to swipe to the right
            if (e2.x - e1.x > swipedDistance) {
                hit()
                return true
            }
            return false
        }
    }

    // we implement stand and hit separately
    // implement stand
    private fun stand() {
        if (!flag) {
            // check the dealer
            while(dealer.getScore() < 17) {
                // distribute cards until score >= 17
                val rand = Random().nextInt(cardList.size)
                val nextCard = cardList[rand]
//            add card and calculate score
                dealer.addCard(nextCard)
                // update layout
                dealerCards[dealerIndex++].setImageResource(cardListID[rand])
                // delete the card in pile
                cardList.removeAt(rand)
                cardListID.removeAt(rand)
            }
            // compare and calculate result
            // flip back the first card
            dealerCards[0].setImageResource(backID)
            // calculate result
            calculateResult()
        } else {
            Toast.makeText(this, "Please start a new game!", Toast.LENGTH_LONG).show()
        }

    }

    // implement hit
    private fun hit() {
        if (!flag) {
            // add card to player
            val rand = Random().nextInt(cardList.size)
            val nextCard = cardList[rand]
            player.addCard(nextCard)
            // update layout
            playerCards[playerIndex++].setImageResource(cardListID[rand])
            // delete the card in pile
            cardList.removeAt(rand)
            cardListID.removeAt(rand)
            // update score
            playerScore.text = "Score: " + player.getScore()
            // check if busted
            if (player.getScore() > 21) {
                calculateResult()
            }
        } else {
            Toast.makeText(this, "Please start a new game!", Toast.LENGTH_LONG).show()
        }

    }



    // calculate result
    private fun calculateResult() {
        if (player.getScore() > 21) {
            if (dealer.getScore() > 21) {
                // no possibility here
            } else {
                result.text = "Lose!"
                resultCode = 3
            }
        } else if (player.getScore() == 21) {
            if (dealer.getScore() == 21) {
                result.text = "Tie!"
                resultCode = 2
            } else {
                result.text = "Win!"
                resultCode = 1
            }
        } else {
            if (dealer.getScore() > 21) {
                result.text = "Win!"
                resultCode = 1
            } else {
                if (dealer.getScore() > player.getScore()) {
                    result.text = "Lose!"
                    resultCode = 3
                } else if (dealer.getScore() == player.getScore()) {
                    result.text = "Tie!"
                    resultCode = 2
                } else {
                    result.text = "Win!"
                    resultCode = 1
                }
            }
        }
        flag = true
        // save to DB
        saveToDB()
    }

    // implement saving to DB
    private fun saveToDB() {
        // check the resultCode
        if (resultCode == 1) {
            // win
            player.addWin()
            documentReference.get()
                .addOnSuccessListener {
                    val totalWin = when(it.contains("win")) {
                        true -> { it.get("win", Long::class.java) as Long + 1}
                        false -> 1
                    }

                    documentReference.update("win", totalWin)
                        .addOnSuccessListener {
                            Log.d("test", "win added successfully")
                        }
                        .addOnFailureListener {
                            Log.e("test", it.localizedMessage)
                        }
                }
        } else if (resultCode == 2) {
            // tie
            // we do not need to do anything here
        } else {
            // lose
            player.addLose()
            documentReference.get()
                .addOnSuccessListener {
                    val totalLose = when(it.contains("lose")) {
                        true -> { it.get("lose", Long::class.java) as Long + 1}
                        false -> 1
                    }

                    documentReference.update("lose", totalLose)
                        .addOnSuccessListener {
                            Log.d("test", "win added successfully")
                        }
                        .addOnFailureListener {
                            Log.e("test", it.localizedMessage)
                        }
                }
        }
    }

    // display win and lose times
    private fun displayWinAndLose() {
        documentReference.get().addOnSuccessListener {
            if (it == null) {
                win.text = "Win: 0"
                lose.text = "Lose: 0"
            }

            if (!it.contains("win")) {
                win.text = "Win: 0"
            } else {
                win.text = "Win: " + it.get("win")
            }

            if (!it.contains("lose")) {
                lose.text = "Lose: 0"
            } else {
                lose.text = "Lose: " + it.get("lose")
            }
        }
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        mDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }
}
