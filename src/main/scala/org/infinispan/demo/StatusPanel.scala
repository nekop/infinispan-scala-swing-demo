package org.infinispan.demo

import java.awt.Dimension
import scala.swing.{SwingWorker => _, _}
import eu.flierl.grouppanel.GroupPanel


class StatusPanel extends GroupPanel {
  val cacheCountLabel = new Label("Cache Count: 0")
  val entryCountLabel = new Label("Entry Count: 0")
  val progressBar = new ProgressBar {
    maximumSize = new Dimension(120, 20)
  }
  javax.swing.UIManager.put("ProgressBar.cycleTime", 1500) // make it faster, 3000 by default 

  autoCreateContainerGaps = false

  theHorizontalLayout is Sequential(
    Parallel(cacheCountLabel),
    Parallel(Gap(0,32,32)),
    Parallel(entryCountLabel),
    Parallel(Gap(0,0,Int.MaxValue)),
    Parallel(Trailing)(progressBar)
  )

  theVerticalLayout is Sequential(
    Parallel(Baseline)(cacheCountLabel, entryCountLabel, progressBar)
  )

  def updateEntryCountLabel(i: Int) {
    entryCountLabel.text = "Entry Count: " + i
    entryCountLabel.repaint
  }

  def updateCacheCountLabel(i: Int) {
    cacheCountLabel.text = "Cache Count: " + i
    cacheCountLabel.repaint
  }
}

