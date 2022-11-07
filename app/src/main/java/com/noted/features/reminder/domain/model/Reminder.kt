package com.noted.features.reminder.domain.model

import java.time.LocalDateTime

data class Reminder(
    val dateTimeOfFirstRemind: LocalDateTime = LocalDateTime.MAX,
    val repeat: Repeat = Repeat.Once,
) {
    companion object {
        fun from(day: Day, time: Time, repeat: Repeat): Reminder {
            val localDateTime = LocalDateTime.now()
                .plusDays(day.toLong())
                .withHour(time.getHour())
                .withMinute(time.getMinute())
            return Reminder(
                dateTimeOfFirstRemind = localDateTime,
                repeat = repeat,
            )
        }
    }
}

enum class Day {
    Today {
        override fun toLong() = 0L
    },
    Tomorrow {
        override fun toLong() = 1L
    },
    NextWeek {
        override fun toLong() = 7L
    },
    OtherDay {
        var days = 0L
        override fun toLong() = days
    };

    abstract fun toLong(): Long
}

enum class Time {
    Morning {
        override fun getHour() = 8
        override fun getMinute() = 0
    },
    Day {
        override fun getHour() = 13
        override fun getMinute() = 0
    },
    Evening {
        override fun getHour() = 18
        override fun getMinute() = 0
    },
    Night {
        override fun getHour() = 23
        override fun getMinute() = 0
    },
    Other {
        val hours: Int = 0
        val minutes: Int = 0

        override fun getHour() = hours
        override fun getMinute() = minutes
    };

    abstract fun getHour(): Int
    abstract fun getMinute(): Int
}

enum class Repeat {
    Once,
    Day,
    Week,
    Month,
    Year,
    Interval {
        var interval: Int = 0
    };
}
