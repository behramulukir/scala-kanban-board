import backend.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.io._
import java.time.LocalDate

//Tests related to the KanbanApp object
class AppTest extends AnyFlatSpec with Matchers:
  val app = backend.KanbanApp

  "App" should "have the correct initial lists" in {
    assert(app.allBoards.isEmpty)
    assert(app.allStages.isEmpty)
    assert(app.allCards.isEmpty)
    assert(app.allTags.isEmpty)
  }


  "App" should "have the 1 board when new board is created with correct name" in {
    app.addBoard("board1")
    assert(app.allBoards.nonEmpty)
    assert(app.allBoards.head.name == "board1")
  }


//Tests related to the Board class
class BoardTest extends AnyFlatSpec with Matchers:
  val app = backend.KanbanApp
  val board = app.addBoard("board1")

  "Board" should "have the correct initial lists" in {
    assert(board.allStages.isEmpty)
    assert(board.allCards.isEmpty)
    assert(board.allTags.isEmpty)
    assert(board.archivedCards.isEmpty)
  }

  "Board" should "have operational stage management functions" in {
    val stage = board.addStage
    assert(board.allStages.nonEmpty)

    board.removeStage(stage)
    assert(board.allStages.isEmpty)
  }

  "Board" should "have operational name-change functions" in {
    board.changeName("newBoard")

    assert(app.allBoards.head.name == "newBoard")
  }


//Tests related to the Stage class
class StageTest extends AnyFlatSpec with Matchers:
  val app = backend.KanbanApp
  val board = app.addBoard("board1")
  val stage = board.addStage

  "Stage" should "have the correct initial lists" in {
    assert(stage.allCards.isEmpty)
  }

  "Stage" should "have operational card management functions" in {
    stage.addCard
    assert(stage.allCards.nonEmpty)

    var card = stage.allCards.head
    stage.removeCard(card)
    assert(stage.allCards.isEmpty)
  }


//Tests related to the Card class (and a little bit of board class)
class CardTest extends AnyFlatSpec with Matchers:
  val app = backend.KanbanApp
  val board = app.addBoard("board1")
  val stage = board.addStage
  stage.addCard
  val card = stage.allCards.head

  "Card" should "have the correct initial lists" in {
    assert(card.tags.isEmpty)
  }

  "Card" should "should have changable description" in {
    assert(card.description == "")

    card.changeDescription("test content")

    assert(card.description == "test content")
  }

  "Card and Board" should "have operational tag management functions" in {
    board.tagAddition("test card")

    assert(board.allTags.nonEmpty)

    var tag = board.allTags.head

    assert(tag.cards.nonEmpty)

    card.removeTag(tag)

    assert(card.tags.isEmpty)
    assert(tag.cards.isEmpty)
  }

  "Card and Board" should "have operational archive management functions" in {
    board.archiveCard(card)

    assert(board.archivedCards.nonEmpty)
    assert(card.archiveStatus == true)

    board.dearchiveCard(card)

    assert(board.archivedCards.isEmpty)
    assert(card.archiveStatus == false)
  }

//Tests related to the file saving functions in KanbanApp object
class saveTest extends AnyFlatSpec with Matchers:

  val app = backend.KanbanApp

  "App" should "create a non-empty Json files to export boards" in {

    val board = app.addBoard("board1")
    val tag1 = board.tagAddition("tag1")
    val tag2 = board.tagAddition("tag2")

    val stage1 = board.addStage
    stage1.changeName("stage1")

    val stage2 = board.addStage
    stage2.changeName("stage2")

    val stage1card1 = stage1.addCard
    stage1card1.changeDescription("stage1card1")
    stage1card1.changeDeadline(LocalDate.now())
    stage1card1.archiveCard()

    val stage1card2 = stage1.addCard
    stage1card2.changeDescription("stage1card2")
    stage1card2.addTag(board.allTags.head)

    val stage2card1 = stage2.addCard
    stage2card1.changeDescription("stage2card1")

    val dirName = "./src/main/data"
    val folder = new File(dirName)

    app.exportBoard(board)
    assert(folder.listFiles().nonEmpty)
  }

  "App" should "read Json files and create the board and its elements accordingly" in {
    app.importBoard("./src/main/data/board1")

    val board = app.allBoards.head

    assert(board.name == "board1")
    assert(board.allTags.size == 2)
    assert(board.allStages.size == 2)
    assert(board.allCards.size == 3)
  }