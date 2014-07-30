------------------------------------------------------------------------
Please use this README.TXT file for all AP Computer Science labs
------------------------------------------------------------------------

PROJECT TITLE/PURPOSE: GameLand
PURPOSE OF PROJECT: Loops and such
VERSION or DATE: 2014-31-1
AUTHORS: Richard Li
DESCRIPTION OF PROJECT:
Class Name(s): Board, Dice, Game, Player
Class Methods:
                Board:
                    gameOver()
                    genPlayerInfo()
                    gridsize()
                    logic()
                    out()
                    updateButton()
                    updateButtons()
                    
                Dice:
                    diceRoll()
                    probability()
                    
                Player:
                    getPos()
                    setPos()
                    getMoves()
                    move()
                    setName()
                    toString()
                    
                Game:
                    genSeed()
                    diceRoll() <----- Dice.diceroll()
                    calcSpaces()
                    doTurn()
                    doRound()
                    next()
                    main()/TestMe()
