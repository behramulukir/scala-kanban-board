package gui

import backend.*

import scala.collection.mutable.*
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.{AnchorPane, Border, BorderPane, BorderStroke, BorderStrokeStyle, BorderWidths, CornerRadii, HBox, Pane, StackPane, VBox}
import scalafx.geometry.Pos
import scalafx.geometry.Pos.Center
import scalafx.scene.control.{Button, ButtonBar, Menu, MenuBar, MenuButton, MenuItem, TextField, TitledPane}
import scalafx.scene.paint.*

import scala.collection.mutable
import scala.language.implicitConversions

class StageUI(parentPane: TitledPane, board: Board, stage: Stage) extends VBox{

  this.prefHeight <== parentPane.height
  this.prefWidth = 200

  this.setSpacing(6)

  //Stage name and add card button
  val buttonAndText = new HBox()
  buttonAndText.prefHeight <== this.height * 0.1
  buttonAndText.prefWidth <== this.width

  val stageNameUI = new TextField():
    text = stage.name
    prefHeight <== buttonAndText.height
    prefWidth <== buttonAndText.width * 0.6

  val addCardButton = new Button("Add Card"):
    prefHeight <== buttonAndText.height
    prefWidth <== buttonAndText.width * 0.4

  buttonAndText.children += stageNameUI
  buttonAndText.children += addCardButton

  this.children += buttonAndText


  //Adding CardUIs to the box
  for i <- stage.allCards do {
    var cardToAdd = new CardUI(this, i)
    var cardPane = new StackPane()
    cardPane.children = cardToAdd
    cardPane.setAlignment(Center)
    cardPane.setBorder(new Border(new BorderStroke(i.color, BorderStrokeStyle.Solid, CornerRadii(2), BorderWidths(3))))
    this.children += cardPane

  }

  //Adding border to stages to make it easy to distinguish
  this.setBorder(new Border(new BorderStroke(Color.Black, BorderStrokeStyle.Solid, CornerRadii.Empty, BorderWidths(2))))

}