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

class TopPanel extends GroupPanel {
  lazy val random = new Random
  val configFileLabel = new Label("Infinispan config file")
  val configFileText = new TextField("infinispan-demo.xml", 30)
  val configFileButton = new Button("Open...")
  var currentFile = new File("infinispan-demo.xml")
  val configFileChooser = new FileChooser(currentFile) {
    title = "Open Infinispan config file"
    fileFilter = new FileNameExtensionFilter("Infinispan config file", "xml")
  }
  val startButton = new Button("Start Cache")

  val randomGeneratorLabel = new Label("Generate random entries")
  val randomGeneratorText = new TextField("50", 6)
  val randomGeneratorButton = new Button("Generate")
  val clearButton = new Button("Clear all caches")

  listenTo(configFileButton, startButton, randomGeneratorButton, clearButton)
  reactions += {
    case ButtonClicked(`configFileButton`) =>
      configFileChooser.showOpenDialog(this) match {
        case FileChooser.Result.Approve => {
          currentFile = configFileChooser.selectedFile
          configFileText.text = currentFile.getPath
        }
        case FileChooser.Result.Cancel => ;
        case FileChooser.Result.Error => ;
      }
    case ButtonClicked(`startButton`) =>
      try {
        InfinispanSwingDemo.topFrame.startCache(configFileText.text)
      } catch {
        case e: Exception => e.printStackTrace()
      }
    case ButtonClicked(`randomGeneratorButton`) =>
      // todo actor
      try {
        val count = randomGeneratorText.text.toInt
        val map = new HashMap[String,String]() // we need java map here
        for (i <- 0 until count) {
          map.put(random.nextInt.toHexString.toUpperCase, random.nextInt.toHexString.toUpperCase)
        }
        InfinispanSwingDemo.topFrame.cachePanelList.head.cache.putAll(map)
      } catch {
        // todo
        case e: Exception => e.printStackTrace
      }
    case ButtonClicked(`clearButton`) =>
      // todo actor
      try {
        InfinispanSwingDemo.topFrame.cachePanelList.head.cache.clear
      } catch {
        // todo
        case e: Exception => e.printStackTrace
      }
  }
  autoCreateGaps = true
  autoCreateContainerGaps = true

  horizontalGroup {
    sequential(
      parallel()(configFileLabel, randomGeneratorLabel),
      parallel()(configFileText, sequential(
        parallel()(randomGeneratorText),
        parallel()(randomGeneratorButton)
      )),
      parallel()(configFileButton),
      parallel()(startButton, clearButton)
    )
  }

  verticalGroup {
    sequential(
      parallel(Alignment.Baseline)(configFileLabel, configFileText, configFileButton, startButton),
      parallel(Alignment.Baseline)(randomGeneratorLabel, randomGeneratorText, randomGeneratorButton, clearButton)
    )
  }
  
}

