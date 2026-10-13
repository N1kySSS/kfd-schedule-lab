package schedule

import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RepositoryTest {
    private val day = LocalDate.of(2026, 10, 10)
    private fun session(n: Int) = Cancelled(day, "Topic $n")

    @Test
    fun snapshotDoesNotChangeAfterAdd() {
        val repo = InMemorySessionRepository()

        repo.add(session(1))

        val snapshot = repo.all()

        repo.add(session(2))

        assertEquals(1, snapshot.size)
        assertEquals(2, repo.all().size)
    }

    @Test
    fun changingSnapshotDoesNotChangeRepository() {
        val repo = InMemorySessionRepository()

        repo.add(session(1))
        repo.add(session(2))

        val snapshot = repo.all() as MutableList<Session>

        snapshot.clear()

        assertEquals(2, repo.all().size)
    }

    @Test
    fun addAllKeepsOrder() {
        val repo = InMemorySessionRepository()

        val a = session(1)
        val b = session(2)
        val c = session(3)

        repo.addAll(listOf(a, b, c))

        assertEquals(listOf(a, b, c), repo.all())
    }

    @Test
    fun duplicatesAreStoredSeparately() {
        val repo = InMemorySessionRepository()
        val a = session(1)

        repo.add(a)
        repo.addAll(listOf(a, a))

        assertEquals(3, repo.all().size)
    }

    @Test
    fun auditCountsSingleAndBatchAdd() {
        val repo = AuditedSessionRepository()

        repo.add(session(1))
        repo.addAll(listOf(session(2), session(3), session(4)))

        assertEquals(4, repo.all().size)
        assertEquals(4, repo.totalAdded)
        assertEquals(listOf(AuditEvent("add", 1), AuditEvent("addAll", 3)), repo.events)
    }

    @Test
    fun emptyAddAllLogsZero() {
        val repo = AuditedSessionRepository()

        repo.addAll(emptyList())

        assertEquals(listOf(AuditEvent("addAll", 0)), repo.events)
    }

    @Test
    fun allDoesNotLogAnything() {
        val repo = AuditedSessionRepository()

        repo.add(session(1))
        repo.all()

        assertEquals(1, repo.events.size)
    }
}
