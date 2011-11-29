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
import javax.swing.LayoutStyle
import javax.swing.LayoutStyle.ComponentPlacement
import javax.swing.SwingConstants

/** Provides several declarative means for manual spacing of components in 
  * a `GroupPanel`.
  * 
  * @author Andreas Flierl
  */
trait Gaps extends SizeTypes { this: GroupPanel =>
  /**
   * A manual gap between two components.
   */
  protected object Gap {
    /** 
     * Creates a rigid (non-resizable) gap with the specified size.
     *
     * @param size the size >= 0 of the gap
     *
     * @see GroupLayout.Group#addGap(int)
     */
    def apply[A <: G](size: GapSize) = new InGroup[A] {
      override private[grouppanel] def build(parent: A) = parent.addGap(size.pixels)
    }
    
    /** 
     * Creates a resizable gap with the specified size constraints.
     * 
     * @param min minimum size >= 0
     * @param pref preferred size >= 0
     * @param max maximum size >= 0
     * @see GroupLayout.Group#addGap(int, int, int)
     */
    def apply[A <: G](min: GapSize, pref: GapSize, max: GapSize) = new InGroup[A] {
      override private[grouppanel] def build(parent: A) =
        parent.addGap(min.pixels, pref.pixels, max.pixels)
    }
  }
  
  /** 
   * A gap between two components that acts like a ''spring'', pushing the two
   * elements away from each other.
   */
  protected object Spring {      
    /** 
     * Creates a preferred gap that acts like a ''spring'' with the specified 
     * size constraints between the two elements that surround this gap. 
     * The minimum size is the preferred size of the gap which is defined by 
     * the `LayoutStyle` used.
     * 
     * @param relationship the relationship between the two components;
     *        the default for this parameter is `Related`
     * @param preferred the preferred size >= 0 of the gap (or one of `UseDefault` 
     *        or `Infinite`); the default for this parameter is `UseDefault`
     * @see GroupLayout.Group#addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement, int, int)
     */
    def apply(relationship: RelatedOrUnrelated = Related, preferred: PreferredGapSize = UseDefault) =
      PreferredGap(relationship, preferred, Infinite)
  }
  
  /**
   * A manual gap between a component and a container (or group) edge. 
   * Only valid inside a sequential group.
   */
  protected object ContainerGap {
    /**
     * Creates a gap between a component and a container edge, using the 
     * preferrerd size as specified in the `layoutStyle`.
     * 
     * @see GroupLayout.Group#addContainerGap()
     */
    def apply() = new InSequential {
      override private[grouppanel] def build(parent: GroupLayout#SequentialGroup) = 
        parent.addContainerGap()
    }
    
    /** 
     * Creates a new gap with the specified size constraints between a 
     * component and a container edge.
     * 
     * @param pref the preferred size >= 0 of the gap (or one of `UseDefault` and `Infinite`)
     * @param max the maximum size >= 0 of the gap (or one of `UseDefault`, `UsePreferred` and `Infinite`)
     * @see GroupLayout.Group#addContainerGap(int, int)
     */
    def apply(pref: PreferredGapSize, max: Size) = new InSequential {
        override private[grouppanel] def build(parent: GroupLayout#SequentialGroup) = 
          parent.addContainerGap(pref.pixels, max.pixels)
      }
  }
  
  /**
   * A gap between a component and a container (or group) edge that acts like 
   * a ''spring'', pushing the element away from the edge.
   */
  protected object ContainerSpring {
    /** 
     * Creates a resizable gap that acts like a ''spring''.
     * 
     * @param pref preferred size >= 0 (or one of `UseDefault` and `Infinite`)
     *        the default for this parameter is `UseDefault`
     * @see GroupLayout.Group#addGap(int, int, int)
     */
    def apply[A <: G](pref: PreferredGapSize = UseDefault) = ContainerGap(pref, Infinite)
  }
  
  /**
   *  A gap between two components with a size that is defined by the 
   *  `LayoutStyle` used, depending on the relationship between
   *  the components.
   */
  protected object PreferredGap {
    /** 
     * Creates a preferred gap between the two elements that surround this
     * gap.
     * 
     * @param relationship the relationship between the two components
     * @see GroupLayout.Group#addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement)
     */
    def apply(relationship: RelatedOrUnrelated) = new InSequential {
      override private[grouppanel] def build(parent: GroupLayout#SequentialGroup) =
        parent.addPreferredGap(relationship.wrapped)
    }
    
    /** 
     * Creates a preferred gap with the specified size constraints between the 
     * two elements that surround this gap. The minimum size is the preferred 
     * size of the gap which is defined by the `LayoutStyle` used.
     * 
     * @param relationship the relationship between the two components
     * @param preferred the preferred size >= 0 of the gap (or one of `UseDefault` and `Infinite`)
     * @param the maximum size >= 0 of the gap (or one of `UseDefault`, `UsePreferred` 
     *        and `Infinite`)
     * @see GroupLayout.Group#addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement, int, int)
     */
    def apply(relationship: RelatedOrUnrelated, preferred: PreferredGapSize, max: Size) = 
        new InSequential {
      override private[grouppanel] def build(parent: GroupLayout#SequentialGroup) =
        parent.addPreferredGap(relationship.wrapped, preferred.pixels, max.pixels)
    }
    
    /** 
     * Creates a rigid gap between the two specified elements. The minimum 
     * size is the preferred size of the gap which is defined by the 
     * `LayoutStyle` used.
     * 
     * @param comp1 the element "in front of" the gap
     * @param comp2 the element "behind" the gap
     * @param relationship the relationship between the two components
     * @see GroupLayout.Group#addPreferredGap(javax.swing.JComponent, javax.swing.JComponent, javax.swing.LayoutStyle.ComponentPlacement)
     */
    def apply(comp1: Component, comp2: Component, relationship: Placement) = 
        new InSequential {
      override private[grouppanel] def build(parent: GroupLayout#SequentialGroup) =
        parent.addPreferredGap(comp1.peer, comp2.peer, relationship.wrapped)
    }
    
    /** 
     * Creates a preferred gap with the specified size constraints between the 
     * two specified elements. The minimum size is the preferred size of the gap
     * which is defined by the `LayoutStyle` used.
     * 
     * @param comp1 the element "in front of" the gap
     * @param comp2 the element "behind" the gap
     * @param relationship the relationship between the two components
     * @param preferred the preferred size >= 0 of the gap (or one of `UseDefault` 
     *        and `Infinite`)
     * @param the maximum size >= 0 of the gap (or one of `UseDefault` and `Infinite`)
     * @see GroupLayout.Group#addPreferredGap(javax.swing.JComponent, javax.swing.JComponent, javax.swing.LayoutStyle.ComponentPlacement, int, int)
     */
    def apply(comp1: Component, comp2: Component, relationship: Placement,
        preferred: PreferredGapSize, max: PreferredGapSize) = new InSequential {
      override private[grouppanel] def build(parent: GroupLayout#SequentialGroup) =
        parent.addPreferredGap(comp1.peer, comp2.peer, relationship.wrapped,
                               preferred.pixels, max.pixels)
    }
  }
}