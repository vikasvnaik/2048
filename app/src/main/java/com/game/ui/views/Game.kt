package com.game.ui.views

import android.content.Context
import com.game.domain.manager.UserPrefDataManager
import java.util.*
/***
 * @author Vikas
 * @param mView - Full view which contains game and score details
 * @param userDataManager - Shared preference to store score details
 * ***/
class Game internal constructor(private val mView: GameView,private val userDataManager: UserPrefDataManager) {
    var gameState = GAME_NORMAL
    var lastGameState = GAME_NORMAL
    private var bufferGameState = GAME_NORMAL
    val numSquaresX = 4
    val numSquaresY = 4
    var grid: Grid? = null
    var aGrid: AnimationGrid? = null
    var canUndo = false
    var score: Long = 0
    var highScoreVal: Long = 0
    var lastScore: Long = 0
    private var bufferScore: Long = 0

    fun newGame() {

        if (grid == null) {
            grid = Grid(numSquaresX, numSquaresY)
        } else {
            prepareUndoState()
            saveUndoState()
            grid!!.clearGrid()
        }
        aGrid = AnimationGrid(numSquaresX, numSquaresY)
        highScoreVal = getHighScore()!!
        if (score >= highScoreVal) {
            highScoreVal = score
            recordHighScore()
        }
        score = DebugTools.startingScore
        gameState = GAME_NORMAL
        addStartTiles()
        mView.showHelp = firstRun()
        mView.refreshLastTime = true
        mView.resyncTime()
        mView.invalidate()
    }
    /***
     * Initial game view
     * 2 random tiles will be added to start the game
     * ***/
    private fun addStartTiles() {
        val debugTiles: List<Tile>? = DebugTools.generatePremadeMap()
        if (debugTiles != null) {
            for (tile in debugTiles) {
                spawnTile(tile)
            }
            return
        }
        val startTiles = 2
        for (xx in 0 until startTiles) {
            addRandomTile()
        }
    }
    /***
     * To add random tiles
     * 1> 2 tiles at the start
     * 2> 1 tile on every move
     * ***/
    private fun addRandomTile() {
        if (grid?.isCellsAvailable == true) {
            val value = if (Math.random() < 0.9) 2 else 4
            val tile = grid!!.randomAvailableCell()?.let { Tile(it, value) }
            spawnTile(tile!!)
        }
    }
    /***
     * Created tile appears with animation
     * ***/
    private fun spawnTile(tile: Tile) {
        grid?.insertTile(tile)
        aGrid?.startAnimation(
            tile.x, tile.y, SPAWN_ANIMATION,
            SPAWN_ANIMATION_TIME, MOVE_ANIMATION_TIME, null
        ) //Direction: -1 = EXPANDING
    }
    /***
     * Storing high score in sharedpreference
     * ***/
    private fun recordHighScore() {
        userDataManager.highScore = highScoreVal
    }

    /***
     * Read stored high score from sharedpreference
     * ***/
    private fun getHighScore(): Long? {
        return userDataManager.highScore
    }

    /***
     * To display help text for first time
     * ***/
    private fun firstRun(): Boolean {
        if (userDataManager.firstRun == true) {
            userDataManager.firstRun = false
            return true
        }
        return false
    }

    /***
     * Preparing new tile after merge
     * ***/
    private fun prepareTiles() {
        for (array in grid?.field!!) {
            for (tile in array) {
                if (grid?.isCellOccupied(tile) == true) {
                    if (tile != null) {
                        tile.mergedFrom = null
                    }
                }
            }
        }
    }

    /***
     * For tile movement
     * ***/
    private fun moveTile(tile: Tile, cell: Cell) {
        grid!!.field[tile.x][tile.y] = null
        grid!!.field[cell.x][cell.y] = tile
        tile.updatePosition(cell)
    }

    /***
     * To store previous state for undo option
     * ***/
    private fun saveUndoState() {
        grid?.saveTiles()
        canUndo = true
        lastScore = bufferScore
        lastGameState = bufferGameState
    }

