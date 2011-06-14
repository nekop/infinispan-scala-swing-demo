package org.infinispan.demo

import swing.SimpleSwingApplication
import swing._
import swing.event._
import javax.swing.filechooser.FileNameExtensionFilter
import java.io.File
import java.io.InputStream
import java.awt.Dimension
import java.net.URL

import actors.Actor._
import org.infinispan.Cache
import org.infinispan.manager.DefaultCacheManager
import org.infinispan.manager.EmbeddedCacheManager
import org.infinispan.notifications.Listener
import org.infinispan.notifications.cachelistener.annotation._
import org.infinispan.notifications.cachelistener.event.Event
import org.infinispan.notifications.cachemanagerlistener.annotation._
import org.infinispan.notifications.cachemanagerlistener.event._
import org.infinispan.lifecycle.ComponentStatus._

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

  val stopButton = new Button("Stop")

  listenTo(stopButton)
  reactions += {
    case ButtonClicked(`stopButton`) => {
      cache.getStatus match {
        case RUNNING => {
          actor {
            stopCache
          }
          stopButton.text = "Start"
        }
        case TERMINATED => {
          actor {
            startCache
          }
          stopButton.text = "Stop"
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
      parallel()(stopButton)
    )
  }

  verticalGroup {
    sequential(
      parallel(Alignment.Baseline)(cacheLabel, cacheBar, stopButton)
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
