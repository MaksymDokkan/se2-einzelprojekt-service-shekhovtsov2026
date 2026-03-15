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

class GameResultControllerTests {

    private lateinit var mockedService: GameResultService
    private lateinit var controller: GameResultController

    @BeforeEach
    fun setup() {
        mockedService = mock<GameResultService>()
        controller = GameResultController(mockedService)
    }

    @Test
    fun test_getGameResult() {
        val expectedResult = GameResult(1, "TestPlayer", 50, 20.0)
        whenever(mockedService.getGameResult(1)).thenReturn(expectedResult)

        val actualResult = controller.getGameResult(1)

        verify(mockedService).getGameResult(1)
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun test_getAllGameResults() {
        val expectedList = listOf(GameResult(1, "TestPlayer", 50, 20.0))
        whenever(mockedService.getGameResults()).thenReturn(expectedList)

        val actualList = controller.getAllGameResults()

        verify(mockedService).getGameResults()
        assertEquals(expectedList, actualList)
    }

    @Test
    fun test_addGameResult() {
        val newResult = GameResult(1, "TestPlayer", 50, 20.0)

        controller.addGameResult(newResult)

        verify(mockedService).addGameResult(newResult)
    }

    @Test
    fun test_deleteGameResult() {
        val targetId = 1L

        controller.deleteGameResult(targetId)

        verify(mockedService).deleteGameResult(targetId)
    }


}