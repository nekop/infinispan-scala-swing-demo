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
import javax.swing.GroupLayout
import javax.swing.SwingConstants

/** Several methods that delegate directly to the underlying `GroupLayout`.
  *  
  * @author Andreas Flierl
  */
trait Delegations {
  val layout: GroupLayout
  
  /**
   * The component will not take up any space when it's invisible (default).
   */
  def honorVisibilityOf(comp: Component) =
    layout.setHonorsVisibility(comp.peer, true)
    
  /**
   * The component will still take up its space even when invisible.
   */
  def ignoreVisibilityOf(comp: Component) =
    layout.setHonorsVisibility(comp.peer, false)
  
  /**
   * Links the sizes (horizontal and vertical) of several components.
   * 
   * @param comps the components to link
   */
  def linkSize(comps: Component*) = layout.linkSize(comps.map(_.peer): _*)
  
  /**
   * Links the sizes of several components horizontally.
   * 
   * @param comps the components to link
   */
  def linkHorizontalSize(comps: Component*) =
    layout.linkSize(SwingConstants.HORIZONTAL, comps.map(_.peer): _*)
    
  /**
   * Links the sizes of several components vertically.
   * 
   * @param comps the components to link
   */
  def linkVerticalSize(comps: Component*) =
    layout.linkSize(SwingConstants.VERTICAL, comps.map(_.peer): _*)
  
  /**
   * Replaces one component with another. Great for dynamic layouts.
   * 
   * @param existing the component to be replaced
   * @param replacement the component replacing the existing one
   */
  def replace(existing: Component, replacement: Component) =
    layout.replace(existing.peer, replacement.peer)
}