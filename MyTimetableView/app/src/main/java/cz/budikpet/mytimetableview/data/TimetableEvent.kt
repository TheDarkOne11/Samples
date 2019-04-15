package cz.budikpet.mytimetableview.data

import org.joda.time.DateTime
import java.util.*

data class TimetableEvent(
    val siriusId: Int?,
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