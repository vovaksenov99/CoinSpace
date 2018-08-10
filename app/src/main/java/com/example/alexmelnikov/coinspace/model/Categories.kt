package com.example.alexmelnikov.coinspace.model

import android.content.Context
import com.example.alexmelnikov.coinspace.R

enum class Category {
    MEAL {
        override fun getStringResource(): Int {
            return R.string.meal
        }

        override fun getIconResource(): Int {
            return R.drawable.meal
        }

    },
    EDUCATION {
        override fun getStringResource(): Int {
            return R.string.education
        }

        override fun getIconResource(): Int {
            return R.drawable.educational
        }
    },
    OTHER {
        override fun getStringResource(): Int {
            return R.string.other
        }

        override fun getIconResource(): Int {
            return R.drawable.other
        }
    },
    TRAVEL {
        override fun getStringResource(): Int {
            return R.string.travel
        }

        override fun getIconResource(): Int {
            return R.drawable.travel
        }
    },
    FOOD {
        override fun getStringResource(): Int {
            return R.string.food
        }

        override fun getIconResource(): Int {
            return R.drawable.food
        }
    },
    HOUSE {
        override fun getStringResource(): Int {
            return R.string.house
        }

        override fun getIconResource(): Int {
            return R.drawable.house
        }
    },
    CLOTH {
        override fun getStringResource(): Int {
            return R.string.cloth
        }

        override fun getIconResource(): Int {
            return R.drawable.cloth
        }
    },
    FUN {
        override fun getStringResource(): Int {
            return R.string.funy
        }

        override fun getIconResource(): Int {
            return R.drawable.funy
        }
    },
    TRANSPORT {
        override fun getStringResource(): Int {
            return R.string.transport
        }

        override fun getIconResource(): Int {
            return R.drawable.transport
        }
    },
    HEALTH {
        override fun getStringResource(): Int {
            return R.string.health
        }

        override fun getIconResource(): Int {
            return R.drawable.health
        }
    };

    abstract fun getIconResource(): Int
    abstract fun getStringResource(): Int
}


fun getCategoryByString(categoryIndex: String): Category {
    for (category in Category.values())
        if (category.toString() == categoryIndex)
            return category
    throw Exception("Not valid currency index. Invalid string '$categoryIndex'")
}
