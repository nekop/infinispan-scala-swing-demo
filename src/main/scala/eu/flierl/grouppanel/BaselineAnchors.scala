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

/** Provides baseline anchor constants for a `GroupPanel`. Baseline anchors
  * may either be explicitly specified (using the Parallel.baseline(...) factory)
  * or will be determined based on its elements: the baseline will be anchored 
  * to the bottom if and only if all the elements with a baseline, and that are
  * aligned to the baseline, have a baseline resize behavior of
  * `CONSTANT_DESCENT`.
  * 
  * @author Andreas Flierl
  */
trait BaselineAnchors {
  /** 
   * Allows to specify whether to anchor the baseline to the top or the bottom 
   * of a baseline-aligned parallel group.
   */
  protected sealed class BaselineAnchor(private[grouppanel] val wrapped: Boolean)

  /** Anchor the baseline to the top of the group. */
  object AnchorToTop extends BaselineAnchor(true)
  
  /** Anchor the baseline to the bottom of the group. */
  object AnchorToBottom extends BaselineAnchor(false)
}