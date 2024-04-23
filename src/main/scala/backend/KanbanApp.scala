package backend

import gui.App.folder
import play.api.libs.json

import scala.collection.mutable.*
import scala.io.*
import java.time.*
import java.io.*
import play.api.libs.json.*

import java.nio.charset.StandardCharsets
import java.nio.file.*

object KanbanApp:
  
  //Lists for all elements
  var allBoards: Buffer[Board] = Buffer()
  var allStages: Buffer[Stage] = Buffer()
  var allCards: Buffer[Card] = Buffer()
  var allTags: Buffer[Tag] = Buffer()
  
  //Last board that user was interacting
  var lastBoard: Option[Board] = None

  //Adding a board to the app
  def addBoard(name: String): Board =
    val board = new Board(name)
    allBoards = allBoards.addOne(board)
    board
  end addBoard
  
  //Deleting a board from the app
  def deleteBoard(board: Board) =
    allBoards.remove(allBoards.indexOf(board))
  end deleteBoard

  //File saving functions in the form of exporting a board and creating a new board based on a Json file
  //Play library (https://www.playframework.com/documentation/3.0.x/ScalaJson) was used for Json related functions

  //Function to export boards
  def exportBoard(board: Board): Unit =

    //Json writer for cards
    implicit val cardWrites: Writes[Card] = new Writes[Card] {
      def writes(card: Card) = Json.obj(
        "cardID"  -> card.identifier,
        "cardDescription" -> card.description,
        "deadline" -> {if card.deadline.isDefined then card.deadline.get.toString else "None"},
        "tags" -> card.tags.map(_.name),
        "archiveStatus" -> card.archiveStatus
      )
    }

    //Json writer for stages
    implicit val stageWrites: Writes[Stage] = new Writes[Stage] {
      def writes(stage: Stage) = Json.obj(
        "stageID"  -> stage.identifier,
        "stageName" -> stage.name,
        "cards" -> stage.allCards
      )
    }

    //Json writer for boards
    implicit val boardWrites: Writes[Board] = new Writes[Board] {
      def writes(board: Board) = Json.obj(
        "boardID"  -> board.identifier,
        "boardName" -> board.name,
        "boardTags" -> board.allTags.map(_.name),
        "stages" -> board.allStages
      )
    }

    //Saving Json to external file
    val jsonString = Json.toJson(board)
    val targetDir = s"./src/main/data/${board.name}"

    //Writing the file if the folder exists
    Files.write(Paths.get(targetDir), jsonString.toString.getBytes(StandardCharsets.UTF_8))

    //IO exception handling - Does target file exist
    val myFileWriterAfterCheck =
      try FileReader(targetDir)
      catch
        case e: FileNotFoundException =>
          throw new FileNotFoundException("Target file doesn't exists")




  //Function to create boards based on the given Json files
  def importBoard(directory: String): Unit =

    //I/O exception handling - Does source file exists
    val myFileReader =
      try FileReader(directory)
      catch
        case e: FileNotFoundException =>
          throw new FileNotFoundException("Source file doesn't exists")

    // Case class to read Json
    case class CardCase(cardID: String, cardDescription: String, deadline: String, tags: List[String], archiveStatus: Boolean)
    case class StageCase(stageID: String, stageName: String, cards: List[CardCase])
    case class BoardCase(boardID: String, boardName: String, boardTags: List[String], stages: List[StageCase])

    //Reading values for each case class
    implicit val readCard: Reads[CardCase] = Json.reads[CardCase]
    implicit val readStage: Reads[StageCase] = Json.reads[StageCase]
    implicit val readBoard: Reads[BoardCase] = Json.reads[BoardCase]

    //Reading Json file and parsing it
    val jsonStream = Source.fromFile(directory)
    val jsonRead = jsonStream.getLines.mkString
    val jsonString = Json.parse(jsonRead)

    //Creating board case class based on the Json
    val caseBoard = jsonString.as[BoardCase]

    //Reading the case class BoardCase and creating real boards based on that
    var newBoard = this.addBoard(caseBoard.boardName)
    newBoard.identifier = caseBoard.boardID
    for tag <- caseBoard.boardTags do
      newBoard.tagAddition(tag)
    for stage <- caseBoard.stages do
      var newStage = newBoard.addStage
      newStage.changeName(stage.stageName)
      newStage.identifier = stage.stageID
      for card <- stage.cards do
        val newCard = newStage.addCard
        newCard.changeDescription(card.cardDescription)
        newCard.identifier = card.cardID
        newCard.archiveStatus = card.archiveStatus
        if newCard.archiveStatus then
          newBoard.archiveCard(newCard)
        if card.deadline != "None" then
          newCard.changeDeadline(LocalDate.parse(card.deadline))
        for tag <- card.tags do
          var chosenTag = newBoard.allTags.filter(_.name == tag).head
          newCard.addTag(chosenTag)
      
      
      jsonStream.close()