package org.infinispan.demo

import java.io.File
import java.net.URL
import javax.swing.SwingWorker
import org.infinispan.Cache
import org.infinispan.lifecycle.ComponentStatus._
import org.infinispan.manager.DefaultCacheManager
import org.infinispan.notifications.Listener
import org.infinispan.notifications.cachelistener.annotation._
import org.infinispan.notifications.cachelistener.event.Event
import org.infinispan.notifications.cachemanagerlistener.annotation._
import org.infinispan.notifications.cachemanagerlistener.event._
import scala.swing.event.{Event => _, _}
import scala.swing.{SwingWorker => _, _}


class CachePanel(val cacheConfigFile: String, val id: Int) extends GroupPanel {

  var cacheManager: DefaultCacheManager = null;
  var cache: Cache[String, String] = null;

  val cacheLabel = new Label("Cache " + id)
  val cacheBar = new ProgressBar {
    def string_=(s: String): Unit = peer.setString(s)
    def string: String = peer.getString
    override def value_=(i: Int): Unit = {
      super.value = i
      string = i.toString
    }
    min = 0
    max = 1000
    labelPainted = true
    value = 0
  }
  val showButton = new Button("Show entries")
  val stopButton = new Button("Stop")

  listenTo(showButton, stopButton)
  reactions += {
    case ButtonClicked(`showButton`) => {
      val dataDialog = new DataDialog(InfinispanSwingDemo.topFrame, cache)
      dataDialog.open
    }
    case ButtonClicked(`stopButton`) => {
      cache.getStatus match {
        case RUNNING => {
          new SwingWorker[Unit, Unit] {
            InfinispanSwingDemo.topFrame.statusPanel.progressBar.indeterminate = true
            def doInBackground {
              try {
                stopCache
              } catch {
                case ex: Exception => ex.printStackTrace
              }
            }
            override def done {
              stopButton.text = "Start"
              InfinispanSwingDemo.topFrame.statusPanel.progressBar.indeterminate = false
            }
          }.execute
        }
        case TERMINATED => {
          new SwingWorker[Unit, Unit] {
            InfinispanSwingDemo.topFrame.statusPanel.progressBar.indeterminate = true
            def doInBackground {
              try {
                startCache
              } catch {
                case ex: Exception => ex.printStackTrace
              }
            }
            override def done {
              stopButton.text = "Stop"
              InfinispanSwingDemo.topFrame.statusPanel.progressBar.indeterminate = false
            }
          }.execute
        }
        case _ => ;
      }
    }
  }


  autoCreateGaps = true
  autoCreateContainerGaps = true

  horizontalGroup {
    sequential(
      parallel()(cacheLabel),
      parallel()(cacheBar),
      parallel()(showButton),
      parallel()(stopButton)
    )
  }

  verticalGroup {
    sequential(
      parallel(Alignment.Baseline)(cacheLabel, cacheBar, showButton, stopButton)
    )
  }

  @Listener
  class CacheListener {
    @CacheStarted
    def cacheStarted(e: CacheStartedEvent) {
      RefreshThread.refresh
    }
    
    @ViewChanged
    @Merged
    def viewChangeEvent(e: ViewChangedEvent) {
      RefreshThread.refresh
    }

    @CacheEntryCreated
    @CacheEntryModified
    @CacheEntryRemoved
    @CacheEntryEvicted
    def change(e: Event[String,String]) {
      if (!e.isPre) RefreshThread.refresh
    }

    @DataRehashed
    def rehash(e: Event[String,String]) {
      if (!e.isPre) RefreshThread.refresh
    }
  }

  def createCacheManager = {
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
        try {
          if (stream != null) {
            stream.close()
          }
        } catch {
          case _ => ; // ignore
        }
      }
    }
    cacheManager
  }

  def startCache {
    cacheManager = createCacheManager
    cache = cacheManager.getCache()
    val listener = new CacheListener
    cache.addListener(listener)
    cache.getCacheManager.addListener(listener)
    cache.start
    
  }
  def stopCache {
    cache.stop
    cache.getCacheManager.stop
  }

}
