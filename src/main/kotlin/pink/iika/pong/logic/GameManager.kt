package pink.iika.pong.logic

import pink.iika.pong.logic.client.GameClient
import pink.iika.pong.logic.client.OnlineGamePanel
import pink.iika.pong.logic.client.ServerHandler
import pink.iika.pong.util.gameenum.GameMap
import pink.iika.pong.util.gameenum.GameMode
import pink.iika.pong.logic.gameobject.GameObject
import pink.iika.pong.util.gameenum.GameOption
import pink.iika.pong.logic.gameobject.Obstacle
import pink.iika.pong.logic.gameobject.Paddle
import pink.iika.pong.logic.gameobject.PowerUp
import pink.iika.pong.util.gameenum.PowerUpType
import pink.iika.pong.logic.gameobject.Ball
import pink.iika.pong.logic.server.GameRoom
import pink.iika.pong.util.gameenum.GameEvent
import pink.iika.pong.util.listener.GameListener
import java.awt.Color
import java.awt.event.KeyEvent
import java.util.concurrent.CopyOnWriteArrayList
import javax.swing.DefaultListModel

class GameManager(private val gamePanel: GamePanel, private val menuPanel: MenuPanel, private val onlineGamePanel: OnlineGamePanel, private val scoreKeeper: ScoreKeeper, private val gameObjectList: CopyOnWriteArrayList<GameObject>, handler: ServerHandler):
    GameListener {
    private var playerNum = 0
    private val powerUpList = CopyOnWriteArrayList<PowerUp>()
    private val colors = arrayOf(Color(0xE4A8CA), Color(0xCCAA87), Color(0xBB6588), Color(0x8889CC))
    private val keybinds = arrayOf(KeyEvent.VK_COMMA, KeyEvent.VK_SLASH, KeyEvent.VK_F, KeyEvent.VK_H)
    private val gameLoop = GameLoop(gamePanel, powerUpList)
    private val client = GameClient(handler, onlineGamePanel, this)

    init {
        gamePanel.setPowerUpList(powerUpList)
    }

    override fun onGameEvent(e: GameEvent) {
        when (e) {
            GameEvent.REPLAY_GAME -> startGame()
            GameEvent.EXIT_TO_MENU -> doMenu()
            GameEvent.CREATE_POWER_UP -> gameLoop.createPowerUp()
            GameEvent.ADD_SCORE_ONE -> scoreKeeper.score1 += 1
            GameEvent.ADD_SCORE_ONE_HALF -> scoreKeeper.score1 += 0.5
            GameEvent.ADD_SCORE_TWO -> scoreKeeper.score2 += 1
            GameEvent.ADD_SCORE_TWO_HALF -> scoreKeeper.score2 += 0.5
            GameEvent.CONTINUE_GAME_ONE -> startGame(1)
            GameEvent.CONTINUE_GAME_TWO -> startGame(2)
            GameEvent.PAUSE_GAME -> pauseGame()
            GameEvent.RESUME_GAME -> resumeGame()
            GameEvent.ENABLE_START -> {enableOnlineStart(); println("enablingstart yayayaya")}
            GameEvent.START_ONLINE_GAME -> startOnlineGame()
            GameEvent.START_CLIENT -> startClient()
            GameEvent.GET_ROOMS -> client.getRooms()
        }
    }

    override fun onGameStart(options: DefaultListModel<GameOption>) {
        startGame(gameOptionList = options)
    }

    override fun onGameEnd() {
        gameLoop.stop()
        gamePanel.setPaused(true)
    }

    override fun onSetKeybind(key: Int, index: Int) {
        if (index > 3) keybinds[index - 4] = key
        else {
            val paddle = gameObjectList[if (index < 2) 1 else 0] as Paddle
            when(index) {
                0, 2 -> paddle.leftKey = key
                1, 3 -> paddle.rightKey = key
            }

            when (index) {
                0 -> onlineGamePanel.setLeftControl(key)
                1 -> onlineGamePanel.setRightControl(key)
            }
        }
    }

    override fun onSetColor(color: Color, index: Int) {
        when (index) {
            0, 1, 2, 3 -> colors[index] = color
            4 -> gamePanel.background = color
        }

        gamePanel.setColors(colors)
        onlineGamePanel.setColors(colors)
    }

    override fun onTogglePowerUp(type: PowerUpType, exclude: Boolean) {
        if (exclude) gameLoop.getExcludeList().add(type)
        else gameLoop.getExcludeList().remove(type)
    }

    override fun onJoinRoom(room: GameRoom) {
        client.joinRoom(room)
    }

    override fun onUpdateRooms(roomList: MutableList<GameRoom>) {
        menuPanel.setRooms(roomList)
    }

    override fun onCreateRoom(room: GameRoom) {
        client.createRoom(room)
    }

    private fun startGame(paddleNum: Int = 0, gameOptionList: DefaultListModel<GameOption> = DefaultListModel<GameOption>()) {
        menuPanel.isVisible = false
        gamePanel.setPaused(false)

        for (i in 0..<gameOptionList.size) {
            val gameOption = gameOptionList[i]

            when (gameOption) {
                GameOption.NORMAL_MODE -> setGameMode(GameMode.NORMAL_MODE)
                GameOption.CHAOS_MODE -> setGameMode(GameMode.CHAOS_MODE)
                GameOption.BORING_MODE -> setGameMode(GameMode.BORING_MODE)
                GameOption.SINGLEPLAYER -> setPlayers(1)
                GameOption.MULTIPLAYER -> setPlayers(2)
                GameOption.NO_OBSTACLES -> setMap(GameMap.NO_OBSTACLES)
                GameOption.ONE_OBSTACLE -> setMap(GameMap.RANDOM_OBSTACLES, 1)
                GameOption.TWO_OBSTACLES -> setMap(GameMap.RANDOM_OBSTACLES, 2)
                GameOption.THREE_OBSTACLES -> setMap(GameMap.RANDOM_OBSTACLES, 3)
                GameOption.FOUR_OBSTACLES -> setMap(GameMap.RANDOM_OBSTACLES, 4)
                GameOption.ONE_BIG_BLOCK -> setMap(GameMap.ONE_BIG_BLOCK)
                GameOption.DOUBLE_BALL -> gamePanel.setDoubleBall(true)
                GameOption.DOUBLE_PADDLE -> {
                    gameObjectList.add(Paddle(side = 1, leftKey = keybinds[0], rightKey = keybinds[1]))
                    if (playerNum == 2) gameObjectList.add(
                        Paddle(
                            side = 2,
                            leftKey = keybinds[2],
                            rightKey = keybinds[3]
                        )
                    )
                }
                GameOption.SPLIT_GAME -> {
                    gamePanel.setSplitGame(true)
                    gameObjectList.add(Paddle(side = 1, leftKey = keybinds[0], rightKey = keybinds[1]))
                    if (playerNum == 2) gameObjectList.add(
                        Paddle(
                            side = 2,
                            leftKey = keybinds[2],
                            rightKey = keybinds[3]
                        )
                    )
                    gameObjectList.add(Obstacle(gamePanel.width / 2 - 5.0, 0.0, 10.0, gamePanel.height.toDouble()))
                }
                null -> throw NoWhenBranchMatchedException("Invalid Game Option")
            }
        }

        when (paddleNum) {
            1 -> scoreKeeper.score1 += 1
            2 -> scoreKeeper.score2 += 1
        }

        gameObjectList.add(Ball())
        powerUpList.clear()
        gamePanel.initializeBall()
        gamePanel.initializePaddles(paddleNum)
        gamePanel.initializeComponents()
        gameLoop.start()
        gamePanel.isVisible = true
        gamePanel.requestFocusInWindow()
    }

    private fun startOnlineGame() {
        client.startGame()
        onlineGamePanel.isVisible = true
        menuPanel.setOnlineGameButton(false)
        onlineGamePanel.requestFocusInWindow()
    }

    private fun startClient() {
        client.startClient()
    }

    private fun pauseGame() {
        gameLoop.pause()
    }

    private fun resumeGame() {
        gameLoop.resume()
        gamePanel.setPaused(false)
    }

    private fun doMenu() {
        for (i in gameObjectList.lastIndex downTo 2) gameObjectList.removeAt(i)
        gamePanel.setSplitGame(false)
        gamePanel.setDoubleBall(false)
        gamePanel.isVisible = false
        gameLoop.reset()
        menuPanel.doMenu()
    }

    private fun setGameMode(gameMode: GameMode) {
        when (gameMode) {
            GameMode.NORMAL_MODE -> gameLoop.setPowerUpCount(2400)
            GameMode.CHAOS_MODE -> gameLoop.setPowerUpCount(120)
            GameMode.BORING_MODE -> gameLoop.setPowerUpCount(Int.MAX_VALUE)
        }
    }

    private fun setMap(map: GameMap, obstacles: Int = 0) {
        when (map) {
            GameMap.NO_OBSTACLES -> {}

            GameMap.RANDOM_OBSTACLES -> {
                for (i in 1..obstacles) gameObjectList.add(
                    Obstacle(
                        (20..gamePanel.width - 200).random().toDouble(),
                        (20..gamePanel.height - 200).random().toDouble()
                    )
                )
            }

            GameMap.ONE_BIG_BLOCK -> {
                gameObjectList.add(Obstacle(200.0, 200.0, gamePanel.width - 400.0, gamePanel.height - 400.0))
            }
        }
    }

    private fun setPlayers(playerNum: Int) {
        this.playerNum = playerNum
        gamePanel.setPlayers(playerNum)
        gameLoop.setPlayerNum(playerNum)
    }

    private fun enableOnlineStart() = menuPanel.setOnlineGameButton(true)
}