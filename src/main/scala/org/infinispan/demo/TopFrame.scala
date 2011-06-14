package org.infinispan.demo

import scala.collection.JavaConversions._
import actors.Actor._
import swing.SimpleSwingApplication
import swing._
import swing.event._
import collection.mutable._
import javax.swing.filechooser.FileNameExtensionFilter
import java.io.File
import java.io.InputStream
import java.awt.Dimension
import java.net.URL

import org.infinispan.Cache
import org.infinispan.manager.DefaultCacheManager
import org.infinispan.manager.EmbeddedCacheManager
import org.infinispan.notifications.Listener
import org.infinispan.notifications.cachelistener.annotation._
import org.infinispan.notifications.cachelistener.event.Event
import org.infinispan.notifications.cachemanagerlistener.annotation._
import org.infinispan.notifications.cachemanagerlistener.event._

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
    contents += new Menu ("Debug") {
      contents += new MenuItem(Action("Refresh") {
        RefreshThread.refresh
      })
      contents += new MenuItem(Action("processEviction") {
        try {
          InfinispanSwingDemo.topFrame.cachePanelList.foreach(cachePanel => {
            cachePanel.cache.getAdvancedCache.getEvictionManager.processEviction
          })
        } catch {
          // todo
          case e: Exception => e.printStackTrace
        }
      })
      contents += new MenuItem(Action("Dump") {
        try {
          InfinispanSwingDemo.topFrame.cachePanelList.foreach(cachePanel => {
            println("Cache " + cachePanel.id + " " + cachePanel.cache + ", size=" + cachePanel.cache.size)
          })
        } catch {
          // todo
          case e: Exception => e.printStackTrace
        }
      })
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
      contents += new MenuItem(Action("Clear All") {
        try {
          InfinispanSwingDemo.topFrame.cachePanelList.foreach(cachePanel => {
            cachePanel.cache.clear
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
    // todo use SwingWorker, show progress spinner or something
    actor {
      // todo deal with exceptions, no xml provided
      val cachePanel = new CachePanel(cacheConfigFile, i)
      cachePanel.startCache
      cachePanelList += cachePanel
      Swing.onEDT {
        mainPanel.contents += cachePanel
        mainPanel.revalidate
      }
    }
  }

}
