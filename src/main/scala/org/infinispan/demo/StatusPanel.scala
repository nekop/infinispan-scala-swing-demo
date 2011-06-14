package org.infinispan.demo

import swing.SimpleSwingApplication
import swing._
import swing.event._
import util.Random

import javax.swing.filechooser.FileNameExtensionFilter
import java.io.File
import java.io.InputStream
import java.awt.Dimension
import java.net.URL
import java.util.HashMap

import org.infinispan.Cache
import org.infinispan.manager.DefaultCacheManager
import org.infinispan.manager.EmbeddedCacheManager
import org.infinispan.notifications.Listener
import org.infinispan.notifications.cachelistener.annotation._
import org.infinispan.notifications.cachelistener.event.Event
import org.infinispan.notifications.cachemanagerlistener.annotation._
import org.infinispan.notifications.cachemanagerlistener.event._

class StatusPanel extends GroupPanel {
  //val cacheCountLabel = new Label("Cache Count: 0")
  val entryCountLabel = new Label("Entry Count: 0")
  val progressBar = new ProgressBar

  autoCreateGaps = true
  autoCreateContainerGaps = false

  horizontalGroup {
    sequential(
      //parallel()(cacheCountLabel),
      parallel()(entryCountLabel),
      parallel()(progressBar)
    )
  }

  verticalGroup {
    sequential(
      //parallel(Alignment.Baseline)(cacheCountLabel, entryCountLabel, progressBar)
      parallel(Alignment.Baseline)(entryCountLabel, progressBar)
    )
  }

  def updateEntryCountLabel(i: Int) {
    entryCountLabel.text = "Entry Count: " + i
    entryCountLabel.repaint
  }
}

