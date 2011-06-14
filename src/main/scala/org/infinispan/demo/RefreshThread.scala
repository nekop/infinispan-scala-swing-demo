package org.infinispan.demo

import swing.SimpleSwingApplication
import swing._
import swing.event._
import javax.swing.filechooser.FileNameExtensionFilter
import java.io.File
import java.io.InputStream
import java.awt.Dimension
import java.net.URL

import actors.Actor
import org.infinispan.Cache
import org.infinispan.manager.DefaultCacheManager
import org.infinispan.manager.EmbeddedCacheManager
import org.infinispan.notifications.Listener
import org.infinispan.notifications.cachelistener.annotation._
import org.infinispan.notifications.cachelistener.event.Event
import org.infinispan.notifications.cachemanagerlistener.annotation._
import org.infinispan.notifications.cachemanagerlistener.event._
import org.infinispan.lifecycle.ComponentStatus._

object RefreshThread extends Thread {
  // todo, super(name)

  val interval = 500
  var needRefresh = false

  override def run {
    while (true) {
      if (needRefresh) {
        try {
          performRefresh
        } catch {
          case ex: Exception => ex.printStackTrace
        }
      } else {
        synchronized {
          needRefresh = true
          wait(interval)
        }
      }
    }
  }

  def refresh {
    synchronized {
      needRefresh = true
      notify
    }
  }


  def performRefresh {
    synchronized {
      needRefresh = false
    }
    Swing.onEDT {
      var entryCount = 0
      InfinispanSwingDemo.topFrame.cachePanelList.foreach(cachePanel => {
        if (cachePanel.cache.getStatus == RUNNING) {
          cachePanel.cacheBar.value = cachePanel.cache.size
          entryCount += cachePanel.cache.size
        } else {
          cachePanel.cacheBar.value = 0
        }
        cachePanel.cacheBar.repaint
      })
      InfinispanSwingDemo.topFrame.statusPanel.updateEntryCountLabel(entryCount)
    }
  }
}
