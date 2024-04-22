package gui

import backend.*

import scala.collection.mutable.*
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.{AnchorPane, BorderPane, Pane, VBox}
import scalafx.geometry.Pos
import scalafx.scene.control.{Menu, MenuBar, MenuButton, MenuItem, TextInputDialog}
import scalafx.scene.paint.*

import java.io.File
import scala.collection.mutable
import scala.language.implicitConversions

object App extends JFXApp3:

  //Variable to store current board in a safe way
  var currentBoardOption: Option[Board] = None

  //Variable to see if this is a first launch of the application
  var freshStart = true

  //Directory where files are saved
  val dirName = "./src/main/data"
  val folder = new File(dirName)

  //Big container for the GUI
  var mainContainer = new VBox()
  //I don't have CSS stylesheet yet, I have only created fxml UI draft
  //stylesheets = Seq("fxml/styles.css")

  override def start(): Unit =

    if freshStart then
      if folder.list().nonEmpty then
        for file <- folder.list() do
          KanbanApp.importBoard(folder.getPath + "/" + file)
      else
        KanbanApp.addBoard("Initial Board")

      currentBoardOption = KanbanApp.allBoards.headOption

    freshStart = false


    var currentBoard = currentBoardOption.get

    mainContainer = new VBox()

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
