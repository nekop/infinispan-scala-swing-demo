package org.infinispan.demo

import javax.swing.{JPanel, GroupLayout}
import scala.swing._


trait GroupPanel extends Panel {
  override lazy val peer: javax.swing.JPanel = {
    val p = new javax.swing.JPanel with SuperMixin
    p.setLayout(new GroupLayout(p))
    p
  }
  private lazy val layout = peer.getLayout.asInstanceOf[GroupLayout]
  
  def autoCreateGaps = layout.getAutoCreateGaps()
  def autoCreateGaps_=(b: Boolean) = layout.setAutoCreateGaps(b)
  
  def autoCreateContainerGaps = layout.getAutoCreateContainerGaps
  def autoCreateContainerGaps_=(b: Boolean) = layout.setAutoCreateContainerGaps(b)
  
  
  sealed case class Size(value: Int)
  case object SizePreferred extends Size(GroupLayout.PREFERRED_SIZE)
  case object SizeDefault extends Size(GroupLayout.DEFAULT_SIZE)
  implicit def intToSize(value: Int) = Size(value)
  
  case class SizeRange(min: Size, pref: Size, max: Size)
  object DefaultSizes extends SizeRange(SizeDefault, SizeDefault, SizeDefault)
  def Rigid(size: Size) = SizeRange(size, size, size)
  
  object ComponentPlacement extends Enumeration {
    import javax.swing.LayoutStyle.ComponentPlacement._
    val Indent = Value(INDENT.ordinal)
    val Related = Value(RELATED.ordinal)
    val Unrelated = Value(UNRELATED.ordinal)
  }
  object Alignment extends Enumeration {
    import GroupLayout.Alignment._
    val Leading = Value(LEADING.ordinal)
    val Trailing = Value(TRAILING.ordinal)
    val Center = Value(CENTER.ordinal)
    val Baseline = Value(BASELINE.ordinal)
  }
  implicit def convertAlignment(a: Alignment.Value): GroupLayout.Alignment =
    GroupLayout.Alignment.values()(a.id)
  
  sealed trait GroupItem
  sealed trait AnyGroupItem extends GroupItem
  sealed trait SeqGroupItem extends GroupItem
  sealed trait ParGroupItem extends GroupItem
  case class ComponentItem(component: Component, sizes: SizeRange) extends AnyGroupItem
  case class BaselineComponentItem(component: Component, sizes: SizeRange) extends SeqGroupItem
  case class Gap(sizes: SizeRange) extends AnyGroupItem
  case class Subgroup(group: Group) extends AnyGroupItem
  case class BaselineSubgroup(group: Group) extends SeqGroupItem
  case class ContainerGap(pref: Int, max: Int) extends SeqGroupItem
  case class PreferredGap(placement: ComponentPlacement.Value, pref: Int, max: Int) extends SeqGroupItem
  case class AlignedComponentItem(component: Component, alignment: Alignment.Value, sizes: SizeRange) extends ParGroupItem
  case class AlignedSubgroup(alignment: Alignment.Value, group: Group) extends ParGroupItem
  implicit def convertGroup(g: Group): Subgroup = Subgroup(g)
  
  implicit def componentItem(component: Component): ComponentItem = ComponentItem(component, DefaultSizes)
  
  case class Group(group: GroupLayout#Group) { self =>
    def addItem(item: GroupItem): Unit = item match {
      case i: AnyGroupItem =>
        i match {
          case ComponentItem(c, SizeRange(Size(min),Size(pref),Size(max))) => group.addComponent(c.peer, min,pref,max)
          case Gap(SizeRange(Size(min), Size(pref), Size(max))) => group.addGap(min, pref, max)
          case Subgroup(g) => group.addGroup(g.group)
        }
      case _ => error("Unhandled GroupItem: " + item)
    }
    def add(items: GroupItem*) = items foreach addItem
  }
  class SequentialGroup(override val group: GroupLayout#SequentialGroup) extends Group(group) {
    def this() = this(layout.createSequentialGroup) 
    override def addItem(item: GroupItem) = item match{
      case i: SeqGroupItem =>
        i match {
          case BaselineComponentItem(c, SizeRange(Size(min),Size(pref),Size(max))) => group.addComponent(true, c.peer, min,pref,max)
          case BaselineSubgroup(g) => group.addGroup(true, g.group)
          case ContainerGap(pref, max) => group.addContainerGap(pref,max)
          case PreferredGap(placement, pref, max) => group.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.values()(placement.id), pref, max)
        }
      case _ => super.addItem(item)
    }
  }
  class ParallelGroup(override val group: GroupLayout#ParallelGroup) extends Group(group) {
    def this() = this(layout.createParallelGroup)
    override def addItem(item: GroupItem) = item match {
      case i: ParGroupItem =>
        i match {
          case AlignedComponentItem(c, align, SizeRange(Size(min),Size(pref),Size(max))) =>
            group.addComponent(c.peer, align, min,pref,max)
          case AlignedSubgroup(align, g) =>
            group.addGroup(align, g.group)
        }
      case _ => super.addItem(item)
    }
  }
  
  def sequential(items: GroupItem*) = new SequentialGroup {
    add(items: _*)
  }
  private type P = (GroupItem*)=>ParallelGroup
  private def par(group: GroupLayout#ParallelGroup): P =
    items => new ParallelGroup(group) {
      add(items: _*)
    }
  def parallel(align: Alignment.Value, resize: Boolean): P =
    par(layout.createParallelGroup(align, resize))
  def parallel(): P = parallel(Alignment.Leading, true)
  def parallel(align: Alignment.Value): P = parallel(align, true)
  
  def baseline(resize: Boolean, anchorTop: Boolean): P =
    par(layout.createBaselineGroup(resize,anchorTop))
  
  def honorsVisibility = layout.getHonorsVisibility()
  def honorsVisibility_=(b: Boolean) = layout.setHonorsVisibility(b)
  def honorsVisibility_=(c: Component, b: Option[Boolean]) =
    layout.setHonorsVisibility(c.peer, b match {
      case None => null
      case Some(true) => java.lang.Boolean.TRUE
      case Some(false) => java.lang.Boolean.FALSE
    })
  
  def replace(c1: Component, c2: Component) = layout.replace(c1.peer, c2.peer)
  
  def linkSizes(cs: Component*) = layout.linkSize(cs.map(_.peer): _*)
  def linkWidths(cs: Component*) = layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, cs.map(_.peer): _*)
  def linkHeights(cs: Component*) = layout.linkSize(javax.swing.SwingConstants.VERTICAL, cs.map(_.peer): _*)
  
  def horizontalGroup(g: Group) = layout.setHorizontalGroup(g.group)
  def verticalGroup(g: Group) = layout.setVerticalGroup(g.group)
}


object TestGroupPanel extends SimpleSwingApplication {
  val top = new Frame {
    contents = new GroupPanel {
      val label1 = new Label("Label 1")
      val label2 = new Label("Label 2")
      val text1 = new TextField
      val text2 = new TextField
      autoCreateGaps = true
      autoCreateContainerGaps = true
      horizontalGroup {
        sequential(
          parallel()(label1, label2),
          parallel()(text1, text2)
        )
      }
      verticalGroup {
        sequential(
          parallel(Alignment.Baseline)(label1, text1),
          parallel(Alignment.Baseline)(label2, text2)
        )
      }
    }
  }
}
