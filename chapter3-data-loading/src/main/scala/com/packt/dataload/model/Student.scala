package com.packt.dataload.model

import scala.util.Try
import scala.util.Success
import scala.util.Failure
import scala.Array.canBuildFrom

class Student(school: String,
  sex: String,
  age: Int,
  address: String,
  famsize: String,
  pstatus: String,
  medu: Int,
  fedu: Int,
  mjob: String,
  fjob: String,
  reason: String,
  guardian: String,
  traveltime: Int,
  studytime: Int,
  failures: Int,
  schoolsup: String,
  famsup: String,
  paid: String,
  activities: String,
  nursery: String,
  higher: String,
  internet: String,
  romantic: String,
  famrel: Int,
  freetime: Int,
  goout: Int,
  dalc: Int,
  walc: Int,
  health: Int,
  absences: Int,
  g1: Int,
  g2: Int,
  g3: Int) extends Product {

  @throws(classOf[IndexOutOfBoundsException])
  override def productElement(n: Int): Any = n match {
    case 0 => school
    case 1 => sex
    case 2 => age
    case 3 => address
    case 4 => famsize
    case 5 => pstatus
    case 6 => medu
    case 7 => fedu
    case 8 => mjob
    case 9 => fjob
    case 10 => reason
    case 11 => guardian
    case 12 => traveltime
    case 13 => studytime
    case 14 => failures
    case 15 => schoolsup
    case 16 => famsup
    case 17 => paid
    case 18 => activities
    case 19 => nursery
    case 20 => higher
    case 21 => internet
    case 22 => romantic
    case 23 => famrel
    case 24 => freetime
    case 25 => goout
    case 26 => dalc
    case 27 => walc
    case 28 => health
    case 29 => absences
    case 30 => g1
    case 31 => g2
    case 32 => g3
    case _ => throw new IndexOutOfBoundsException(n.toString())
  }

  override def productArity: Int = 33

  override def canEqual(that: Any): Boolean = that.isInstanceOf[Student]

}

object Student {

  def apply(str: String): Option[Student] = {
    val paramArray = str.split(";").map(param => param.replaceAll("\"", "")) //Few values have extra double quotes around it
    Try(
      new Student(paramArray(0),
        paramArray(1),
        paramArray(2).toInt,
        paramArray(3),
        paramArray(4),
        paramArray(5),
        paramArray(6).toInt,
        paramArray(7).toInt,
        paramArray(8),
        paramArray(9),
        paramArray(10),
        paramArray(11),
        paramArray(12).toInt,
        paramArray(13).toInt,
        paramArray(14).toInt,
        paramArray(15),
        paramArray(16),
        paramArray(17),
        paramArray(18),
        paramArray(19),
        paramArray(20),
        paramArray(21),
        paramArray(22),
        paramArray(23).toInt,
        paramArray(24).toInt,
        paramArray(25).toInt,
        paramArray(26).toInt,
        paramArray(27).toInt,
        paramArray(28).toInt,
        paramArray(29).toInt,
        paramArray(30).toInt,
        paramArray(31).toInt,
        paramArray(32).toInt)) match {
        case Success(student) => Some(student)
        case Failure(throwable) => {
          println (throwable.getMessage())
          None
        }
      }

  }

}