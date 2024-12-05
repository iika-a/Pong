import java.awt.BorderLayout
import java.awt.CardLayout
import java.awt.GridBagLayout
import java.awt.GridBagConstraints
import java.awt.FlowLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.ItemEvent
import java.awt.event.ItemListener
import javax.swing.DefaultListModel
import javax.swing.JScrollPane
import javax.swing.JPanel
import javax.swing.JLabel
import javax.swing.JButton
import javax.swing.JList
import javax.swing.JColorChooser
import javax.swing.JCheckBox
import javax.swing.border.LineBorder
import kotlin.system.exitProcess

class MenuPanel(private val scoreKeeper: ScoreKeeper, private val buttonMouseListener: ButtonMouseListener): JPanel(),
    ActionListener, KeyListener, ItemListener {
    private val gameSetupPanel = JPanel(BorderLayout())
    private val settingsPanel = JPanel(BorderLayout())
    private val cards = CardLayout()
    private val gameButtonsPanel = JPanel(cards)
    private val settingsButtonsPanel = JPanel(cards)
    private val menuFont = Font("Segoe UI", 0, 18)
    private var currentKeybindButton: JButton = JButton()
    private var isChangingKeybind = false
    private var formerButtonText = ""

    private val backButton = JButton("Back").apply { setButtonSettings(this); font = font.deriveFont(14f); preferredSize = Dimension(50, 30) }
    private val resetButton = JButton("Reset Scores").apply { setButtonSettings(this); font = font.deriveFont(14f); preferredSize = Dimension(105, 30) }
    private val scoreLabel = JLabel("Player 1 Score: ${scoreKeeper.score1}              Player 2 Score: ${scoreKeeper.score2}").apply { font = menuFont.deriveFont(14f) }

    private val settingsBackButton = JButton("Back").apply { setButtonSettings(this); font = font.deriveFont(14f); preferredSize = Dimension(50, 30) }
    private val settingsKeybindsButton = JButton("Controls").apply { setButtonSettings(this); font = font.deriveFont(14f); preferredSize = Dimension(75, 30) }
    private val settingsColorsButton = JButton("Colors").apply { setButtonSettings(this); font = font.deriveFont(14f); preferredSize = Dimension(60, 30) }

    private val player1ColorButton = JButton("Player 1").apply { setButtonSettings(this) }
    private val player2ColorButton = JButton("Player 2").apply { setButtonSettings(this) }
    private val obstacleColorButton = JButton("Obstacles").apply { setButtonSettings(this) }
    private val ballColorButton = JButton("Ball").apply { setButtonSettings(this) }
    private val backgroundColorButton = JButton("Background").apply { setButtonSettings(this) }
    private val resetColorButton = JButton("Reset to Defaults").apply { setButtonSettings(this) }
    private val colorButtonArray = arrayOf(player1ColorButton, player2ColorButton, obstacleColorButton, ballColorButton, backgroundColorButton)

    private val player1ColorLabel = JLabel("Selected Color").apply { setLabelSettings(this); background = Color(0xE4A8CA) }
    private val player2ColorLabel = JLabel("Selected Color").apply { setLabelSettings(this); background = Color(0xCCAA87) }
    private val obstacleLabel = JLabel("Selected Color").apply { setLabelSettings(this); background = Color(0xBB6588) }
    private val ballLabel = JLabel("Selected Color").apply { setLabelSettings(this); background = Color(0x8889CC) }
    private val backgroundLabel = JLabel("Selected Color").apply { setLabelSettings(this); background = Color.WHITE }
    private val colorLabelArray = arrayOf(player1ColorLabel, player2ColorLabel, obstacleLabel, ballLabel, backgroundLabel)

    private val player1Paddle1LeftButton = JButton("Left: ${KeyEvent.getKeyText(KeyEvent.VK_LEFT)}").apply { setButtonSettings(this) }
    private val player1Paddle1RightButton = JButton("Right: ${KeyEvent.getKeyText(KeyEvent.VK_RIGHT)}").apply { setButtonSettings(this) }
    private val player1Paddle2LeftButton = JButton("Left: ${KeyEvent.getKeyText(KeyEvent.VK_COMMA)}").apply { setButtonSettings(this) }
    private val player1Paddle2RightButton = JButton("Right: ${KeyEvent.getKeyText(KeyEvent.VK_SLASH)}").apply { setButtonSettings(this) }
    private val player2Paddle1LeftButton = JButton("Left: ${KeyEvent.getKeyText(KeyEvent.VK_A)}").apply { setButtonSettings(this) }
    private val player2Paddle1RightButton = JButton("Right: ${KeyEvent.getKeyText(KeyEvent.VK_D)}").apply { setButtonSettings(this) }
    private val player2Paddle2LeftButton = JButton("Left: ${KeyEvent.getKeyText(KeyEvent.VK_F)}").apply { setButtonSettings(this) }
    private val player2Paddle2RightButton = JButton("Right: ${KeyEvent.getKeyText(KeyEvent.VK_H)}").apply { setButtonSettings(this) }
    private val resetKeybindsButton = JButton("Reset to Defaults").apply { setButtonSettings(this) }
    private val keybindButtonArray = arrayOf(player1Paddle1LeftButton, player1Paddle1RightButton, player1Paddle2LeftButton, player1Paddle2RightButton, player2Paddle1LeftButton, player2Paddle1RightButton, player2Paddle2LeftButton, player2Paddle2RightButton)

    private val player1KeybindLabel = JLabel("Player 1").apply { font = menuFont.deriveFont(24f) }
    private val player1Paddle1Label = JLabel("Paddle 1").apply { font = menuFont.deriveFont(19f); horizontalAlignment = JLabel.LEFT; preferredSize = Dimension(223, 25) }
    private val player1Paddle2Label = JLabel("Paddle 2").apply { font = menuFont.deriveFont(19f); horizontalAlignment = JLabel.RIGHT; preferredSize = Dimension(223, 25) }
    private val player2KeybindLabel = JLabel("Player 2").apply { font = menuFont.deriveFont(24f) }
    private val player2Paddle1Label = JLabel("Paddle 1").apply { font = menuFont.deriveFont(19f); horizontalAlignment = JLabel.LEFT; preferredSize = Dimension(223, 25) }
    private val player2Paddle2Label = JLabel("Paddle 2").apply { font = menuFont.deriveFont(19f); horizontalAlignment = JLabel.RIGHT; preferredSize = Dimension(223, 25) }

    private val powerUpLabel = JLabel("Selected Power Ups:").apply { font = menuFont; horizontalAlignment = JLabel.CENTER }
    private val increasePaddleSizeBox = JCheckBox("Increase Paddle Size", true).apply { setBoxSettings(this) }
    private val increasePaddleSpeedBox = JCheckBox("Increase Paddle Speed", true).apply { setBoxSettings(this) }
    private val randomizeBallSpeedBox = JCheckBox("Randomize Ball Speed", true).apply { setBoxSettings(this) }
    private val randomizeBallAngleBox = JCheckBox("Randomize Ball Angle", true).apply { setBoxSettings(this) }
    private val spawnBallBox = JCheckBox("Spawn Ball", true).apply { setBoxSettings(this) }
    private val powerUpBoxList = arrayOf(increasePaddleSizeBox, increasePaddleSpeedBox, randomizeBallSpeedBox, randomizeBallAngleBox, spawnBallBox)

    private val playButton = JButton("Play Pong").apply { setButtonSettings(this) }
    private val settingsButton = JButton("Settings").apply { setButtonSettings(this) }
    private val quitButton = JButton("Quit Pong").apply { setButtonSettings(this) }

    private val normalModeButton = JButton("Normal Mode").apply { setButtonSettings(this) }
    private val chaosModeButton = JButton("Chaos Mode").apply { setButtonSettings(this) }
    private val boringModeButton = JButton("Boring Mode").apply { setButtonSettings(this) }
    private val modeButtonList = arrayOf(normalModeButton, chaosModeButton, boringModeButton)

    private val noBlocksButton = JButton("No Obstacles").apply { setButtonSettings(this) }
    private val randomBlocksButton = JButton("Random Obstacles").apply { setButtonSettings(this) }
    private val bigBlockButton = JButton("One Big Block").apply { setButtonSettings(this) }
    private val mapButtonList = arrayOf(noBlocksButton, randomBlocksButton, bigBlockButton)

    private val oneBlockButton = JButton("One Obstacle").apply { setButtonSettings(this) }
    private val twoBlockButton = JButton("Two Obstacles").apply { setButtonSettings(this) }
    private val threeBlockButton = JButton("Three Obstacles").apply { setButtonSettings(this) }
    private val fourBlockButton = JButton("Four Obstacles").apply { setButtonSettings(this) }
    private val blocksButtonList = arrayOf(oneBlockButton, twoBlockButton, threeBlockButton, fourBlockButton)

    private val doubleBallButton = JButton("Double Balls").apply { setButtonSettings(this ) }
    private val doublePaddleButton = JButton("Double Paddles").apply { setButtonSettings(this ) }
    private val splitGameButton = JButton("Split Game").apply { setButtonSettings(this ) }
    private val specialButtonList = arrayOf(doubleBallButton, doublePaddleButton, splitGameButton)

    private val singleplayerButton = JButton("Singleplayer").apply { setButtonSettings(this) }
    private val multiplayerButton = JButton("Multiplayer").apply { setButtonSettings(this) }

    private val optionsButton = JButton("Game Options").apply { setButtonSettings(this) }
    private val startGameButton = JButton("Start Game!").apply { setButtonSettings(this) }

    private var currentMenu = "Main Menu"
    private var gameOptionList = DefaultListModel<GameOption>()
    private var gameManager: GameEventListener? = null

    init {
        this.layout = cards
        this.addKeyListener(this)

        gameButtonsPanel.add(createMainMenu().apply { background = Color(0xFFD1DC) }, "Main Menu")
        gameButtonsPanel.add(createPlayersMenu().apply { background = Color(0xFFD1DC) }, "Players")
        gameButtonsPanel.add(createConfirmMenu().apply { background = Color(0xFFD1DC) }, "Confirm")
        gameButtonsPanel.add(createOptionsMenu().apply { background = Color(0xFFD1DC) }, "Options")
        settingsButtonsPanel.add(createKeybindsPanel().apply { background = Color(0xFFD1DC) }, "Keybinds")
        settingsButtonsPanel.add(createColorsPanel().apply { background = Color(0xFFD1DC) }, "Colors")

        gameSetupPanel.add(createTopPanel().apply { background = Color(0xFFD1DC) }, BorderLayout.NORTH)
        gameSetupPanel.add(createBottomPanel().apply { background = Color(0xFFD1DC) }, BorderLayout.SOUTH)
        gameSetupPanel.add(gameButtonsPanel, BorderLayout.CENTER)

        settingsPanel.add(createSettingsTopPanel().apply { background = Color(0xFFD1DC) }, BorderLayout.NORTH)
        settingsPanel.add(createSettingsBottomPanel().apply { background = Color(0xFFD1DC) }, BorderLayout.SOUTH)
        settingsPanel.add(settingsButtonsPanel, BorderLayout.CENTER)

        this.add(gameSetupPanel, "Game Setup")
        this.add(settingsPanel, "Settings")
    }

    private fun createTopPanel(): JPanel {
        val panel = JPanel()

        scoreLabel.horizontalAlignment = JLabel.LEFT
        panel.add(scoreLabel)

        return panel
    }

    private fun createBottomPanel(): JPanel {
        val panel = JPanel(FlowLayout(FlowLayout.CENTER, 10, 10))

        panel.add(backButton)
        panel.add(resetButton)
        panel.preferredSize = Dimension(720, 45)

        return panel
    }

    private fun createSettingsBottomPanel(): JPanel {
        val panel = JPanel(FlowLayout(FlowLayout.CENTER, 10, 10))
        panel.add(settingsBackButton)
        panel.preferredSize = Dimension(720, 45)

        return panel
    }

    private fun createSettingsTopPanel(): JPanel {
        val panel = JPanel(FlowLayout(FlowLayout.CENTER, 10, 10))

        panel.add(settingsKeybindsButton)
        panel.add(settingsColorsButton)
        panel.preferredSize = Dimension(720, 85)

        return panel
    }

    private fun makeCommonComponentsVisible() {
        resetButton.isVisible = true
        backButton.isVisible = true
        scoreLabel.isVisible = true
    }

    private fun makeCommonComponentsInvisible() {
        resetButton.isVisible = false
        backButton.isVisible = false
        scoreLabel.isVisible = false
    }

    private fun createMainMenu(): JPanel {
        val panel = JPanel(GridBagLayout())
        val mainLabel = JLabel("iika's Pong").apply { font = menuFont }
        val versionLabel = JLabel("version 1.1").apply { font = menuFont.deriveFont(12f) }
        versionLabel.horizontalAlignment = JLabel.CENTER
        mainLabel.horizontalAlignment = JLabel.CENTER

        val constraints = GridBagConstraints().apply {
            gridx = 0
            gridy = GridBagConstraints.RELATIVE
            insets.set(10, 10, 10, 10)
            fill = GridBagConstraints.HORIZONTAL
        }

        panel.add(mainLabel, constraints)
        panel.add(versionLabel, constraints)
        panel.add(playButton, constraints)
        panel.add(settingsButton, constraints)
        panel.add(quitButton, constraints)

        return panel
    }

    private fun createPlayersMenu(): JPanel {
        val panel = JPanel(GridBagLayout())
        val playersLabel = JLabel("How many players?").apply { font = menuFont }
        playersLabel.horizontalAlignment = JLabel.CENTER

        val constraints = GridBagConstraints().apply {
            gridx = 0
            gridy = GridBagConstraints.RELATIVE
            insets.set(10, 10, 10, 10)
            fill = GridBagConstraints.HORIZONTAL
        }

        panel.add(playersLabel, constraints)
        panel.add(singleplayerButton, constraints)
        panel.add(multiplayerButton, constraints)

        return panel
    }

    private fun createConfirmMenu(): JPanel {
        val panel = JPanel(GridBagLayout())
        val confirmLabel = JLabel("Selected Options:").apply { font = menuFont }
        confirmLabel.horizontalAlignment = JLabel.CENTER

        val scrollPane = JScrollPane(JList(gameOptionList).apply { font = menuFont.deriveFont(12f) })

        val constraints = GridBagConstraints().apply {
            gridx = 0
            gridy = GridBagConstraints.RELATIVE
            insets.set(10, 10, 10, 10)
            fill = GridBagConstraints.HORIZONTAL
        }

        panel.add(confirmLabel, constraints)
        panel.add(scrollPane, constraints)
        panel.add(optionsButton, constraints)
        panel.add(startGameButton, constraints)

        return panel
    }

    private fun createOptionsMenu(): JPanel {
        val panel = JPanel(BorderLayout()).apply { background = Color(0xFFD1DC) }
        val buttonPanel = JPanel(GridBagLayout()).apply { background = Color(0xFFD1DC) }
        val buttonPanel1 = JPanel(FlowLayout()).apply { background = Color(0xFFD1DC) }
        val buttonPanel2 = JPanel(FlowLayout()).apply { background = Color(0xFFD1DC) }
        val buttonPanel3 = JPanel(FlowLayout()).apply { background = Color(0xFFD1DC) }
        val buttonPanel4 = JPanel(FlowLayout()).apply { background = Color(0xFFD1DC) }
        val sidePanel = JPanel(BorderLayout()).apply { background = Color(0xFFD1DC); preferredSize = Dimension(400, 450) }
        val checkBoxPanel  = JPanel(GridBagLayout()).apply { background = Color(0xFFD1DC) }
        val scrollPane = JScrollPane(JList(gameOptionList).apply { font = menuFont.deriveFont(12f) }).apply { preferredSize = Dimension(200, 400) }
        val panePanel = JPanel(BorderLayout()).apply { background = Color(0xFFD1DC) }

        val modeLabel = JLabel("Game Modes", JLabel.CENTER).apply { font = menuFont }
        val mapLabel = JLabel("Game Maps", JLabel.CENTER).apply { font = menuFont }
        val blocksLabel = JLabel("Number of Obstacles (Random Obstacles Only)", JLabel.CENTER).apply { font = menuFont }
        val specialLabel = JLabel("Special Modifiers", JLabel.CENTER).apply { font = menuFont }
        val optionsLabel = JLabel("Selected Options:", JLabel.CENTER).apply { font = menuFont; preferredSize = Dimension(10, 30) }

        val constraints = GridBagConstraints().apply {
            gridx = 0
            gridy = 0
            weightx = 1.0
            weighty = 0.0
            fill = GridBagConstraints.HORIZONTAL
            anchor = GridBagConstraints.CENTER
            insets.set(10, 10, 10, 10)
        }

        buttonPanel.add(modeLabel, constraints)
        checkBoxPanel.add(powerUpLabel, constraints)

        constraints.gridy++
        for (button in modeButtonList) buttonPanel1.add(button)
        buttonPanel.add(buttonPanel1, constraints)
        checkBoxPanel.add(increasePaddleSizeBox, constraints)

        constraints.gridy++
        buttonPanel.add(mapLabel, constraints)
        checkBoxPanel.add(increasePaddleSpeedBox, constraints)

        constraints.gridy++
        for (button in mapButtonList) buttonPanel2.add(button)
        buttonPanel.add(buttonPanel2, constraints)
        checkBoxPanel.add(randomizeBallSpeedBox, constraints)

        constraints.gridy++
        buttonPanel.add(blocksLabel, constraints)
        checkBoxPanel.add(randomizeBallAngleBox, constraints)

        constraints.gridy++
        for (button in blocksButtonList) buttonPanel3.add(button)
        buttonPanel.add(buttonPanel3, constraints)
        checkBoxPanel.add(spawnBallBox, constraints)

        constraints.gridy++
        buttonPanel.add(specialLabel, constraints)

        constraints.gridy++
        for (button in specialButtonList) buttonPanel4.add(button)
        buttonPanel.add(buttonPanel4, constraints)

        panePanel.add(optionsLabel, BorderLayout.NORTH)
        panePanel.add(scrollPane, BorderLayout.CENTER)
        sidePanel.add(panePanel, BorderLayout.EAST)
        sidePanel.add(checkBoxPanel, BorderLayout.WEST)

        panel.add(sidePanel, BorderLayout.EAST)
        panel.add(buttonPanel, BorderLayout.CENTER)

        return panel
    }

    private fun createKeybindsPanel(): JPanel {
        val panel = JPanel(GridBagLayout())
        val labelPanel1 = JPanel(FlowLayout(FlowLayout.CENTER)).apply { background = Color(0xFFD1DC) }
        val labelPanel2 = JPanel(FlowLayout(FlowLayout.CENTER)).apply { background = Color(0xFFD1DC) }
        val buttonPanel1 = JPanel(FlowLayout(FlowLayout.CENTER)).apply { background = Color(0xFFD1DC) }
        val labelPanel3 = JPanel(FlowLayout(FlowLayout.CENTER)).apply { background = Color(0xFFD1DC) }
        val labelPanel4 = JPanel(FlowLayout(FlowLayout.CENTER)).apply { background = Color(0xFFD1DC) }
        val buttonPanel2 = JPanel(FlowLayout(FlowLayout.CENTER)).apply { background = Color(0xFFD1DC) }
        val buttonPanel3 = JPanel(FlowLayout(FlowLayout.CENTER)).apply { background = Color(0xFFD1DC) }
        val panelArray = arrayOf(labelPanel1, labelPanel2, buttonPanel1, labelPanel3, labelPanel4, buttonPanel2, buttonPanel3)

        val constraints = GridBagConstraints().apply {
            gridx = 0
            gridy = 0
            weightx = 1.0
            weighty = 0.0
            fill = GridBagConstraints.HORIZONTAL
            anchor = GridBagConstraints.CENTER
            insets.set(10, 10, 10, 10)
        }

        labelPanel1.add(player1KeybindLabel)
        labelPanel2.add(player1Paddle1Label)
        labelPanel2.add(player1Paddle2Label)
        labelPanel3.add(player2KeybindLabel)
        labelPanel4.add(player2Paddle1Label)
        labelPanel4.add(player2Paddle2Label)
        for (i in 0..3) buttonPanel1.add(keybindButtonArray[i])
        for (i in 4..7) buttonPanel2.add(keybindButtonArray[i])
        buttonPanel3.add(resetKeybindsButton)

        for (i in 0..6) {
            constraints.gridy = i
            panel.add(panelArray[i], constraints)
        }

        return panel
    }

    private fun createColorsPanel(): JPanel {
        val panel = JPanel(GridBagLayout()).apply { background = Color(0xFFD1DC) }
        val buttonPanel = JPanel(FlowLayout(FlowLayout.CENTER)).apply { background = Color(0xFFD1DC) }
        val labelPanel = JPanel(FlowLayout(FlowLayout.CENTER)).apply { background = Color(0xFFD1DC) }

        val constraints = GridBagConstraints().apply {
            gridx = 0
            gridy = 0
            weightx = 1.0
            weighty = 0.0
            fill = GridBagConstraints.HORIZONTAL
            anchor = GridBagConstraints.CENTER
            insets.set(10, 10, 10, 10)
        }

        for (button in colorButtonArray) buttonPanel.add(button)
        for (label in colorLabelArray) labelPanel.add(label)

        panel.add(buttonPanel, constraints)
        constraints.gridy++
        panel.add(labelPanel, constraints)
        constraints.gridy++
        panel.add(JPanel(FlowLayout(FlowLayout.CENTER)).apply { background = Color(0xFFD1DC); add(resetColorButton) }, constraints)

        return panel
    }

    private fun setButtonSettings(button: JButton) {
        val buttonSize = Dimension(185, 60)
        val menuFont = Font("Segoe UI", 0, 18)
        val buttonColor = Color(0xD1F6FF)
        val buttonBorder = LineBorder(Color.WHITE, 3)

        button.preferredSize = buttonSize
        button.font = menuFont
        button.background = buttonColor
        button.border = buttonBorder
        button.addActionListener(this)
        button.addMouseListener(buttonMouseListener)
    }

    private fun setLabelSettings(label: JLabel) {
        val labelSize = Dimension(185, 60)
        val menuFont = Font("Segoe UI", 0, 18)
        val labelBorder = LineBorder(Color.BLACK, 3)

        label.preferredSize = labelSize
        label.font = menuFont
        label.border = labelBorder
        label.horizontalAlignment = JLabel.CENTER
        label.isOpaque = true
    }

    private fun setBoxSettings(box: JCheckBox) {
        box.font = menuFont.deriveFont(14f)
        box.background = Color(0xFFD1DC)
        box.addItemListener(this)
    }

    private fun doDefaultButtonStates() {
        enableButtons(normalModeButton, noBlocksButton, doubleBallButton, doublePaddleButton, splitGameButton, settingsColorsButton)
        disableButtons(chaosModeButton, boringModeButton, randomBlocksButton, bigBlockButton, oneBlockButton, twoBlockButton, threeBlockButton, fourBlockButton, settingsKeybindsButton)
    }

    private fun doDefaultBoxStates() {
        for (box in powerUpBoxList) if (!box.isSelected) box.isSelected = true
    }

    private fun showMenu(menuName: String, panel: JPanel) {
        currentMenu = menuName
        if (menuName == "Main Menu") makeCommonComponentsInvisible()
        cards.show(panel, menuName)
    }

    fun doMenu() {
        this.isVisible = true
        resetGameOptionList()
        refreshScore()
        doDefaultButtonStates()
        doDefaultBoxStates()
        showMenu("Game Setup", gameButtonsPanel)
        showMenu("Main Menu", gameButtonsPanel)
    }

    private fun refreshScore() {
        scoreLabel.text = "Player 1 Score: ${scoreKeeper.score1}                        Player 2 Score: ${scoreKeeper.score2}"
    }

    private fun resetGameOptionList() {
        gameOptionList.clear()
        gameOptionList.addElement(GameOption.NORMAL_MODE)
        gameOptionList.addElement(GameOption.NO_OBSTACLES)
    }

    override fun actionPerformed(e: ActionEvent?) {
        when (e?.source) {
            playButton -> {
                showMenu("Players", gameButtonsPanel)
                makeCommonComponentsVisible()
            }
            settingsButton -> {
                showMenu("Settings", this)
                showMenu("Keybinds", settingsButtonsPanel)
                disableButtons(settingsKeybindsButton)
                enableButtons(settingsColorsButton)
            }
            quitButton -> {
                exitProcess(0)
            }

            normalModeButton -> {
                toggleOption(GameOption.NORMAL_MODE)
                toggleButtons(chaosModeButton, boringModeButton)
            }
            chaosModeButton -> {
                toggleOption(GameOption.CHAOS_MODE)
                toggleButtons(normalModeButton, boringModeButton)
            }
            boringModeButton -> {
                toggleOption(GameOption.BORING_MODE)
                toggleButtons(normalModeButton, chaosModeButton)
            }

            noBlocksButton -> {
                toggleOption(GameOption.NO_OBSTACLES)
                toggleButtons(randomBlocksButton, bigBlockButton)
            }
            randomBlocksButton -> {
                toggleButtons(noBlocksButton, bigBlockButton)
                if (!gameOptionList.contains(GameOption.ONE_OBSTACLE) &&
                    !gameOptionList.contains(GameOption.TWO_OBSTACLES) &&
                    !gameOptionList.contains(GameOption.THREE_OBSTACLES) &&
                    !gameOptionList.contains(GameOption.FOUR_OBSTACLES)
                ) toggleButtons(oneBlockButton, twoBlockButton, threeBlockButton, fourBlockButton)

                if (gameOptionList.contains(GameOption.ONE_OBSTACLE)) {
                    gameOptionList.removeElement(GameOption.ONE_OBSTACLE)
                    toggleButtons(oneBlockButton)
                }
                else if (gameOptionList.contains(GameOption.TWO_OBSTACLES)) {
                    gameOptionList.removeElement(GameOption.TWO_OBSTACLES)
                    toggleButtons(twoBlockButton)
                }
                else if (gameOptionList.contains(GameOption.THREE_OBSTACLES)) {
                    gameOptionList.removeElement(GameOption.THREE_OBSTACLES)
                    toggleButtons(threeBlockButton)
                }
                else if (gameOptionList.contains(GameOption.FOUR_OBSTACLES)) {
                    gameOptionList.removeElement(GameOption.FOUR_OBSTACLES)
                    toggleButtons(fourBlockButton)
                }
            }
            bigBlockButton -> {
                toggleOption(GameOption.ONE_BIG_BLOCK)
                toggleButtons(noBlocksButton, randomBlocksButton)
            }

            oneBlockButton -> {
                toggleOption(GameOption.ONE_OBSTACLE)
                toggleButtons(twoBlockButton, threeBlockButton, fourBlockButton)
            }
            twoBlockButton -> {
                toggleOption(GameOption.TWO_OBSTACLES)
                toggleButtons(oneBlockButton, threeBlockButton, fourBlockButton)
            }
            threeBlockButton -> {
                toggleOption(GameOption.THREE_OBSTACLES)
                toggleButtons(oneBlockButton, twoBlockButton, fourBlockButton)
            }
            fourBlockButton -> {
                toggleOption(GameOption.FOUR_OBSTACLES)
                toggleButtons(oneBlockButton, twoBlockButton, threeBlockButton)
            }

            doubleBallButton -> {
                toggleOption(GameOption.DOUBLE_BALL)
                if (!gameOptionList.contains(GameOption.DOUBLE_PADDLE)) toggleButtons(splitGameButton)
            }
            doublePaddleButton -> {
                toggleOption(GameOption.DOUBLE_PADDLE)
                if (!gameOptionList.contains(GameOption.DOUBLE_BALL)) toggleButtons(splitGameButton)
            }
            splitGameButton -> {
                toggleOption(GameOption.SPLIT_GAME)
                toggleButtons(doubleBallButton, doublePaddleButton)
            }

            singleplayerButton -> {
                gameOptionList.addElement(GameOption.SINGLEPLAYER)
                showMenu("Confirm", gameButtonsPanel)
            }
            multiplayerButton -> {
                gameOptionList.addElement(GameOption.MULTIPLAYER)
                showMenu("Confirm", gameButtonsPanel)
            }

            startGameButton -> {
                gameManager?.onGameStart(gameOptionList)
            }
            optionsButton -> showMenu("Options", gameButtonsPanel)

            resetButton -> {
                scoreKeeper.resetScore()
                refreshScore()
            }
            backButton -> {
                when (currentMenu) {
                    "Players" -> showMenu("Main Menu", gameButtonsPanel)
                    "Confirm" -> {
                        if (gameOptionList.contains(GameOption.SINGLEPLAYER)) gameOptionList.removeElement(GameOption.SINGLEPLAYER)
                        if (gameOptionList.contains(GameOption.MULTIPLAYER)) gameOptionList.removeElement(GameOption.MULTIPLAYER)
                        showMenu("Players", gameButtonsPanel)
                    }
                    "Options" -> showMenu("Confirm", gameButtonsPanel)
                }
            }

            settingsBackButton -> {
                if (isChangingKeybind) {
                    isChangingKeybind = false
                    currentKeybindButton.text = formerButtonText
                }
                showMenu("Game Setup", this)
                showMenu("Main Menu", gameButtonsPanel)
            }
            settingsKeybindsButton -> {
                toggleButtons(settingsKeybindsButton, settingsColorsButton)
                showMenu("Keybinds", settingsButtonsPanel)
            }
            settingsColorsButton -> {
                if (isChangingKeybind) {
                    isChangingKeybind = false
                    currentKeybindButton.text = formerButtonText
                }
                toggleButtons(settingsColorsButton, settingsKeybindsButton)
                showMenu("Colors", settingsButtonsPanel)
            }

            player1ColorButton -> {
                var color = JColorChooser.showDialog(this, "Choose Color", player1ColorLabel.background)
                if (color == null) color = player1ColorLabel.background
                player1ColorLabel.background = color
                gameManager?.onSetColor(color, 0)
            }
            player2ColorButton -> {
                var color = JColorChooser.showDialog(this, "Choose Color", player2ColorLabel.background)
                if (color == null) color = player2ColorLabel.background
                player2ColorLabel.background = color
                gameManager?.onSetColor(color, 1)
            }
            obstacleColorButton -> {
                var color = JColorChooser.showDialog(this, "Choose Color", obstacleLabel.background)
                if (color == null) color = obstacleLabel.background
                obstacleLabel.background = color
                gameManager?.onSetColor(color, 2)
            }
            ballColorButton -> {
                var color = JColorChooser.showDialog(this, "Choose Color", ballLabel.background)
                if (color == null) color = ballLabel.background
                ballLabel.background = color
                gameManager?.onSetColor(color, 3)
            }
            backgroundColorButton -> {
                var color = JColorChooser.showDialog(this, "Choose Color", backgroundLabel.background)
                if (color == null) color = backgroundLabel.background
                backgroundLabel.background = color
                gameManager?.onSetColor(color, 4)
            }
            resetColorButton -> {
                val colors = arrayOf(Color(0xE4A8CA), Color(0xCCAA87), Color(0xBB6588), Color(0x8889CC), Color(0xFFFFFF))
                for (i in 0..4) {
                    colorLabelArray[i].background = colors[i]
                    gameManager?.onSetColor(colors[i], i)
                }
            }

            in keybindButtonArray -> {
                if (isChangingKeybind) {
                    isChangingKeybind = false
                    currentKeybindButton.text = formerButtonText
                } else {
                    currentKeybindButton = e?.source as JButton
                    formerButtonText = currentKeybindButton.text
                    currentKeybindButton.text = "Type Key..."
                    isChangingKeybind = true
                    this.requestFocusInWindow()
                }
            }
            resetKeybindsButton -> {
                if (isChangingKeybind) {
                    isChangingKeybind = false
                    currentKeybindButton.text = formerButtonText
                }
                val texts = arrayOf("Left: Left", "Right: Right", "Left: Comma", "Right: Slash", "Left: A", "Right: D", "Left: F", "Right: H")
                val keys = arrayOf(KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_COMMA, KeyEvent.VK_SLASH, KeyEvent.VK_F, KeyEvent.VK_H)

                for (i in 0..7) {
                    keybindButtonArray[i].text = texts[i]
                    gameManager?.onSetKeybind(keys[i], i)
                }
            }
        }
    }

    override fun keyPressed(e: KeyEvent?) {
        if (isChangingKeybind) {
            isChangingKeybind = false
            val key = e?.keyCode ?: KeyEvent.VK_UNDEFINED
            when (currentKeybindButton) {
                player1Paddle1LeftButton -> {
                    gameManager?.onSetKeybind(key, 0)
                    currentKeybindButton.text = "Left: ${KeyEvent.getKeyText(key)}"
                }
                player1Paddle1RightButton -> {
                    gameManager?.onSetKeybind(key, 1)
                    currentKeybindButton.text = "Right: ${KeyEvent.getKeyText(key)}"
                }
                player2Paddle1LeftButton -> {
                    gameManager?.onSetKeybind(key, 2)
                    currentKeybindButton.text = "Left: ${KeyEvent.getKeyText(key)}"
                }
                player2Paddle1RightButton -> {
                    gameManager?.onSetKeybind(key, 3)
                    currentKeybindButton.text = "Right: ${KeyEvent.getKeyText(key)}"
                }
                player1Paddle2LeftButton -> {
                    gameManager?.onSetKeybind(key, 4)
                    currentKeybindButton.text = "Left: ${KeyEvent.getKeyText(key)}"
                }
                player1Paddle2RightButton -> {
                    gameManager?.onSetKeybind(key, 5)
                    currentKeybindButton.text = "Right: ${KeyEvent.getKeyText(key)}"
                }
                player2Paddle2LeftButton -> {
                    gameManager?.onSetKeybind(key, 6)
                    currentKeybindButton.text = "Left: ${KeyEvent.getKeyText(key)}"
                }
                player2Paddle2RightButton -> {
                    gameManager?.onSetKeybind(key, 7)
                    currentKeybindButton.text = "Right: ${KeyEvent.getKeyText(key)}"
                }
            }
        }
    }

    override fun itemStateChanged(e: ItemEvent?) {
        val box = e?.source as JCheckBox
        val type = getBoxType(box)
        val exclude = e.stateChange == ItemEvent.DESELECTED
        gameManager?.onTogglePowerUp(type, exclude)
    }

    private fun getBoxType(box: JCheckBox): PowerUpType {
        return when (box) {
            increasePaddleSizeBox -> PowerUpType.INCREASE_PADDLE_SIZE
            increasePaddleSpeedBox -> PowerUpType.INCREASE_PADDLE_SPEED
            randomizeBallAngleBox -> PowerUpType.RANDOMIZE_BALL_ANGLE
            randomizeBallSpeedBox -> PowerUpType.RANDOMIZE_BALL_SPEED
            else -> PowerUpType.SPAWN_BALL
        }
    }

    private fun toggleOption(gameOption: GameOption) {
        if (!gameOptionList.contains(gameOption)) gameOptionList.addElement(gameOption)
        else gameOptionList.removeElement(gameOption)
    }

    private fun toggleButtons(vararg buttons: JButton) {
        for (button in buttons) if (button.isEnabled) disableButtons(button)
        else enableButtons(button)
    }

    private fun disableButtons(vararg buttons: JButton) {
        for (button in buttons) {
            button.isEnabled = false
            button.background = Color(0xEBF7FA)
            button.foreground = Color(0x555555)
            button.border = LineBorder(Color.WHITE, 3)
        }
    }

    private fun enableButtons(vararg buttons: JButton) {
        for (button in buttons) {
            button.isEnabled = true
            button.background = Color(0xD1F6FF)
            button.foreground = Color.BLACK
            button.border = LineBorder(Color.WHITE, 3)
        }
    }

    fun setGameEventListener(listener: GameEventListener) {
        this.gameManager = listener
    }

    //no implementation needed
    override fun keyTyped(e: KeyEvent?) {}
    override fun keyReleased(e: KeyEvent?) {}
}