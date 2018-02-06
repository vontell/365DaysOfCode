package org.vontech.morrow

/**
 * An enumerated type representing whether this task is for Today, Tomorrow, Overmorrow, or already
 * finished
 * @author Aaron Vontell (aaron@vontech.org), on behalf of Vontech Software, LLC
 */
enum class Day {

    TODAY {
        override fun toString(): String { return "Today" }
    },
    TOMORROW {
        override fun toString(): String { return "Tomorrow" }
    },
    OVERMORROW {
        override fun toString(): String { return "Overmorrow" }
    },
    COMPLETED {
        override fun toString(): String { return "Completed" }
    };

    private val drawables =
        arrayListOf(R.drawable.today, R.drawable.tomorrow, R.drawable.overmorrow, R.drawable.save)

    fun getDrawable(): Int {
        return drawables[this.ordinal]
    }

}