package backend

import java.time.LocalDate
import scala.collection.mutable
import scala.collection.mutable._

//Class for checklist
class Checklist {

  //Unique ID value
  private val random = scala.util.Random
  val identifier: String = "2/" + LocalDate.now.toString + "/" + random.nextInt(100000).toString

  //Lists for completed and uncompleted steps
  var completedSteps: Buffer[Step] = Buffer()
  var uncompletedSteps: Buffer[Step] = Buffer()

  //Variable for rate of progress so far
  var progress = completedSteps.size / (completedSteps.size + uncompletedSteps.size)

  //Function for adding a new step to the checklist
  def addStep(desc: String) = {
    val step = new Step(desc, uncompletedSteps)
    uncompletedSteps = uncompletedSteps.addOne(step)
  }
  
  //Function for removing the step from the checklist
  def removeStep(step: Step): Unit ={
    step.bufferType.remove(step.bufferType.indexOf(step))
  }

  //Function for marking a step completed, it removes the step and adds it as completed
  def completeStep(step: Step): Unit ={
    removeStep(step)
    completedSteps = completedSteps.addOne(step)
  }
}

//Class for checklist subitem, Step
class Step(desc: String, listType: mutable.Buffer[Step]){

  //Textual content of step
  var description = desc

  //Function for changing textual content
  def changeDescription(newDesc: String) = {
    description = newDesc
  }

  //Which list the step is at, completed or uncompleted
  var bufferType = listType

  //Function for changing the list type of step
  def changeType(newType: Buffer[Step]) ={
    bufferType = newType
  }
}

