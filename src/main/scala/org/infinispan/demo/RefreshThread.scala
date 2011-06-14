package org.infinispan.demo

import scala.swing.{SwingWorker => _, _}
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
          case ex: Exception => ex.printStackTrace // todo
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
      // Disable dynamic refresh, it's excessive and makes GUI slow
      //notify
    }
  }


  def performRefresh {
    synchronized {
      needRefresh = false
    }
    Swing.onEDT {
      var entryCount = 0
      var cacheCount = 0
      InfinispanSwingDemo.topFrame.cachePanelList.foreach(cachePanel => {
        if (cachePanel.cache.getStatus == RUNNING) {
          cachePanel.cacheBar.value = cachePanel.cache.size
          cacheCount += 1
          entryCount += cachePanel.cache.size
        } else {
          cachePanel.cacheBar.value = 0
        }
        cachePanel.cacheBar.repaint
      })
      InfinispanSwingDemo.topFrame.statusPanel.updateEntryCountLabel(entryCount)
      InfinispanSwingDemo.topFrame.statusPanel.updateCacheCountLabel(cacheCount)
    }
  }
}
