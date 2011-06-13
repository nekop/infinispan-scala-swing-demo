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

class CachePanel(val cache: Cache[String, String], val id: Int) extends GroupPanel {

  val listener = new CacheListener
  cache.addListener(listener)
  cache.getCacheManager.addListener(listener)
  cache.start

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
            cache.stop
          }
          stopButton.text = "Start"
        }
        case TERMINATED => {
          actor {
            cache.start
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

  def update {
    Swing.onEDT {
      if (cache.getStatus == RUNNING) {
        cacheBar.value = cache.size
      } else {
        cacheBar.value = 0
      }
      cacheBar.repaint
    }
  }

  @Listener
  class CacheListener {
    @CacheStarted
    def cacheStarted(e: CacheStartedEvent) {
      InfinispanSwingDemo.topFrame.refreshDelay
    }
    
    @ViewChanged
    @Merged
    def viewChangeEvent(e: ViewChangedEvent) {
      InfinispanSwingDemo.topFrame.refreshDelay
    }

    @CacheEntryCreated
    @CacheEntryModified
    @CacheEntryRemoved
    @CacheEntryEvicted
    def change(e: Event[String,String]) {
      if (!e.isPre) update
    }

    @DataRehashed
    def rehash(e: Event[String,String]) {
      if (!e.isPre) InfinispanSwingDemo.topFrame.refreshDelay
    }
  }
}
