package com.example.cse438.blackjack.model

class Player {
    // we need some fields and methods for each unique player
    // name are used to show on leaderboard
    private var first_name = ""
    private var last_name = ""
    // we need to store the number of win and lose for each player
    private var win = 0
    private var lose = 0
    // score of the player
    private var score = 0
    // we need an arrayList to store all cards
    private var cards = ArrayList<Card>()
    var username = ""

    // get firstName
    public fun getFirstName(): String {
        return first_name
    }

    // get lastName
    public fun getLastName(): String {
        return last_name
    }

    // get win times
    public fun getWinTimes(): Int {
        return win
    }

    // get lose times
    public fun getLoseTimes(): Int {
        return lose
    }

    // get score
    public fun getScore(): Int {
        return score
    }

    // calculate score
    private fun calculateScore(newScore: Int): Int {
        // we need to calculate a different score for 1
        if (newScore == 1) {
            // check if we add 11, will it bust?
            if (score + 11 > 21) {
                // busted, we should use it as 1
                score++
            } else {
                // we should use it as 11
                score += 11
            }
        } else {
            score += newScore
        }
        return score
    }

    // add a new card
    public fun addCard(card: Card) {
        cards.add(card)
        calculateScore(card.getCardValue())
    }

    // add one win
    public fun addWin() {
        win++
    }

    // add one lose
    public fun addLose() {
        lose++
    }

    fun assignValue(key: String, value: Any) {
        when (key) {
            "lose" -> this.lose = (value as Long).toInt()
            "first_name" -> this.first_name = value as String
            "last_name" -> this.last_name = value as String
            "win" -> this.win = (value as Long).toInt()
        }
    }
}