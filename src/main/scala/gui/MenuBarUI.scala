package gui

import backend.*
import javafx.geometry

import scala.collection.mutable.*
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.{AnchorPane, BorderPane, HBox, Pane, StackPane, VBox}
import scalafx.geometry.Pos
import scalafx.geometry.Pos.Center
import scalafx.scene.control.{ComboBox, Menu, MenuBar, MenuButton, MenuItem, Button}
import scalafx.scene.paint.*

import scala.collection.mutable
import scala.language.implicitConversions

//Pane that contains left-side settings such as archive, tags, add board, add list
class MenuBarUI(parentPane: HBox, boardui: BoardUI) extends VBox{


  var currentBoard = boardui.currentBoard

  this.prefHeight <== parentPane.height
  this.prefWidth <== parentPane.width * 0.15

  //Archive button showing all archived cards with their descriptions
  val archiveButton = new MenuButton("Archive"):
    val archiveList = Buffer[MenuItem]()
    for i <- currentBoard.archivedCards do archiveList += new MenuItem(i.description)
    items = archiveList.toList

  val archivePane = new StackPane():
    prefHeight <== parentPane.height / 3
    children += archiveButton

  //Tags button showing all archived cards with their descriptions
  val tagsButton = new MenuButton("Tags"):
    val tagsList = Buffer[MenuItem]()
    for i <- currentBoard.allTags do tagsList += new MenuItem(i.name)
    items = tagsList.toList

  val tagsPane = new StackPane():
    prefHeight <== parentPane.height / 3
    children += tagsButton

  //List button to add lists
  val listButton = new Button("Add List"):
    onAction = (event) => {
      var newStage = boardui.currentBoard.addStage
      boardui.addStageUI(newStage)
    }
  val listButtonPane = new StackPane():
    prefHeight <== parentPane.height / 6
    children += listButton

  //Board button to add boards
  val boardButton = new Button("Add Board")
  val boardButtonPane = new StackPane():
    prefHeight <== parentPane.height / 6
    children += boardButton


  //Adding buttons to left pane and adjusting its location
  this.children += archivePane
  this.children += tagsPane
  this.children += listButtonPane
  this.children += boardButtonPane

}