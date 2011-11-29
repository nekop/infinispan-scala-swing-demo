package org.infinispan.demo

import org.infinispan.Cache
import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConversions._
import scala.swing.event.{Event => _, _}
import scala.swing.{SwingWorker => _, _}
import eu.flierl.grouppanel.GroupPanel

class DataDialog(owner: Window, val cache: Cache[String, String]) extends Dialog(owner) {
  modal = true
  title = "Data"
  val cells = new ArrayBuffer[Array[Any]]()
  cache.getAdvancedCache.getDataContainer.foreach(entry => {
    val cell = Array[Any](entry.getKey, entry.getValue, entry.getLifespan, entry.getMaxIdle)
    cells += cell
  })
  val headers = Array[Any]("key", "value", "lifespan", "maxIdle")

  val noteLabel = new Label("This table is read-only")
  val dataTable =  new Table(cells.toArray, headers)
  val dataTableContainer =  new ScrollPane(dataTable)
  val closeButton = new Button("Close")

  listenTo(closeButton)
  reactions += {
    case ButtonClicked(`closeButton`) => {
      DataDialog.this.dispose
    }
  }

  contents = new GroupPanel {
    theHorizontalLayout is Sequential(
      Parallel(noteLabel, dataTableContainer, closeButton)
    )

    theVerticalLayout is Sequential(
      Parallel(Baseline)(noteLabel),
      Parallel(Baseline)(dataTableContainer),
      Parallel(Baseline)(closeButton)
    )
  }

}
