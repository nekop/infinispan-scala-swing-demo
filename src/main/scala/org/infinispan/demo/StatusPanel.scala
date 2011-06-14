package org.infinispan.demo

import java.awt.Dimension
import scala.swing.{SwingWorker => _, _}


class StatusPanel extends GroupPanel {
  val cacheCountLabel = new Label("Cache Count: 0")
  val entryCountLabel = new Label("Entry Count: 0")
  val progressBar = new ProgressBar {
    maximumSize = new Dimension(120, 20)
  }
  javax.swing.UIManager.getLookAndFeelDefaults.put("ProgressBar.cycleTime", 1500) // make it faster, 3000 by default 

  autoCreateGaps = true
  autoCreateContainerGaps = false

  horizontalGroup {
    sequential(
      parallel()(cacheCountLabel),
      parallel()(Gap(SizeRange(0,32,32))),
      parallel()(entryCountLabel),
      parallel()(Gap(SizeRange(0,0,Int.MaxValue))),
      parallel(Alignment.Trailing)(progressBar)
    )
  }

  verticalGroup {
    sequential(
      parallel(Alignment.Baseline)(cacheCountLabel, entryCountLabel, progressBar)
    )
  }

  def updateEntryCountLabel(i: Int) {
    entryCountLabel.text = "Entry Count: " + i
    entryCountLabel.repaint
  }

  def updateCacheCountLabel(i: Int) {
    cacheCountLabel.text = "Cache Count: " + i
    cacheCountLabel.repaint
  }
}

