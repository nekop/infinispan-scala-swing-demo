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
import javax.swing.LayoutStyle.ComponentPlacement

/** A panel that uses [[javax.swing.GroupLayout]] to visually arrange 
  * its components. 
  * 
  * The key point to understanding this layout manager is that it separates
  * horizontal and vertical layout. Thus, every component appears twice: once 
  * in the horizontal and once in the vertical layout. Consult the Java API 
  * documentation for `GroupLayout` and Sun's Java tutorials for a 
  * comprehensive explanation.
  * 
  * The main advantage of using this panel instead of manually tinkering with
  * the layout is that this panel provides a concise, declarative syntax for
  * laying out its components. This approach should make most use cases easier.
  * In some special cases, e.g. when re-creating layouts on-the-fly, it might
  * be preferable to use a more imperative style, for which direct access to
  * the underlying layout manager is provided.
  * 
  * In contrast to the underlying swing layout, this panel activates the 
  * automatic creation of gaps between components by default, since this panel
  * is intended for coding UIs "by hand", not so much for visual UI builder
  * tools. Many features of the underlying layout are aimed at those, tough.
  * Most of them are available through this panel for completeness' sake but it 
  * is anticipated that coders won't need to use them very much.
  * 
  * =Code examples=
  * 
  * This section contains a few simple examples to showcase the basic 
  * functionality of `GroupPanel`s. For all examples, it is assumed
  * that everything from the package `scala.swing` is imported and the code is
  * placed inside a [[scala.swing.SimpleSwingApplication]] like this:
  * 
  * {{{
  * import swing._
  * 
  * object Example extends SimpleSwingApplication {
  *   override def top = new MainFrame {
  *     contents = new GroupPanel {
  *       // example code here
  *     }
  *   }
  * }
  * }}}
  * 
  * ==Simple panel with 2 components==
  * 
  * In the first example, there's a label and a text field, which appear
  * in a horizontal sequence but share the same vertical space.
  * 
  * {{{
  * val label = new Label("Fnord:")
  * val textField = new TextField(20)
  * 
  * theHorizontalLayout is Sequential(label, textField)
  * theVerticalLayout is Parallel(label, textField)
  * }}}
  * 
  * It can be observed that the resize behaviour of the text field is rather 
  * strange. To get better behaviour, the components' vertical sizes can be 
  * linked together.
  * 
  * {{{
  * linkVerticalSize(label, textField)
  * }}}
  * 
  * Alternatively, it would have been possible to disallow the resizing of 
  * the vertical, parallel group. To achieve this, the vertical layout line
  * should be written this way:
  * 
  * {{{
  * theVerticalLayout is Parallel(Leading, FixedSize)(label, textField)
  * }}}
  * 
  * Since text fields aren't resizable when used with baseline alignment (more 
  * about that further down), the following code also prevents (vertical) 
  * resizing:
  * 
  * {{{
  * theVerticalLayout is Parallel(Baseline)(label, textField)
  * }}}
  * 
  * ==Size and alignment==
  * 
  * Components can be added with custom size constraints (minumum, preferred, 
  * maximum size). The next example showcases that. The text field appears 
  * with a preferred height of 100 pixels and when the component is resized, 
  * it can be shrinked down to its minimum height of 50 pixels and enlarged 
  * to its maximum height of 200 pixels.
  * 
  * '''Note:''' similar to the methods `asBaseline` and `aligned`, 
  * the `sized(...)` method is made available by an implicit conversion. But
  * because type inference does not work well with implicits in this case,
  * the conversion (via the `add`) method must be done explicitly. The same 
  * holds for all the sizing methods for components. 
  * 
  * {{{
  * theHorizontalLayout is Sequential(label, textField)
  * theVerticalLayout is Parallel(label, add(textField).sized(50, 100, 200))
  * }}}
  * 
  * The vigilant reader may have noticed that the `sized(...)` method does not
  * take integer parameters but rather instances of the type `Size`. This type,
  * along with some specializations of it, are used to enforce valid values for
  * sizes, which can be pixel sizes greater than or equal to zero or special
  * hints: `UseDefault`, `UsePreferred` and `Infinite`. For convenience, `Int`s 
  * are implicitly converted to pixel sizes where required.
  * 
  * Instead of using these hints, it most of the time is easier to use the
  * provided convenience methods: `sized`, `fixedToDefaultSize`, 
  * `resizable` and `fullyResizable`.
  * 
  * Because the default alignment in a parallel group is `Leading`,
  * both components are "glued" to the top of the container (panel). To align
  * the label's text with the text inside the text field, an explicit alignment
  * can be specified in a preceding argument list, like this:
  * 
  * {{{
  * theHorizontalLayout is Sequential(label, textField)
  * theVerticalLayout is Parallel(Baseline)(label, add(textField).sized(50, 100, 200))
  * }}}
  * 
  * This example also shows a potential problem of baseline alignment: some 
  * components stop being resizable. More specifically, the javadoc
  * for `GroupLayout.ParallelGroup` states:
  * 
  *   - Elements aligned to the baseline are resizable if they have have a 
  *     baseline resize behavior of `CONSTANT_ASCENT` or `CONSTANT_DESCENT`.
  *   - Elements with a baseline resize behavior of `OTHER` or `CENTER_OFFSET`
  *     are not resizable.
  * 
  * Since a text field's resizing behaviour is `CENTER_OFFSET`, it is
  * not resizable when used with baseline alignment.
  * 
  * ===Baseline reference===
  * 
  * Since panels do not provide a meaningful baseline by default, a `GroupPanel`
  * allows to define a baseline reference, i.e. a component that provides the
  * baseline for the panel instead. For example, to use `myButton` as the baseline
  * reference, one may declare:
  * 
  * {{{
  * theBaselineReference is myButton
  * }}}
  * 
  * ==Gaps==
  * 
  * The `GroupPanel` turns on automatic creation of gaps between
  * components and along the container edges. To see the difference, try turning
  * this feature off manually by inserting the following lines:
  * 
  * {{{
  * autoCreateGaps = false
  * autoCreateContainerGaps = false
  * }}}
  * 
  * With both types of gaps missing, the components are clamped together and to
  * the container edges, which does not look very pleasing. Gaps can be added
  * manually, too. The following example does this in order to get a result that 
  * looks similar to the version with automatically created gaps, albeit in a
  * much more verbose manner.
  * 
  * {{{
  * theHorizontalLayout is Sequential(
  *   ContainerGap(),
  *   label, 
  *   PreferredGap(Related),
  *   textField,
  *   ContainerGap()
  * )
  *
  * theVerticalLayout is Sequential(
  *   ContainerGap(),
  *   Parallel(label, textField),
  *   ContainerGap()
  * )
  * }}}
  * 
  * Rigid gaps with custom size or completely manual gaps (specifying minimum, 
  * preferred and maximum size) between components are created with
  * the `Gap` object:
  *
  * {{{
  * theHorizontalLayout is Sequential(
  *   label, 
  *   Gap(10, 20, 100),
  *   textField
  * )
  * 
  * theVerticalLayout is Sequential(
  *   Parallel(label, Gap(30), textField)
  * )
  * }}}
  * 
  * In a parallel group, such a gap can be used to specify a minimum amount of 
  * space taken by the group.
  * 
  * In addition to rigid gaps in the previous example, it is also possible to
  * specify gaps that resize. This could be done by specifying a maximum size 
  * of `Infinite`. However, for the most commonly used type of these, there is
  * a bit of syntax sugar available with the `Spring` 
  * and `ContainerSpring` objects. 
  *
  * {{{
  * theHorizontalLayout is Sequential(
  *   ContainerGap(),
  *   label, 
  *   Spring(), // default is Related
  *   textField,
  *   ContainerSpring()
  * )
  * }}}
  * 
  * These create gaps that minimally are as wide as a `PreferredGap` would
  * be - it is possible to specify whether the `Related` or `Unrelated` distance
  * should be used - but can be resized to an arbitrary size.
  *
  * {{{
  * theHorizontalLayout is Sequential(
  *   ContainerGap(),
  *   label, 
  *   Spring(Unrelated),
  *   textField,
  *   ContainerSpring()
  * )
  * }}}
  * 
  * The preferred size can also be specified more closely (`UseDefault`  
  * or `Infinite` aka "as large as possible"):
  *
  * {{{
  * theHorizontalLayout is Sequential(
  *   ContainerGap(),
  *   label, 
  *   Spring(Unrelated, Infinite),
  *   textField,
  *   ContainerSpring(Infinite)
  * )
  * }}}
  * 
  * Please note that `PreferredGap`, `Spring`, `ContainerGap` and `ContainerSpring` may 
  * '''only''' be used inside a sequential group.
  * 
  * ==A dialog with several components==
  * 
  * As a last, more sophisticated example, here's the `GroupPanel`
  * version of the "Find" dialog presented as example 
  * for `GroupLayout` in the Java tutorials by Sun:
  * 
  * {{{
  * val label = new Label("Find what:")
  * val textField = new TextField
  * val caseCheckBox = new CheckBox("Match case")
  * val wholeCheckBox = new CheckBox("Whole words")
  * val wrapCheckBox = new CheckBox("Wrap around")
  * val backCheckBox = new CheckBox("Search backwards")
  * val findButton = new Button("Find")
  * val cancelButton = new Button("Cancel")
  * 
  * theHorizontalLayout is Sequential(
  *   label,
  *   Parallel(
  *     textField,
  *     Sequential(
  *       Parallel(caseCheckBox, wholeCheckBox),
  *       Parallel(wrapCheckBox, backCheckBox)
  *     )
  *   ),
  *   Parallel(findButton, cancelButton)
  * )
  * 
  * linkHorizontalSize(findButton, cancelButton)
  * 
  * theVerticalLayout is Sequential(
  *   Parallel(Baseline)(label, textField, findButton),
  *   Parallel(
  *     Sequential(
  *       Parallel(Baseline)(caseCheckBox, wrapCheckBox),
  *       Parallel(Baseline)(wholeCheckBox, backCheckBox)
  *     ),
  *     cancelButton
  *   )
  * )
  * }}}
  * 
  * @see javax.swing.GroupLayout
  * @author Andreas Flierl
  */
class GroupPanel extends Panel 
                 with BaselineCustomization
                 with GroupLayoutProperties
                 with Delegations 
                 with Alignments
                 with Placements
                 with ComponentsInGroups
                 with Groups
                 with Gaps {

  private[grouppanel] type G = GroupLayout#Group
  
  /** This panel's underlying layout manager. */
  val layout = new GroupLayout(peer)
  
  peer.setLayout(layout)
  autoCreateGaps = true
  autoCreateContainerGaps = true
  
  /** Starting point for the horizontal layout. */
  object theHorizontalLayout {
    def is(group: Group) = layout.setHorizontalGroup(group.buildChildren)
  }
  
  /** Starting point for the vertical layout. */
  object theVerticalLayout {
    def is(group: Group) = layout.setVerticalGroup(group.buildChildren)
  }  
}
