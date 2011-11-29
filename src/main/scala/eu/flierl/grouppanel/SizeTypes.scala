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

/** Provides types and constants to ensure the correct use of pixel sizes and 
  * size hints in a `GroupLayout`. Integer values will be converted to a size 
  * type via an implicit conversion. These pixel sizes must always be >= 0. 
  * 
  * @author Andreas Flierl
  */
trait SizeTypes {
  /** Pixel size and all size hints. Used to specify sizes of a component and
    * the maximum size of `PreferredGap`s and `ContainerGap`s. 
    */
  protected sealed class Size(val pixels: Int)

  /** Pixel size and `Infinite`. Used to specify the size of a `Gap`. */
  protected sealed trait GapSize extends Size
  
  /** Pixel size, `UseDefault` and `Infinite`. Used to specify the preffered
    * size of `PreferredGap`s and `ContainerGap`s.  
    */
  protected sealed trait PreferredGapSize extends Size
   
  /** Instructs the layout to use a component's default size. */
  object UseDefault extends Size(GroupLayout.DEFAULT_SIZE) with PreferredGapSize
  
  /** Instructs the layout to use a component's preferred size. */
  object UsePreferred extends Size(GroupLayout.PREFERRED_SIZE)
  
  /** Represents an arbitrarily large size. */
  object Infinite extends Size(Int.MaxValue) with GapSize with PreferredGapSize
  
  /** Implicitly converts an Int to a Size object when needed. 
    * 
    * @param pixels a size in pixels; must be >= 0
    */
  protected implicit def int2Size(pixels: Int) = {
    require(pixels >= 0, "size must be >= 0")
    new Size(pixels) with GapSize with PreferredGapSize
  }
}