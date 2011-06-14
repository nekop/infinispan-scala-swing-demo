package org.infinispan.demo

import scala.swing.SimpleSwingApplication


object InfinispanSwingDemo extends SimpleSwingApplication {

  var topFrame: TopFrame = null;

  def top = {
    topFrame = new TopFrame(this)
    topFrame
  }

  override def shutdown {
    InfinispanSwingDemo.topFrame.cachePanelList.foreach(cachePanel => {
      cachePanel.stopCache
    })
  }

}
