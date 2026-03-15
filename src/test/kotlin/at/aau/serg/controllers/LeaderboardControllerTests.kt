package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import kotlin.test.Test
import kotlin.test.assertEquals
import org.mockito.Mockito.`when` as whenever // when is a reserved keyword in Kotlin

class LeaderboardControllerTests {

    private lateinit var mockedService: GameResultService
    private lateinit var controller: LeaderboardController

    @BeforeEach
    fun setup() {
        mockedService = mock<GameResultService>()
        controller = LeaderboardController(mockedService)
    }

    @Test
    fun test_getLeaderboard_correctScoreSorting() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 15, 10.0)
        val third = GameResult(3, "third", 10, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        val res: List<GameResult> = controller.getLeaderboard()

        verify(mockedService).getGameResults()
        assertEquals(3, res.size)
        assertEquals(first, res[0])
        assertEquals(second, res[1])
        assertEquals(third, res[2])
    }

    @Test
    fun test_getLeaderboard_sameScore_CorrectSpeedSorting() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 20, 10.0)
        val third = GameResult(3, "third", 20, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        val res: List<GameResult> = controller.getLeaderboard()

        verify(mockedService).getGameResults()
        assertEquals(3, res.size)
        assertEquals(second, res[0])
        assertEquals(third, res[1])
        assertEquals(first, res[2])
    }


    private fun setupMockedLeaderboard(size: Int) {
        val results = (1..size).map {
            GameResult(it.toLong(), "Player$it", 100 - it, it.toDouble())
        }
        whenever(mockedService.getGameResults()).thenReturn(results)
    }


    @Test
    fun test_getLeaderboard_invalidRankZero_throwsException() {
        setupMockedLeaderboard(1)

        val exception = assertThrows<ResponseStatusException> {
            controller.getLeaderboard(0)
        }
        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
    }

    @Test
    fun test_getLeaderboard_invalidRankNegative_throwsException() {
        setupMockedLeaderboard(1)

        val exception = assertThrows<ResponseStatusException> {
            controller.getLeaderboard(-5)
        }
        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
    }

    @Test
    fun test_getLeaderboard_invalidRankTooHigh_throwsException() {
        setupMockedLeaderboard(1)

        val exception = assertThrows<ResponseStatusException> {
            controller.getLeaderboard(2)
        }
        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
    }

    @Test
    fun test_getLeaderboard_rankInMiddle_returnsSevenPlayers() {

        setupMockedLeaderboard(10)

        val res = controller.getLeaderboard(5)

        assertEquals(7, res.size)
        assertEquals("Player2", res.first().playerName)
        assertEquals("Player5", res[3].playerName)
        assertEquals("Player8", res.last().playerName)
    }

    @Test
    fun test_getLeaderboard_rankNearTop_hitsStartBoundary() {
        setupMockedLeaderboard(10)

        val res = controller.getLeaderboard(2)

        assertEquals(5, res.size)
        assertEquals("Player1", res.first().playerName)
        assertEquals("Player5", res.last().playerName)
    }

    @Test
    fun test_getLeaderboard_rankNearBottom_hitsEndBoundary() {
        setupMockedLeaderboard(10)

        val res = controller.getLeaderboard(9)

        assertEquals(5, res.size)
        assertEquals("Player6", res.first().playerName)
        assertEquals("Player10", res.last().playerName)
    }




}