/*
 * Copyright (c) 2009-2011 Andreas Flierl
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package eu.flierl.grouppanel

import swing._
import javax.swing.{ GroupLayout, JPanel }
import java.awt.{ Component => AWTComp }
import scala.annotation.tailrec

/** Allows to assign a baseline reference, i.e. a component that provides the
  * baseline of the panel instead of the panel itself (which has no meaningful
  * baseline by default).
  * 
  * @author Andreas Flierl
  */
trait BaselineCustomization { this: Panel =>
  private[this] var customBaseline: Option[Component] = None

  object theBaselineReference {
    def is(c: Component): Unit = {
      if (! peer.isAncestorOf(c.peer)) throw new IllegalArgumentException(
        "component to be used as baseline reference must be a descendant of this GroupPanel")
      customBaseline = Some(c)
    }
  }
  
  def theDefaultBaselineIsUsed(): Unit = customBaseline = None
  
  /** The swing `JPanel` wrapped by this `Panel`. */
  override lazy val peer: JPanel = new JPanel with SuperMixin {
    override def getBaseline(w: Int, h: Int): Int = {
      customBaseline.map { c =>
        setSize(w, h)
        validate
        sumOfOffsets(c.peer, c.peer.getBaseline(c.peer.getWidth, c.peer.getHeight))
      }.getOrElse(super.getBaseline(w, h))
    }
    
    @tailrec
    private[this] def sumOfOffsets(c: AWTComp, y: Int): Int = {
      if (c == this || ! c.getParent.isInstanceOf[AWTComp]) y
      else sumOfOffsets(c.getParent.asInstanceOf[AWTComp], y + c.getY)
    }
  }
}