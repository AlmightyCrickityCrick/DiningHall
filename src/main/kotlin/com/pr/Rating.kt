package com.pr

class Rating:Thread() {
    var ratings= ArrayList<Int>()
    var currentRating:Float = 0f


    fun calculateRating(){
        currentRating = 0f
        var count = 0
        for (i in ratings) {
            currentRating+=i
            count++
        }
        println(currentRating/count)
    }

    fun addRating(max:Int, actual:Long){
        if(actual<=max) ratings.add(5)
        else if (actual <= max*1.1)ratings.add(4)
        else if (actual <= max*1.2)ratings.add(3)
        else if (actual <= max*1.3)ratings.add(2)
        else if (actual <= max*1.4)ratings.add(1)
        else ratings.add(0)

        calculateRating()

    }

}