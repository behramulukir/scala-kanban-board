package gui

import backend.*

import scala.collection.mutable.*
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.{AnchorPane, BorderPane, Pane, VBox}
import scalafx.geometry.Pos
import scalafx.scene.control.{Menu, MenuBar, MenuButton, MenuItem, TextInputDialog}
import scalafx.scene.paint.*

import scala.collection.mutable
import scala.language.implicitConversions

object App extends JFXApp3:

  var currentBoard = KanbanApp.allBoards.head

  //Big container for the GUI
  var mainContainer = new VBox()
  //I don't have CSS stylesheet yet, I have only created fxml UI draft
  //stylesheets = Seq("fxml/styles.css")

  override def start(): Unit =

    mainContainer = new VBox()

    var stage1 = currentBoard.addStage
    var card1 = stage1.addCard
    var card11 = stage1.addCard

    var stage2 = currentBoard.addStage
    var card2 = stage2.addCard

    var stage3 = currentBoard.addStage
    var card3 = stage3.addCard

    var stage4 = currentBoard.addStage
    var card4 = stage4.addCard

    var stage5 = currentBoard.addStage
    var card5 = stage5.addCard


    mainContainer.children += TopBarUI(mainContainer)

    if KanbanApp.allBoards.nonEmpty then
      var kanbanui = new KanbanUI(mainContainer, currentBoard)
      mainContainer.children += kanbanui

    //Thing that runs the application
    stage = new JFXApp3.PrimaryStage:
        title = "Kanban App"
        width = 800
        height = 600
        scene = new Scene:
          fill = Color.rgb(245, 245, 245)
          root = mainContainer
