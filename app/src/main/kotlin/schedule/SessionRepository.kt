package schedule

interface SessionRepository {
    fun add(session: Session)
    fun addAll(sessions: List<Session>)
    fun all(): List<Session>
}

class InMemorySessionRepository : SessionRepository {
    private val items = mutableListOf<Session>()

    override fun add(session: Session) {
        items.add(session)
    }

    override fun addAll(sessions: List<Session>) {
        items.addAll(sessions)
    }

    override fun all(): List<Session> = items.toList()
}
