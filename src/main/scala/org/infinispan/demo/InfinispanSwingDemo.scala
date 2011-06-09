package org.infinispan.demo

import swing.SimpleSwingApplication
import swing._
import swing.event._
import javax.swing.filechooser.FileNameExtensionFilter
import java.io.File
import java.io.InputStream
import java.net.URL

import org.infinispan.Cache
import org.infinispan.manager.DefaultCacheManager
import org.infinispan.manager.EmbeddedCacheManager
import org.infinispan.notifications.Listener
import org.infinispan.notifications.cachelistener.annotation._
import org.infinispan.notifications.cachelistener.event.Event
import org.infinispan.notifications.cachemanagerlistener.annotation._
import org.infinispan.notifications.cachemanagerlistener.event._

object InfinispanSwingDemo extends SimpleSwingApplication {

  var topFrame: TopFrame = null;

  def top = {
    topFrame = new TopFrame(this)
    topFrame
  }

}
