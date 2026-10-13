package schedule

data class AuditEvent(val operation: String, val count: Int)

class AuditedSessionRepository(
    private val delegate: SessionRepository = InMemorySessionRepository(),
) : SessionRepository by delegate {

    private val _events = mutableListOf<AuditEvent>()
    val events: List<AuditEvent> get() = _events.toList()

    var totalAdded: Int = 0
        private set

    override fun add(session: Session) {
        delegate.add(session)
        _events.add(AuditEvent("add", 1))
        totalAdded += 1
    }

    override fun addAll(sessions: List<Session>) {
        delegate.addAll(sessions)
        _events.add(AuditEvent("addAll", sessions.size))
        totalAdded += sessions.size
    }
}
