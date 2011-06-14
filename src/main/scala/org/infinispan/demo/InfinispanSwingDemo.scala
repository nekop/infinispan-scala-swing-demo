package org.infinispan.demo

import scala.swing.SimpleSwingApplication


object InfinispanSwingDemo extends SimpleSwingApplication {

  var topFrame: TopFrame = null;

  def top = {
    topFrame = new TopFrame(this)
    topFrame
  }

}
