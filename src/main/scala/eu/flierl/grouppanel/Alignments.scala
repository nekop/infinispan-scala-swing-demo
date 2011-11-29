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

/** Provides alignment constants for parallel groups in a `GroupPanel`.
  * 
  * @author Andreas Flierl
  */
trait Alignments {
  /**
   * Represents an alignment of a component (or group) within a parallel group.
   * 
   * @see javax.swing.GroupLayout.Alignment
   */
  protected sealed class Alignment(private[grouppanel] val wrapped: GroupLayout.Alignment)
  
  /** Elements are aligned along their baseline. Only valid along the vertical axis. */
  object Baseline extends Alignment(GroupLayout.Alignment.BASELINE)
  
  /** Elements are centered inside the group. */
  object Center extends Alignment(GroupLayout.Alignment.CENTER)
  
  /** Elements are anchored to the leading edge (origin) of the group. */
  object Leading extends Alignment(GroupLayout.Alignment.LEADING)
  
  /** Elements are anchored to the trailing edge (end) of the group. */
  object Trailing extends Alignment(GroupLayout.Alignment.TRAILING)
}
