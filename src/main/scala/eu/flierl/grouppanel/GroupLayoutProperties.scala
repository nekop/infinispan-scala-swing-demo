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

import javax.swing.GroupLayout

/** Property wrappers for `GroupLayout`'s setters and getters.
  * 
  * @author Andreas Flierl
  */
trait GroupLayoutProperties {
  def layout: GroupLayout
  
  /** Indicates whether gaps between components are automatically created. */
  def autoCreateGaps = layout.getAutoCreateGaps
  
  /** Sets whether gaps between components are automatically created. */
  def autoCreateGaps_=(flag: Boolean) = layout.setAutoCreateGaps(flag)
  
  /** 
   * Indicates whether gaps between components and the container borders are 
   * automatically created. 
   */
  def autoCreateContainerGaps = layout.getAutoCreateContainerGaps
  
  /** 
   * Sets whether gaps between components and the container borders are 
   * automatically created. 
   */
  def autoCreateContainerGaps_=(flag: Boolean) = 
    layout.setAutoCreateContainerGaps(flag)
  
  /** Returns the layout style used. */
  def layoutStyle = layout.getLayoutStyle
  
  /** Assigns a layout style to use. */
  def layoutStyle_=(style: javax.swing.LayoutStyle) = layout.setLayoutStyle(style)
  
  /** 
   * Indicates whether the visibilty of components is considered for the layout.
   * If set to `false`, invisible components still take up space.
   * Defaults to `true`.
   */
  def honorsVisibility = layout.getHonorsVisibility
  
  /**
   * Sets whether the visibilty of components should be considered for the 
   * layout. If set to `false`, invisible components still take up 
   * space. Defaults to `true`. 
   */
  def honorsVisibility_=(flag: Boolean) =
    layout.setHonorsVisibility(flag)
}