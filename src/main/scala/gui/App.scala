package gui

import backend.*
import scala.collection.mutable.*
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.{AnchorPane, BorderPane, Pane, VBox}
import scalafx.geometry.Pos
import scalafx.scene.control.{Menu, MenuBar, MenuButton, MenuItem}
import scalafx.scene.paint.*

import scala.collection.mutable
import scala.language.implicitConversions

object App extends JFXApp3:

  override def start(): Unit =

    val kanban = backend.KanbanApp()
    kanban.addBoard("Initial Board")
    var currentBoard = kanban.allBoards.head

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


    //Big container for the GUI
    val mainContainer = new VBox()
      //I don't have CSS stylesheet yet, I have only created fxml UI draft
      //stylesheets = Seq("fxml/styles.css")

    //Top menu bar for some settings such as changing to other boards, import, and export
    val topBar = new MenuBar():
      prefHeight <== mainContainer.height * 0.05
      prefWidth <== mainContainer.width

       //Menu 1 - Settings. There are menu items for things such as changing board name
       val setting = new Menu("Settings"):
         val nameChange = new MenuItem("Change name")

         items = List(nameChange)

       //Menu 2 - Boards. There is a menu item for each board of KanbanApp
       val boards = new Menu("Boards"):
         for i <- kanban.allBoards do items += new MenuItem(i.name)

        //Menu 3 - File Management. There are two menu items, one for import and one for export
       val fileManagement = new Menu("File Management"):

         val importing = new MenuItem("Import")
         val exporting = new MenuItem("Export")

         items = List(importing, exporting)

       menus = List(setting, boards, fileManagement)

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
