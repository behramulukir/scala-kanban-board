package backend

import java.io.File
import java.time.LocalDate

//Abstract class for attachment
abstract class Attachment {
  
  //Randomly created unique Identifier value
  private val random = scala.util.Random
  var identifier:String = "4/" + LocalDate.now.toString + "/" + random.nextInt(100000).toString
  
  //Type of attachment, cannot be changed
  val attachmentType: String
}

//Class for card attachment
class cardAttachment(card: Card) extends Attachment {
  val attachmentType = "Card"
  val cardID: String = card.identifier
}

//Card for file attachment
class fileAttachment(file: File) extends Attachment {
  val attachmentType = "File"
  val location = file.getPath
}