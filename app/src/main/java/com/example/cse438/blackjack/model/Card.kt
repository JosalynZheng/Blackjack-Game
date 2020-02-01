package com.example.cse438.blackjack.model

class Card(cardName: String) {
    private val cardName = cardName
    private var cardValue = 0

    // get cardName
    public fun getCardName(): String {
        return cardName
    }

    // get cardValue
    public fun getCardValue(): Int {
        transferCardValue()
        return cardValue
    }

    // transfer value from cardName
    private fun transferCardValue() {
        when(cardName){
            "clubs2" -> cardValue = 2
            "clubs3" -> cardValue =  3
            "clubs4" -> cardValue =  4
            "clubs5" -> cardValue =  5
            "clubs6" -> cardValue =  6
            "clubs7" -> cardValue =  7
            "clubs8" -> cardValue =  8
            "clubs9" -> cardValue =  9
            "clubs10" -> cardValue =  10
            "clubs_ace" -> cardValue =  1
            "clubs_jack" -> cardValue =  10
            "clubs_queen" -> cardValue =  10
            "clubs_king" -> cardValue =  10
            "diamonds2" -> cardValue =  2
            "diamonds3" -> cardValue =  3
            "diamonds4" -> cardValue =  4
            "diamonds5" -> cardValue =  5
            "diamonds6" -> cardValue =  6
            "diamonds7" -> cardValue =  7
            "diamonds8" -> cardValue =  8
            "diamonds9" -> cardValue =  9
            "diamonds10" -> cardValue =  10
            "diamonds_ace" -> cardValue =  1
            "diamonds_jack" -> cardValue =  10
            "diamonds_queen" -> cardValue =  10
            "diamonds_king" -> cardValue =  10
            "hearts2" -> cardValue =  2
            "hearts3" -> cardValue =  3
            "hearts4" -> cardValue =  4
            "hearts5" -> cardValue =  5
            "hearts6" -> cardValue =  6
            "hearts7" -> cardValue =  7
            "hearts8" -> cardValue =  8
            "hearts9" -> cardValue =  9
            "hearts10" -> cardValue =  10
            "hearts_ace" -> cardValue =  1
            "hearts_jack" -> cardValue =  10
            "hearts_queen" -> cardValue =  10
            "hearts_king" -> cardValue =  10
            "spades2" -> cardValue =  2
            "spades3" -> cardValue =  3
            "spades4" -> cardValue =  4
            "spades5" -> cardValue =  5
            "spades6" -> cardValue =  6
            "spades7" -> cardValue =  7
            "spades8" -> cardValue =  8
            "spades9" -> cardValue =  9
            "spades10" -> cardValue =  10
            "spades_ace" -> cardValue =  1
            "spades_jack" -> cardValue =  10
            "spades_queen" -> cardValue =  10
            "spades_king" -> cardValue =  10
        }
    }
}