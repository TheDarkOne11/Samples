package cz.budikpet.mytimetableview.data

import org.joda.time.DateTime
import org.joda.time.Interval
import java.util.*

data class TimetableEvent(
    val siriusId: Int? = null,
    var googleId: Long? = null,
    val room: String,
    val acronym: String,
    val fullName: String = acronym,
    val event_type: EventType,
    val starts_at: DateTime,
    val ends_at: DateTime,
    var deleted: Boolean = false,
    val changed: Boolean = false,
    val capacity: Int,
    val occupied: Int = 0,
    val teachers: ArrayList<String>,
    val students: ArrayList<String>? = null
) {

    fun overlapsWith(timetableEvent: TimetableEvent): Boolean {
        val interval1 = Interval(this.starts_at.millis, this.ends_at.millis)
        val interval2 = Interval(timetableEvent.starts_at.millis, timetableEvent.ends_at.millis)

        return interval1.overlaps(interval2)
    }

    override fun hashCode(): Int {
        return Objects.hash(siriusId)
    }

    override fun equals(other: Any?): Boolean {
        if (other is TimetableEvent) {
            return siriusId == other.siriusId
        }

        return super.equals(other)
    }
}