    /***
     * preparing undo state on action to revert on click
     * ***/
    private fun prepareUndoState() {
        grid?.prepareSaveTiles()
        bufferScore = score
        bufferGameState = gameState
    }
    /***
     * On click of undo resetting to previous state
     * ***/
    fun revertUndoState() {
        if (canUndo) {
            canUndo = false
            aGrid?.cancelAnimations()
            grid?.revertTiles()
            score = lastScore
            gameState = lastGameState
            mView.refreshLastTime = true
            mView.invalidate()
        }
    }

    /***
     * On game win
     * ***/
    fun gameWon(): Boolean {
        return gameState > 0 && gameState % 2 != 0
    }

    /***
     * on game lost
     * ***/
    fun gameLost(): Boolean {
        return gameState == GAME_LOST
    }

    val isActive: Boolean
        get() = !(gameWon() || gameLost())

    /***
     * tile movement is handled
     * ***/
    fun move(direction: Int) {
        aGrid?.cancelAnimations()
        // 0: up, 1: right, 2: down, 3: left
        if (!isActive) {
            return
        }
        prepareUndoState()
        val vector = getVector(direction)
        val traversalsX = buildTraversalsX(vector)
        val traversalsY = buildTraversalsY(vector)
        var moved = false
        prepareTiles()
        for (xx in traversalsX) {
            for (yy in traversalsY) {
                val cell = Cell(xx, yy)
                val tile: Tile? = grid?.getCellContent(cell)
                if (tile != null) {
                    val positions = findFarthestPosition(cell, vector)
                    val next: Tile? = grid?.getCellContent(positions[1])
                    if (next != null && next.value === tile.value && next.mergedFrom == null) {
                        val merged = Tile(positions[1], tile.value * 2)
                        val temp = arrayOf(tile, next)
                        merged.mergedFrom = temp
                        grid?.insertTile(merged)
                        grid?.removeTile(tile)

                        // Converge the two tiles' positions
                        tile.updatePosition(positions[1])
                        val extras = intArrayOf(xx, yy)
                        aGrid?.startAnimation(
                            merged.x, merged.y, MOVE_ANIMATION,
                            MOVE_ANIMATION_TIME, 0, extras
                        ) //Direction: 0 = MOVING MERGED
                        aGrid?.startAnimation(
                            merged.x, merged.y, MERGE_ANIMATION,
                            SPAWN_ANIMATION_TIME, MOVE_ANIMATION_TIME, null
                        )

                        // Update the score
                        score = score + merged.value
                        highScoreVal = Math.max(score, highScoreVal)
                        if (score >= highScoreVal) {
                            highScoreVal = score
                            recordHighScore()
                        }

                        // The mighty 2048 tile
                        if (merged.value >= winValue() && !gameWon()) {
                            gameState = gameState + GAME_WIN // Set win state
                            endGame()
                        }
                    } else {
                        moveTile(tile, positions[0])
                        val extras = intArrayOf(xx, yy, 0)
                        aGrid?.startAnimation(
                            positions[0].x,
                            positions[0].y,
                            MOVE_ANIMATION,
                            MOVE_ANIMATION_TIME,
                            0,
                            extras
                        ) //Direction: 1 = MOVING NO MERGE
                    }
                    if (!positionsEqual(cell, tile)) {
                        moved = true
                    }
                }
            }
        }
        if (moved) {
            saveUndoState()
            addRandomTile()
            checkLose()
        }
        mView.resyncTime()
        mView.invalidate()
    }

    /***
     * Check if lost
     * ***/
    private fun checkLose() {
        if (!movesAvailable() && !gameWon()) {
            gameState = GAME_LOST
            endGame()
        }
    }

    /***
     * Ending the game
     * ***/
    private fun endGame() {
        aGrid?.startAnimation(
            -1,
            -1,
            FADE_GLOBAL_ANIMATION,
            NOTIFICATION_ANIMATION_TIME,
            NOTIFICATION_DELAY_TIME,
            null
        )
        if (score >= highScoreVal) {
            highScoreVal = score
            recordHighScore()
        }
    }

    /***
     * Getting cell movement
     * ***/
    private fun getVector(direction: Int): Cell {
        val map = arrayOf(
            Cell(0, -1),  // up
            Cell(1, 0),  // right
            Cell(0, 1),  // down
            Cell(-1, 0) // left
        )
        return map[direction]
    }

