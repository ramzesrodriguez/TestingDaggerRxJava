package it.droidcon.testingdaggerrxjava.test4

import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Observable
import io.reactivex.Single
import it.droidcon.testingdaggerrxjava.TestSchedulerRule
import it.droidcon.testingdaggerrxjava.core.UserInteractor
import it.droidcon.testingdaggerrxjava.core.UserStats
import it.droidcon.testingdaggerrxjava.core.gson.Badge
import it.droidcon.testingdaggerrxjava.core.gson.StackOverflowService
import it.droidcon.testingdaggerrxjava.core.gson.User
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import java.util.concurrent.TimeUnit

class UserInteractorTest {
    @get:Rule var mockitoRule = MockitoJUnit.rule()

    @get:Rule var schedulerRule = TestSchedulerRule()

    val stackOverflowService: StackOverflowService = mock()

    @InjectMocks lateinit var userInteractor: UserInteractor

    @Test fun shouldLoadUsers() {
        `when`(stackOverflowService.getTopUsers()).thenReturn(
                Observable.fromArray(
                        User.create(1, 50, "user1"),
                        User.create(2, 30, "user2")
                ).toList())

        `when`(stackOverflowService.getBadges(1)).thenReturn(
                Single.just(arrayOf("badge1").map { Badge(it) })
                        .delay(2, TimeUnit.SECONDS)
        )
        `when`(stackOverflowService.getBadges(2)).thenReturn(
                Single.just(arrayOf("badge2", "badge3").map { Badge(it) })
                        .delay(1, TimeUnit.SECONDS)
        )

        val testObserver = userInteractor.loadUsers().test()

        schedulerRule.testScheduler
                .advanceTimeBy(2, TimeUnit.SECONDS)

        val l = testObserver.assertNoErrors().values()
        assertThat(l[0]).containsExactly(
                UserStats.create(1, 50, "user1", "badge1"),
                UserStats.create(2, 30, "user2", "badge2", "badge3")
        )
    }
}