package gui

import backend.*
import javafx.scene.control.ScrollPane.ScrollBarPolicy

import scala.collection.mutable.*
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.{AnchorPane, BorderPane, HBox, Pane, VBox}
import scalafx.geometry.Pos
import scalafx.scene.control.ScrollPane.ScrollBarPolicy.Always
import scalafx.scene.control.{Menu, MenuBar, MenuButton, MenuItem, ScrollPane, TitledPane}
import scalafx.scene.paint.*

import scala.collection.mutable
import scala.language.implicitConversions

class BoardUI(parentPane: HBox, board: Board) extends TitledPane{

  //Defining the title of titled pane
  this.text = board.name


  //Defining the dimensions
  this.prefHeight <== parentPane.height
  this.prefWidth <== parentPane.width * 0.85


  //Creating ScrollPane
  var boardScroll = new ScrollPane()
  boardScroll.pannable = true

  //Creating BoardHBox
  var boardHBox = new HBox()
  boardHBox.setSpacing(10)

  //Adding Hbox to ScrollPane
  boardScroll.setContent(boardHBox)

  //Setting Board Pane as the content of titled pane
  this.setContent(boardScroll)

  //Adding StageUI to BoardHBox
  for i <- board.allStages do boardHBox.children += new StageUI(this, board, i)


}

