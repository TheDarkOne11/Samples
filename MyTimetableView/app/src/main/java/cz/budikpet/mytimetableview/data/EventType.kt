package cz.budikpet.mytimetableview.data

enum class EventType() {
    ASSESSMENT,
    COURSE_EVENT,
    EXAM,
    LABORATORY,
    LECTURE,
    TUTORIAL,
    TEACHER_TIMETABLE_SLOT,
    OTHER;

    override fun toString(): String {
        return super.toString().toLowerCase()
    }

}