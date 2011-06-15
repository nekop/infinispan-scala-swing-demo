package org.infinispan.demo

import javax.swing.JOptionPane
import javax.swing.SwingWorker
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.swing.{SwingWorker => _, _}


class TopFrame(app: SimpleSwingApplication) extends MainFrame {

  RefreshThread.start

  var cacheCounter = 1

  val topFrame = this
  val cachePanelList = ListBuffer[CachePanel]()
  val topPanel = new TopPanel
  val mainPanel = new BoxPanel(Orientation.Vertical)
  val scrollPane =  new ScrollPane() {
    verticalScrollBarPolicy = ScrollPane.BarPolicy.Always
    preferredSize = new Dimension(200, 200)
    contents = mainPanel
  }
  val statusPanel = new StatusPanel
  
  title = "Infinispan Swing Demo"
  menuBar = new MenuBar {
    contents += new Menu ("File") {
      contents += new MenuItem(Action("Exit") {
        app.quit
      })
    }
    contents += new Menu ("Config") {
      contents += new MenuItem(Action("Cache bar capacity") {
        val value = showInput[String]("Please input value", "Cache bar capacity")
        if (value.nonEmpty) {
          InfinispanSwingDemo.topFrame.cachePanelList.foreach(cachePanel => {
            try {
              cachePanel.cacheBar.max = value.get.toInt
            } catch {
              case _ => ; // ignore
            }
          })
        }
      })
    }
    contents += new Menu ("Cache Operation") {
      contents += new MenuItem(Action("Add entry") {
        val key = showInput("Please input key", "Add entry")
        if (key.nonEmpty) {
          val value = showInput("Please input value", "Add entry")
          if (value.nonEmpty) {
            InfinispanSwingDemo.topFrame.statusPanel.progressBar.indeterminate = true
            new SwingWorker[Unit, Unit] {
              def doInBackground {
                try {
                  InfinispanSwingDemo.topFrame.cachePanelList.head.cache.put(key.get, value.get)
                } catch {
                  case ex: Exception => ex.printStackTrace
                }
              }
              override def done {
                InfinispanSwingDemo.topFrame.statusPanel.progressBar.indeterminate = false
              }
            }.execute
          }
        }
      })
      contents += new MenuItem(Action("Remove entry") {
        val key = showInput("Please input key", "Remove entry")
        if (key.nonEmpty) {
          InfinispanSwingDemo.topFrame.statusPanel.progressBar.indeterminate = true
          new SwingWorker[Unit, Unit] {
            def doInBackground {
              try {
                InfinispanSwingDemo.topFrame.cachePanelList.head.cache.remove(key.get)
              } catch {
                case ex: Exception => ex.printStackTrace
              }
            }
            override def done {
              InfinispanSwingDemo.topFrame.statusPanel.progressBar.indeterminate = false
            }
          }.execute
        }
      })
      contents += new Separator
      contents += new MenuItem(Action("Clear all") {
        InfinispanSwingDemo.topFrame.statusPanel.progressBar.indeterminate = true
        new SwingWorker[Unit, Unit] {
          def doInBackground {
            try {
              InfinispanSwingDemo.topFrame.cachePanelList.head.cache.clear
            } catch {
              case ex: Exception => ex.printStackTrace
            }
          }
          override def done {
            InfinispanSwingDemo.topFrame.statusPanel.progressBar.indeterminate = false
          }
        }.execute
      })
    }
    contents += new Menu ("Debug") {
      contents += new MenuItem(Action("Dump All") {
        try {
          InfinispanSwingDemo.topFrame.cachePanelList.foreach(cachePanel => {
            println("==========")
            println("Cache " + cachePanel.id + " " +cachePanel.cache)
            cachePanel.cache.getAdvancedCache.getDataContainer.foreach(entry => {
              println("key=" + entry.getKey() +
                      ", value=" + entry.getValue() +
                      ", lifespan=" + entry.getLifespan() +
                      ", maxIdle=" + entry.getMaxIdle() +
                      ", isRemoved=" + entry.isRemoved() +
                      ", isEvicted=" + entry.isEvicted() +
                      ", isExpired=" + entry.isExpired() +
                      ", isValid=" + entry.isValid() +
                      ", class=" + entry.getClass()
                    )
            })
          })
        } catch {
          // todo
          case ex: Exception => ex.printStackTrace
        }
      })
      contents += new MenuItem(Action("System.gc()") {
        System.gc()
      })
    }
  }

  contents = new GroupPanel {
    autoCreateGaps = true
    autoCreateContainerGaps = true

    horizontalGroup {
      sequential(
        parallel()(topPanel, scrollPane, statusPanel)
      )
    }

    verticalGroup {
      sequential(
        parallel(Alignment.Baseline)(topPanel),
        parallel(Alignment.Baseline)(scrollPane),
        parallel(Alignment.Baseline)(statusPanel)
      )
    }
  }

  // testing, auto start
  startCache("infinispan-demo.xml")

  def startCache(cacheConfigFile: String) {
    val i = cacheCounter
    cacheCounter += 1
    val cachePanel = new CachePanel(cacheConfigFile, i)
    new SwingWorker[Unit, Unit] {
      Swing.onEDT {
        statusPanel.progressBar.indeterminate = true
      }
      def doInBackground {
        try {
          cachePanel.startCache
          cachePanelList += cachePanel
        } catch {
          case ex: Exception => ex.printStackTrace
        }
      }
      override def done {
        mainPanel.contents += cachePanel
        mainPanel.revalidate
        statusPanel.progressBar.indeterminate = false
      }
    }.execute
  }

  // scala.swing.Dialog.showInput doesn't accept Container, so define my own
  def showInput[A](message: Object, title: String): Option[A] = {
    val r = JOptionPane.showInputDialog(topFrame.peer,
                                        message,
                                        title,
                                        JOptionPane.QUESTION_MESSAGE,
                                        null,
                                        null,
                                        null)
    toOption(r)
  }

  def toOption[A](o: Object): Option[A] = if(o eq null) None else Some(o.asInstanceOf[A])
}
