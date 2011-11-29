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

import javax.swing.LayoutStyle.ComponentPlacement

/** Provides placement constants for a `GroupPanel`.
  * 
  * @author Andreas Flierl
  */
trait Placements {
  /**
   * Specifies how two components are placed relative to each other.
   * 
   * @see javax.swing.LayoutStyle.ComponentPlacement
   */
  protected[Placements] sealed class Placement(
      private[grouppanel] val wrapped: ComponentPlacement)
  
  /**
   * Specifies if two components are related or not.
   * 
   * @see javax.swing.LayoutStyle.ComponentPlacement
   */    
  protected[Placements] sealed class RelatedOrUnrelated(
      cp: ComponentPlacement) extends Placement(cp)
  
  /** Used to request the distance between two visually related components. */
  object Related extends RelatedOrUnrelated(ComponentPlacement.RELATED)
  
  /** Used to request the distance between two visually unrelated components. */
  object Unrelated extends RelatedOrUnrelated(ComponentPlacement.UNRELATED)
  
  /**
   * Used to request the (horizontal) indentation of a component that is 
   * positioned underneath another component.
   */
  object Indent extends Placement(ComponentPlacement.INDENT)
}
