## 8-MagicDucks 

A strategic puzzle game inspired by the classic 8-Queens chess problem, built with Java and OpenGL. 
Command your duck soldiers and arrange them on the chessboard so that no two soldiers can attack each other!

🎮 Game Overview

In this game, 8 duck soldiers are randomly placed on a chessboard. Your mission is to rearrange them following 
the 8-Queens rule: no two soldiers can be in the same row, column, or diagonal. Race against time, challenge a 
friend, or test your skills against an AI opponent!

✨ Features

Single Player Mode: Race against time with 3 lives. Solve puzzles to earn points!
Two Player Mode: Compete head-to-head with a friend using keyboard controls
AI Mode: Challenge an intelligent computer opponent
Dynamic Difficulty: Randomly generated board configurations
Score System: Earn points based on remaining time
Lives System: 3 lives per player - use them wisely!
Smooth Animations: OpenGL-powered graphics and animations
Sound Effects: Immersive audio feedback

🎯 Game Modes
1-Player Mode

Objective: Solve as many boards as possible before losing all 3 lives
Rules:

Start with 3 lives and a countdown timer
Arrange all 8 soldiers so no two attack each other
Solve before time runs out: board resets, timer resets, remaining time added to score
Fail to solve in time: board resets, timer resets, lose 1 life, no score added
Game over when all 3 lives are lost



2-Player Mode

Objective: Outlast your opponent or be the first to solve 5 boards
Controls:

Move between squares: PgUp, PgDown, and arrow keys
Select soldier: Shift + Direction
Each player uses their own keyboard section


Win Conditions:

Opponent loses all 3 lives first, OR
First player to solve 5 boards wins



AI Mode

Objective: Same as 2-Player mode, but against a computer opponent
AI Difficulty: Intelligent solving algorithm with strategic decision-making
Win Conditions: Same as 2-Player mode

🕹️ Controls
Player 1

Move Cursor: Arrow Keys (↑, ↓, ←, →)
Select/Move Soldier: Shift + Arrow Keys
Navigate Squares: PgUp, PgDown

Player 2 (2-Player Mode)

Move Cursor: WASD
Select/Move Soldier: Ctrl + WASD
Navigate Squares: Q, E

🏗️ Project Structure

/src
    |__ Assets/             # Game resources
    |   |__ Players/        # Duck soldier sprites
    |   |__ Sounds/         # Audio files
    |   |__ Background/     # Background images
    |   |__ Buttons/        # UI button graphics
    |
    |__ Board/              # Board rendering and game loop
    |   |__ Board.java
    |   |__ BoardListener.java
    |   |__ onePlayerGLListener.java
    |   |__ twoPlayersGLListener.java
    |   |__ AIGLListener.java
    |
    |__ Entities/           # Game objects
    |   |__ Animation.java
    |   |__ Tile.java
    |   |__ Players.java
    |
    |__ Texture/            # Texture loading
    |   |__ BitmapLoader.java
    |   |__ ResourceRetriever.java
    |   |__ TextureReader.java
    |
    |__ Utils/              # Utility classes
    |   |__ Collision.java
    |   |__ InputHandler.java
    |   |__ Sound.java
    |   |__ Timer.java
    |
    |__ Game/               # Game logic
        |__ Player.java
        |__ AIPlayer.java
        |__ MultiplayerManager.java
        |__ GameManager.java
        |__ GameState.java
        |__ LevelManager.java
