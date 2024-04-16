package gui

import backend.*

import scala.collection.mutable.*
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.{AnchorPane, BorderPane, HBox, Pane, StackPane, VBox}
import scalafx.geometry.Pos
import scalafx.scene.control.{Button, Menu, MenuBar, MenuButton, MenuItem, ScrollPane}
import scalafx.scene.paint.*

import scala.collection.mutable
import scala.language.implicitConversions

class KanbanUI(parentBox: VBox, board: Board) extends HBox:

  //Defining the current board
  var currentBoard = board

  //Defining the dimensions
  this.prefHeight <== parentBox.height * 0.95
  this.prefWidth <== parentBox.width

  //Creating menu bar and adding it
  var menubarui = new MenuBarUI(this, currentBoard)
  this.children += menubarui

  //Creating board ui and adding it
  var boardui = new BoardUI(this, currentBoard)
  this.children += boardui

 

  