    /***
     * Building x axis traversal
     * ***/
    private fun buildTraversalsX(vector: Cell): List<Int> {
        val traversals: MutableList<Int> = ArrayList()
        for (xx in 0 until numSquaresX) {
            traversals.add(xx)
        }
        if (vector.x === 1) {
            Collections.reverse(traversals)
        }
        return traversals
    }

    /***
     * Building y axis traversal
     * ***/
    private fun buildTraversalsY(vector: Cell): List<Int> {
        val traversals: MutableList<Int> = ArrayList()
        for (xx in 0 until numSquaresY) {
            traversals.add(xx)
        }
        if (vector.y === 1) {
            Collections.reverse(traversals)
        }
        return traversals
    }

    /***
     * Go get the farthest cell
     * ***/
    private fun findFarthestPosition(cell: Cell, vector: Cell): Array<Cell> {
        var previous: Cell
        var nextCell = Cell(cell.x, cell.y)
        do {
            previous = nextCell
            nextCell = Cell(
                previous.x + vector.x,
                previous.y + vector.y
            )
        } while (grid!!.isCellWithinBounds(nextCell) && grid!!.isCellAvailable(nextCell))
        return arrayOf(previous, nextCell)
    }

    /***
     * On swipe to check if move is available
     * ***/
    private fun movesAvailable(): Boolean {
        return grid!!.isCellsAvailable  || tileMatchesAvailable()
    }

    /***
     * On swipe to check if tile match is available to merge
     * ***/
    private fun tileMatchesAvailable(): Boolean {
        var tile: Tile
        for (xx in 0 until numSquaresX) {
            for (yy in 0 until numSquaresY) {
                tile = grid?.getCellContent(Cell(xx, yy))!!
                if (tile != null) {
                    for (direction in 0..3) {
                        val vector = getVector(direction)
                        val cell = Cell(xx + vector.x, yy + vector.y)
                        val other: Tile = grid!!.getCellContent(cell)!!
                        if (other != null && other.value === tile.value) {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }
    /***
     * To check if the tile is moved
     * ***/
    private fun positionsEqual(first: Cell, second: Cell): Boolean {
        return first.x === second.x && first.y === second.y
    }

    /***
     * Check on merge is the value is winning value-2048
     * ***/
    private fun winValue(): Int {
        return if (!canContinue()) {
            endingMaxValue
        } else {
            startingMaxValue
        }
    }

    /***
     * To continue game until the tile movement is possible even after winning
     * ***/
    fun setEndlessMode() {
        gameState = GAME_ENDLESS
        mView.invalidate()
        mView.refreshLastTime = true
    }

    /***
     * Checking if can continue after win
     * ***/
    fun canContinue(): Boolean {
        return !(gameState == GAME_ENDLESS || gameState == GAME_ENDLESS_WON)
    }

    companion object {
        const val SPAWN_ANIMATION = -1
        const val MOVE_ANIMATION = 0
        const val MERGE_ANIMATION = 1
        const val FADE_GLOBAL_ANIMATION = 0
        private const val MOVE_ANIMATION_TIME = GameView.BASE_ANIMATION_TIME.toLong()
        private const val SPAWN_ANIMATION_TIME = GameView.BASE_ANIMATION_TIME.toLong()
        private const val NOTIFICATION_DELAY_TIME = MOVE_ANIMATION_TIME + SPAWN_ANIMATION_TIME
        private const val NOTIFICATION_ANIMATION_TIME = (GameView.BASE_ANIMATION_TIME * 5).toLong()
        private const val startingMaxValue = 2048

        //Odd state = game is not active
        //Even state = game is active
        //Win state = active state + 1
        private const val GAME_WIN = 1
        private const val GAME_LOST = -1
        private const val GAME_NORMAL = 0
        private const val GAME_ENDLESS = 2
        private const val GAME_ENDLESS_WON = 3
        private const val HIGH_SCORE = "high score"
        private const val FIRST_RUN = "first run"
        private var endingMaxValue: Int = 0
    }

    init {
        endingMaxValue = Math.pow(
            2.0,
            (mView.numCellTypes - 1).toDouble()
        ).toInt()
    }
}
