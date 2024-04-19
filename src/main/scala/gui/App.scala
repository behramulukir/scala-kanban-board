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

  //Menu 1 - Settings. There are menu items for things such as changing board name
  val setting = new Menu("Settings") {
    val nameChange = new MenuItem("Change name"):
      onAction = (event) => {
        val dialog = new TextInputDialog(defaultValue = "Board Name") {
          initOwner(App.stage)
          title = "Change board name"
          headerText = "Enter the new name for the board."
          contentText = "New name for the current board:"
    }

    val result = dialog.showAndWait()
    currentBoard.changeName(result.getOrElse(currentBoard.name))
    start()
  }

    val addBoard = new MenuItem("Add board"):
      onAction = (event) => {
        val dialog = new TextInputDialog(defaultValue = "New Board") {
        initOwner(App.stage)
        title = "Create a new board"
        headerText = "Enter the board name."
        contentText = "Name of the new board:"
      }
    val result = dialog.showAndWait()
    var newBoard = KanbanApp.addBoard(result.getOrElse("New Board"))
    addBoardUI(newBoard)
  }

    items = List(nameChange, addBoard)
  }

  //Menu 2 - Boards. There is a menu item for each board of KanbanApp
  val boards = new Menu("Boards")

  //Menu 3 - File Management. There are two menu items, one for import and one for export
  val fileManagement = new Menu("File Management") {
    val importing = new MenuItem("Import")
    val exporting = new MenuItem("Export")
    items = List(importing, exporting)
    }

  //Function to add boards to the menu bar button
  def addBoardUI(board: Board) = {
    App.boards.items += new BoardItem(board):
      onAction = (event) => {
        App.currentBoard = KanbanApp.allBoards.filter(_.identifier == board.identifier).head
        App.start()
      }
  }

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

    //Adding boards to the top bar
    boards.items.clear()
    for i <- KanbanApp.allBoards do boards.items += new BoardItem(i):
      onAction = (event) => {
        currentBoard = KanbanApp.allBoards.filter(_.identifier == i.identifier).head
        App.start()
      }

    //Top menu bar for some settings such as changing to other boards, import, and export
    val topBar = new MenuBar(){
      prefHeight <== mainContainer.height * 0.05
      prefWidth <== mainContainer.width
    }

    topBar.menus = List(setting, boards, fileManagement)
    mainContainer.children += topBar


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
