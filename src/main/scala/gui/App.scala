package gui

import backend.*
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.layout.VBox
import scalafx.scene.control.{Alert, ButtonType, MenuBar}
import scalafx.scene.paint.*

import java.io.{File, FileNotFoundException}
import scala.language.implicitConversions

object App extends JFXApp3:

  //Variable to store current board in a safe way
  var currentBoardOption: Option[Board] = None

  //Variable to see if this is a first launch of the application
  var freshStart = true

  //Directory where files are saved
  val dirName = "./src/main/data"
  val folder = new File(dirName)

  //IO exception handling - Does target folder exist
  val myFileWriterAfterCheck =
    if !folder.exists() then
      throw new FileNotFoundException("Target folder not found")


  //Big container for the GUI
  var mainContainer = new VBox()

  override def start(): Unit =

    //If this is initial launch of the application
    //And if Json files exists it imports boards from external files
    //If Json files don't exists then it creates an empty initial board
    if freshStart then
      if folder.list().nonEmpty then
        for file <- folder.list() do
          KanbanApp.importBoard(folder.getPath + "/" + file)
      else
        KanbanApp.addBoard("Initial Board")

      currentBoardOption = KanbanApp.allBoards.headOption

    freshStart = false


    //Current board registery
    var currentBoard = currentBoardOption.get

    //Big container for the GUI
    mainContainer = new VBox()

    //Adding top menu bar to GUI
    mainContainer.children += TopBarUI(mainContainer)

    //If a board exists, it creates the kanban ui and adds it
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


    //Asking user if they want to save their files while they are closing the application
    stage.onCloseRequest = (event) => {
      val alert = new Alert(AlertType.Confirmation) {
        initOwner(stage)
        title = "Saving changes"
        headerText = "Would you like to save your boards?"
        contentText = "Your changes will be deleted if you don't save."
      }

      val result = alert.showAndWait()

      //Save according to the user choice
      result match {
        case Some(ButtonType.OK) => {
          App.folder.listFiles().foreach(_.delete())
          for board <- KanbanApp.allBoards do
            KanbanApp.exportBoard(board)
        }
        case _                   =>
      }

    }

