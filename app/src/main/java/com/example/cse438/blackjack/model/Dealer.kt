package com.example.cse438.blackjack.model

class Dealer {
    // we need some fields and methods for the dealer
    // score of the dealer
    private var score = 0
    // we need an arrayList to store all cards
    private var cards = ArrayList<Card>()

    // get score
    public fun getScore(): Int {
        return score
    }


    // calculate score
    private fun calculateScore(newScore: Int) {
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
    }

    // add a new card and calculate score immediately
    public fun addCard(card: Card) {
        cards.add(card)
        calculateScore(card.getCardValue())
    }

}