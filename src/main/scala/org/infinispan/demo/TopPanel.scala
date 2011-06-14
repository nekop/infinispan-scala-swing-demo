package org.infinispan.demo

import java.io.File
import java.util.HashMap
import javax.swing.SwingWorker
import javax.swing.filechooser.FileNameExtensionFilter
import scala.swing.event._
import scala.swing.{SwingWorker => _, _}
import scala.util.Random


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
    case ButtonClicked(`randomGeneratorButton`) => {
      InfinispanSwingDemo.topFrame.statusPanel.progressBar.indeterminate = true
      new SwingWorker[Unit, Unit] {
        def doInBackground {
          try {
            val count = randomGeneratorText.text.toInt
            val map = new java.util.HashMap[String,String]()
            for (i <- 0 until count) {
              map.put(random.nextInt.toHexString.toUpperCase, random.nextInt.toHexString.toUpperCase)
            }
            InfinispanSwingDemo.topFrame.cachePanelList.head.cache.putAll(map)
          } catch {
            // todo
            case e: Exception => e.printStackTrace
          }
        }
        override def done {
          InfinispanSwingDemo.topFrame.statusPanel.progressBar.indeterminate = false
        }
      }.execute
    }
    case ButtonClicked(`clearButton`) => {
      InfinispanSwingDemo.topFrame.statusPanel.progressBar.indeterminate = true
      new SwingWorker[Unit, Unit] {
        def doInBackground {
          try {
            InfinispanSwingDemo.topFrame.cachePanelList.head.cache.clear
          } catch {
            // todo
            case e: Exception => e.printStackTrace
          }
        }
        override def done {
          InfinispanSwingDemo.topFrame.statusPanel.progressBar.indeterminate = false
        }
      }.execute
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

