package org.infinispan.demo

import scala.swing.{SwingWorker => _, _}


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

