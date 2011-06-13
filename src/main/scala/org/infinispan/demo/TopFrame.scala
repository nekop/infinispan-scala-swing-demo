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
  
  title = "Infinispan Swing Demo"
  menuBar = new MenuBar {
    contents += new Menu ("File") {
      contents += new MenuItem(Action("Exit") {
        app.quit
      })
    }
    contents += new Menu ("Debug") {
      contents += new MenuItem(Action("Refresh") {
        refresh
      })
      contents += new MenuItem(Action("processEviction") {
        try {
          InfinispanSwingDemo.topFrame.cachePanelList.foreach(cachePanel => {
            cachePanel.cache.getAdvancedCache.getEvictionManager.processEviction
            println(cachePanel.cache.getAdvancedCache.getEvictionManager)
            println(cachePanel.cache.getAdvancedCache.getEvictionManager.isEnabled)
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
        parallel()(topPanel, scrollPane)
      )
    }

    verticalGroup {
      sequential(
        parallel(Alignment.Baseline)(topPanel),
        parallel(Alignment.Baseline)(scrollPane)
      )
    }
  }

  // testing, auto start
  startCache("infinispan-demo.xml")
  // Thread.sleep(200)
  // startCache("infinispan-demo.xml")
  // Thread.sleep(200)
  // startCache("infinispan-demo.xml")
  // Thread.sleep(200)
  // startCache("infinispan-demo.xml")

  def startCache(cacheConfigFile: String) {
    val i = cacheCounter
    cacheCounter += 1
    // todo use SwingWorker, show progress spinner or something
    actor {
      // todo deal with exceptions, no xml provided
      var resource = getClass.getClassLoader.getResource(cacheConfigFile)
      if (resource == null) {
        val f = new File(cacheConfigFile)
        if (f.exists) resource = f.toURI.toURL
      }
      if (resource == null) resource = new URL(cacheConfigFile)

      var cacheManager: DefaultCacheManager = null
      if (cacheManager == null) {
        val stream = resource.openStream()
        try {
          cacheManager = new DefaultCacheManager(stream)
        } finally {
          close(stream)
        }
      } 
      var cache: Cache[String,String] = cacheManager.getCache()
      val cachePanel = new CachePanel(cache, i)
      cachePanelList += cachePanel
      Swing.onEDT {
        mainPanel.contents += cachePanel
        mainPanel.revalidate
        // todo this should not be here...
        InfinispanSwingDemo.topFrame.cachePanelList.foreach(cachePanel => {
          cachePanel.update
        })
        println("Added cachePanel")
      }
    }

    println("done")
  }

  def close(in: InputStream) {
    try {
      if (in != null) {
        in.close()
      }
    } catch {
      case _ => ; // ignore
    }
  }

  def refresh {
    InfinispanSwingDemo.topFrame.cachePanelList.foreach(cachePanel => {
      cachePanel.update
    })
  }

  def refreshDelay {
    actor {
      0 until 4 foreach (i => {
        Thread.sleep(500)
        refresh
      })
    }
  }
}
