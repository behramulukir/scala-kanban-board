# Scala Kanban Board

## Definition

The Kanban board is one of the tools that can be used to implement Kanban to manage work at a personal or organizational level. Kanban boards visually depict work at various stages of a process using cards to represent work items and columns to represent each stage of the process. Cards are moved from left to right to show progress and to help coordinate teams performing the work. 

## Features

By definition, a Kanban board includes text-editable cards and the user is free to add new cards and lists. Mostly, the cards contain tags to filter and group along with a deadline attached to the card to keep track of the time left. In this project, cards can be archived by the user and can be returned from the archive pile as well. Also, the cards can be moved to other lists by drag-and-drop motion performed by the users. Also, a single user can have multiple boards to manage. The boards are saved to external JSON files that are imported to recover the last status of the application in case the user launches the application.

## GUI

![General UI](https://github.com/behramulukir/scala-kanban-board/blob/main/documents/General%20UI.png)

The picture above is a screenshot of the GUI in an example use case. There are three main components of the application: a menu bar at the top, a control bar at the left, and a kanban board at the right side of the screen. From the top bar, users can access settings like changing the name of the current board, adding/deleting boards, and saving the current status of the application to external files. From the control bar at the left side, users can access the archive pile of cards and return archived cards back to their lists, see the list of tags and filter cards by the tags, create/delete tags from the board, and add new lists. From the kanban board which is on the right side of the screen, users can interact with lists and cards. They can remove lists or change their names. They can add cards to the lists and interact with the cards from there as well. They can change the card descriptions, delete cards, archive cards, add deadlines, add tags, remove tags, and change the list of cards. They are able to change the lists of cards by simply dragging and dropping them from one list to another.

Below, you can see screenshots of GUI in some example use cases.

<p float="left">
  <img src="https://github.com/behramulukir/scala-kanban-board/blob/main/documents/New%20Tag.png" width="500" />
  <img src="https://github.com/behramulukir/scala-kanban-board/blob/main/documents/Add%20Tag.png" width="500" /> <br>
  <img src="https://github.com/behramulukir/scala-kanban-board/blob/main/documents/Deadline.png" width="500" />
  <img src="https://github.com/behramulukir/scala-kanban-board/blob/main/documents/Quit.png" width="500" />
</p>

## Using the application

The application can be run by running the “App.scala” file under the “GUI” folder, and in the initial launch there is only one board named “Initial Board”. Then users can use the application as they wish and while closing the application, a confirmation dialogue confirms if the user wants to save the changes made during the session. During the next launches of the application, boards, cards, and all the related details will be restored from the external files that are saved. However, if users don’t save the changes then the changes will be lost and they are not going to be transferred to next launches.

## Appendices

Below, you can see the UML diagram of the application and JSON structure used in this project to save/retrieve information in external files.

<p float="left">
  <img src="https://github.com/behramulukir/scala-kanban-board/blob/main/documents/Final_UML.png" width="500" />
  <img src="https://github.com/behramulukir/scala-kanban-board/blob/main/documents/JSON%20Structure.png" width="500" />
</p>
