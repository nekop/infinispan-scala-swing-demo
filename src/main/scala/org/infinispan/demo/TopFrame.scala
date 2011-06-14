package org.infinispan.demo

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
    contents += new Menu ("Cache Operation") {
      contents += new MenuItem(Action("Clear all") {
        InfinispanSwingDemo.topFrame.statusPanel.progressBar.indeterminate = true
        new SwingWorker[Unit, Unit] {
          def doInBackground {
            try {
              InfinispanSwingDemo.topFrame.cachePanelList.head.cache.clear
            } catch {
              // todo
              case e: Exception => e.printStackTrace
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
          case e: Exception => e.printStackTrace
        }
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
        // todo deal with exceptions, no xml provided
        cachePanel.startCache
        cachePanelList += cachePanel
      }
      override def done {
        mainPanel.contents += cachePanel
        mainPanel.revalidate
        statusPanel.progressBar.indeterminate = false
      }
    }.execute
  }
}
