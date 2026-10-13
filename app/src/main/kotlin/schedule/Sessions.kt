package schedule

import java.time.LocalDate

class Lecture(
    override val date: LocalDate,
    override val topic: String,
    val location: String,
    teachers: List<String>,
) : Session {
    private val _savedTeachers: List<String> = teachers.toList()
    val teachers: List<String> get() = _savedTeachers.toList()

    init {
        requireDate(date)
        requireText("topic", topic)
        requireLocation(location)
        requireTeachers(_savedTeachers, min = 1)
    }

    override fun equals(other: Any?) = other is Lecture && date == other.date &&
            topic == other.topic && location == other.location && _savedTeachers == other._savedTeachers
    override fun hashCode() = listOf(date, topic, location, _savedTeachers).hashCode()
    override fun toString() = "Lecture(date=$date, topic=$topic, location=$location, teachers=$_savedTeachers)"
}

class Practice(
    override val date: LocalDate,
    override val topic: String,
    val location: String,
    teachers: List<String>,
) : Session {
    private val _savedTeachers: List<String> = teachers.toList()
    val teachers: List<String> get() = _savedTeachers.toList()

    init {
        requireDate(date)
        requireText("topic", topic)
        requireLocation(location)
        requireTeachers(_savedTeachers, min = 1)
    }

    override fun equals(other: Any?) = other is Practice && date == other.date &&
            topic == other.topic && location == other.location && _savedTeachers == other._savedTeachers
    override fun hashCode() = listOf(date, topic, location, _savedTeachers).hashCode()
    override fun toString() = "Practice(date=$date, topic=$topic, location=$location, teachers=$_savedTeachers)"
}

class Exam(
    override val date: LocalDate,
    override val topic: String,
    val location: String,
    teachers: List<String>,
) : Session {
    private val _savedTeachers: List<String> = teachers.toList()
    val teachers: List<String> get() = _savedTeachers.toList()

    init {
        requireDate(date)
        requireText("topic", topic)
        requireLocation(location)
        requireTeachers(_savedTeachers, min = 2)
    }

    override fun equals(other: Any?) = other is Exam && date == other.date &&
            topic == other.topic && location == other.location && _savedTeachers == other._savedTeachers
    override fun hashCode() = listOf(date, topic, location, _savedTeachers).hashCode()
    override fun toString() = "Exam(date=$date, topic=$topic, location=$location, teachers=$_savedTeachers)"
}

class Cancelled(
    override val date: LocalDate,
    override val topic: String,
) : Session {
    init {
        requireDate(date)
        requireText("topic", topic)
    }

    override fun equals(other: Any?) = other is Cancelled && date == other.date && topic == other.topic
    override fun hashCode() = listOf(date, topic).hashCode()
    override fun toString() = "Cancelled(date=$date, topic=$topic)"
}
