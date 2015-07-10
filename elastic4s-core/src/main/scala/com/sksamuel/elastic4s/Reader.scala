package com.sksamuel.elastic4s

import org.scalactic._

import scala.collection.{Seq, generic}
import scala.reflect.ClassTag

@deprecated("use HitAs, this reader trait has a broken contravariance implementation", "1.6.1")
trait Reader[-U] {
  def read[T <: U : Manifest](json: String): T
}

trait HitAs[T] {
  def as(hit: RichSearchHit): T
}

trait HitRead[T] {
  def as(hit: RichSearchHitLike): T Or Every[ErrorMessage]
}

trait HitFieldRead[T] {
  def as(prefix:String)(hit: RichSearchHitField): T Or Every[ErrorMessage]
  def as(hit: RichSearchHitField): T Or Every[ErrorMessage]=as("")(hit)
}

trait LPHitFieldReads{
  implicit def hitRead2HitFieldRead[T](implicit hitRead:HitRead[T]) =new HitFieldRead[T] {
    import scala.collection.JavaConverters._
    override def as(prefix: String)(hit: RichSearchHitField): Or[T, Every[ErrorMessage]] = hit match {
      case MissingRichSearchField(name)=> Bad(One(s"$prefix $name field is missing"))
      case field=> field.value[Any] match {
        case map:java.util.Map[_,_] => hitRead.as(RichMapSearchHit(map.asInstanceOf[java.util.Map[String,Any]].asScala.toMap))
        case other => Bad(One(s"$prefix ${hit.name} expected a object (Map[String,Any]) got $other"))
      }
    }
  }

  implicit object stringHitFieldRead extends HitFieldRead[String] {
    override def as(prefix:String)(hit: RichSearchHitField): String Or Every[ErrorMessage] = hit match {
      case MissingRichSearchField(name)=> Bad(One(s"$prefix $name field is missing"))
      case field => field.value[Any] match {
        case v:String => Good(v)
        case o => Bad(One(s"$prefix ${hit.name} expected a String got $o"))
      }
    }
  }
  implicit object intHitFieldRead extends HitFieldRead[Int] {
    override def as(prefix:String)(hit: RichSearchHitField): Int Or Every[ErrorMessage] = hit match {
      case MissingRichSearchField(name) => Bad(One(s"$prefix $name field is missing"))
      case field => field.value[Any] match {
        case v:Int => Good(v)
        case v:Long => Good(v.toInt)
        case v:BigInt => Good(v.intValue())
        case o => Bad(One(s"$prefix ${hit.name} expected an Int got $o"))
      }
    }
  }

  implicit object BigDecimalHitFieldRead extends HitFieldRead[BigDecimal] {
    override def as(prefix:String)(hit: RichSearchHitField): BigDecimal Or Every[ErrorMessage] = hit match {
      case MissingRichSearchField(name) => Bad(One(s"$prefix $name field is missing"))
      case field => field.value[Any] match {
        case v:Int => Good(BigDecimal(v))
        case v:Long => Good(BigDecimal(v))
        case v:BigInt => Good(BigDecimal(v))
        case v:Double=> Good(BigDecimal(v))
        case v:Float=> Good(BigDecimal(v))
        case v:BigDecimal=> Good(v)
        case v:java.math.BigDecimal=> Good(v)
        case v:java.math.BigInteger=> Good(BigDecimal(v.longValue()))
        case o => Bad(One(s"$prefix ${hit.name} expected an Int got $o"))
      }
    }
  }
  implicit def optionRead[T ](implicit read:HitFieldRead[T]):HitFieldRead[Option[T]]=new HitFieldRead[Option[T]]{
    override def as(prefix: String)(hit: RichSearchHitField): Option[T] Or Every[ErrorMessage] = hit match {
        case MissingRichSearchField(name)=> Good(None)
        case field => read.as(prefix)(hit).map(Some.apply)
      }
  }
  /**
   * Generic deserializer for collections types.
   */
  implicit def traversableReads[F[_], A](implicit bf: generic.CanBuildFrom[F[_], A, F[A]], ra: HitFieldRead[A]): HitFieldRead[F[A]] = new HitFieldRead[F[A]] {
    import scala.collection.JavaConverters._
    import Accumulation._
    override def as(prefix: String)(hit: RichSearchHitField): F[A] Or Every[ErrorMessage]= hit match {
      case MissingRichSearchField(name) => Bad(One(s"$prefix $name field is missing"))
      case fields => val validations = fields.value[Any] match {
        case list:java.util.List[_]=> list.asScala.toSeq.zipWithIndex.map{case (elt,idx)=> ra.as(SomeValueSearchHitField(s"${hit.name}[$idx]", elt))}
        case list:scala.collection.Seq[_]=> list.zipWithIndex.map{case (elt,idx)=> ra.as(SomeValueSearchHitField(s"${hit.name}[$idx]", elt))}
        case other => Seq.empty[Good[A,Every[ErrorMessage]]]
      }
      validations.combined.map(_.to(bf))
    }
  }

  /**
   * Deserializer for Array[T] types.
   */
  implicit def ArrayReads[T: HitFieldRead: ClassTag]: HitFieldRead[Array[T]] = new HitFieldRead[Array[T]] {
    override def as(prefix: String)(hit: RichSearchHitField): Array[T] Or Every[ErrorMessage]= hit.validate[List[T]](prefix).map(_.toArray)
  }
}
object HitFieldRead extends LPHitFieldReads
