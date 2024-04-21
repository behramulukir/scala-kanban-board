package backend

import scalafx.scene.paint.*
import scalafx.scene.paint.Color.rgb

import scala.collection.mutable.*
import scala.io.*
import java.time.*
import java.io.*
import java.awt.*
import java.time.format.DateTimeFormatter

object KanbanApp:
  
  //Lists for all elements
  var initialBoard = new Board("Initial Board")
  var allBoards: Buffer[Board] = Buffer(initialBoard)
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

//Import and Export functins are commented out, they are not working at the moment
  /*
  //Function to import from files
  //Template part is not ready yet
  def importFunc(file: String) =
    val board: Board = this.addBoard(file)
    var stage: Stage = null
    var card: Card = null
    var checklistItem: Checklist = null

    try
      val bufferedSource = Source.fromFile(file)
      for line <- bufferedSource.getLines do
        if line.substring(0,5) == "Stag:" then
          var id = line.drop(5)
          stage = board.addStageWithID(id)
        else if line.substring(0,5) == "Card:" then
          var cardId = line.drop(5)
          card = stage.addCardWithID(cardId)
        else if line.substring(0,5) == "Text:" then
          var textContent = line.drop(5)
          card.changeDescription(textContent)
        else if line.substring(0,5) == "Tags:" then
          var tags = (line.drop(6)).split(" ")
          for i <- tags do
            card.tagCreation(i)
        else if line.substring(0,5) == "Colo:" then
          var colors = line.drop(5).dropRight(1).split(", ")
          var red = colors(0).toInt
          var green = colors(1).toInt
          var blue = colors(2).toInt
          card.changeColor(rgb(red, green, blue))
        else if line.substring(0,5) == "Deli:" then
          if line.drop(5) != "" then
            card.changeDeadline(LocalDate.parse(line.drop(5)))
        else if line.substring(0,5) == "Chck:" then
          if line.drop(5) != "" then
            var checks: Array[String] = line.drop(5).split(" - ")
            var uncompletedSteps: String = checks(1)
            var completedSteps: String = checks(0)
            uncompletedSteps = uncompletedSteps.drop(1).dropRight(1)
            completedSteps = completedSteps.drop(1).dropRight(1)
            var completedArray = completedSteps.split(", ")
            var undcompletedArray = uncompletedSteps.split(", ")
            for i <- completedArray do
              var checklist = card.checklist.get
              checklist.addCompletedStep(i)
            for i <- undcompletedArray do
              var checklist = card.checklist.get
              checklist.addUncompletedStep(i)
        else if line.substring(0,5) == "Acti:" then
          if line.drop(5) != "" then
            var activity = line.drop(5).split(", ")
            card.addInteraction(activity(0).toInt, activity(1))
        else if line.substring(0,5) == "Clin:" then
          if line.drop(5) != "" then
            var cardsAttach = line.drop(5).split(" - ")
            var attachmentCard = board.allCards.filter(i => i.cardIdentifier == cardsAttach(0))
            var cardAttach = new cardAttachment(attachmentCard.head)
            var cardAttachTo = board.allCards.filter(i => i.cardIdentifier == cardsAttach(1))
            cardAttachTo.head.addAttachment(cardAttach)
        else if line.substring(0,5) == "Flin:" then
          if line.drop(5) != "" then 
            var attachElements = line.drop(5)
            var fileAttach = new fileAttachment(new File(attachElements))
            card.addAttachment(fileAttach)
        else if line.substring(0, 5) == "Arch:" then
          var archPar = line.drop(5)
          if archPar == "T" then
            card.archiveCard()
        else 
          println("empty string")
        bufferedSource.close
      catch 
        case fnf: FileNotFoundException => fnf.printStackTrace()
        case ioe: IOException => ioe.printStackTrace()
      
  end importFunc



  //Export function to export boards to files
  //Remember to put card attachment to the very end of the text file
  //Template part is not ready yet
  def exportFunc(board: Board) =
    val file = new File(board.name + ".txt")

    val bufferedWriter = new BufferedWriter(new FileWriter(file))

    for stage <- allStages do
      var stageID = "Stag:" + stage.identifier
      bufferedWriter.write(stageID)
      bufferedWriter.newLine()
      for card <- stage.allCards do
        var cardID = "Card:" + card.cardIdentifier
        bufferedWriter.write(cardID)
        bufferedWriter.newLine()
        var cardText = "Text:" + card.description
        bufferedWriter.write(cardText)
        bufferedWriter.newLine()
        var tagText = "Tags:"
        for tag <- card.tags do
          tagText = tagText + " " + tag.name
        bufferedWriter.write(tagText)
        bufferedWriter.newLine()
        var colorText = "Colo:(" + card.color.getRed.toString + ", " + card.color.getGreen.toString + ", " + card.color.getBlue.toString + ")"
        bufferedWriter.write(colorText)
        bufferedWriter.newLine()
        var deadlineText = "Deli:"
        if card.deadline.isDefined then
          deadlineText = deadlineText + card.deadline.get.format(DateTimeFormatter.ISO_LOCAL_DATE)
        else
        bufferedWriter.write(deadlineText)
        bufferedWriter.newLine()
        var checklistText = "Chck:("
        var checklistItems = card.checklist.get
        if card.checklist.nonEmpty then
          for compStep <- checklistItems.completedSteps do
            checklistText = checklistText + compStep.description + ", "
          checklistText = checklistText.dropRight(2) + ") - ("
          for uncompStep <- checklistItems.uncompletedSteps do
            checklistText = checklistText + uncompStep.description + ", "
          checklistText = checklistText.dropRight(2) + ")"
        else 
          checklistText = "Chck:"
        bufferedWriter.write(checklistText)
        bufferedWriter.newLine()
        var activityText = "Acti:"
        if card.interactionInfo != (-1, "") then
          activityText = activityText + card.interactionInfo(0) + ", " + card.interactionInfo(1)
        bufferedWriter.write(activityText)
        bufferedWriter.newLine()
        var fileAttachmentText = "Flin:"
        if card.attachments.isEmpty then
          if card.attachments.get.attachmentType == "File" then
            var fileAttachment = card.attachments.get
            fileAttachmentText = fileAttachmentText + fileAttachment.location
        bufferedWriter.write(fileAttachmentText)
        bufferedWriter.newLine()
        var archiveText = "Arch:"
        if card.archiveParameter then
          archiveText = archiveText + "T"
        else
          archiveText = archiveText + "F"
        bufferedWriter.write(archiveText)
        bufferedWriter.newLine()

    for card <- board.allCards do
      var cardAttachmentText = "Clin:"
      if card.attachments.nonEmpty then
        if card.attachments.get.attachmentType == "Card" then
          var cardAttachment = card.attachments.get
          cardAttachmentText = cardAttachmentText + card.cardIdentifier + " - " + cardAttachment.cardID
      bufferedWriter.write(cardAttachmentText)
      bufferedWriter.newLine()

    bufferedWriter.close()
  
  end exportFunc
  */