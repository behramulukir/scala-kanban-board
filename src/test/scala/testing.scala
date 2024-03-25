import backend.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

//Tests related to the KanbanApp class
class AppTest extends AnyFlatSpec with Matchers:
  val app = backend.KanbanApp()

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
  val app = backend.KanbanApp()
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
  val app = backend.KanbanApp()
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


//Tests related to the Card class
class CardTest extends AnyFlatSpec with Matchers:
  val app = backend.KanbanApp()
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
  
  "Card" should "have operational tag management functions" in {
    card.tagAddition("test card")
    
    assert(card.tags.nonEmpty)
    
    var tag = card.tags.head
    
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