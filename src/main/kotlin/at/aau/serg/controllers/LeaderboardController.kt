package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/leaderboard")
class LeaderboardController(
    private val gameResultService: GameResultService
) {

    @GetMapping
    fun getLeaderboard( @RequestParam(required = false) rank: Int? = null): List<GameResult> {


        //2.2.1 Sortierung
        //  gameResultService.getGameResults().sortedWith(compareBy({ -it.score }, {it.timeInSeconds }))

        val rankBand = 3

        val Leaderboard = gameResultService.getGameResults().sortedWith(
            compareBy({ -it.score }, { it.timeInSeconds })
        )

        if (rank == null)
        {
         return Leaderboard
        }

        if (rank <= 0 ) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid rank, minimum zero.")
        }
        if ( rank > Leaderboard.size) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid rank, higher then player count.")
        }


        val start = maxOf(0, rank - (1+rankBand))

        val end = minOf(Leaderboard.size, rank + (rankBand))

        return Leaderboard.subList(start, end)
    }
}