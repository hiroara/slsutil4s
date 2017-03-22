package biz.newrope.slsutil4s.lambdaproxy

import scala.util.{Try, Success, Failure}
import scala.collection.JavaConverters._

import org.json4s._
import org.json4s.native.JsonMethods._

case class Request[P <: PathParameters, Q <: QueryParameters, B <: RequestBody](val path: P, val query: Q, val body: B, val rawBody: Option[String])

object Request {
  val emptyPathParameters: (Map[String, String]) => PathParameters = (_) => PathParameters.Empty
  val emptyQueryParameters: (Map[String, String]) => QueryParameters = (_) => QueryParameters.Empty
  val emptyRequestBody: (JValue) => RequestBody = (_) => RequestBody.Empty

  def apply[P <: PathParameters, Q <: QueryParameters, B <: RequestBody](data: java.util.Map[String, Object])(
    path: Map[String, String] => P = emptyPathParameters,
    query: Map[String, String] => Q = emptyQueryParameters,
    body: JValue => B = emptyRequestBody
  ): Request[P, Q, B] = apply(data.asScala.filterNot(_._2 == null).toMap, path, query, body)

  def apply[P <: PathParameters, Q <: QueryParameters, B <: RequestBody](
    data: Map[String, Any],
    path: Map[String, String] => P,
    query: Map[String, String] => Q,
    body: JValue => B
  ): Request[P, Q, B] = {
    val rawBody = data.get("body").map(_.asInstanceOf[String])
    Request(
      data.get("pathParameters").map(_.asInstanceOf[java.util.Map[String, String]]) match {
        case Some(data) => path(data.asScala.filterNot(_._2 == null).toMap)
        case None => path(Map())
      },
      data.get("queryStringParameters").map(_.asInstanceOf[java.util.Map[String, String]]) match {
        case Some(data) => query(data.asScala.filterNot(_._2 == null).toMap)
        case None => query(Map())
      },
      rawBody.flatMap(raw => Try { parse(raw) }.toOption) match {
        case Some(value) => body(value)
        case None => body(parse("{}"))
      },
      rawBody
    )
  }
}

trait PathParameters
object PathParameters {
  object Empty extends PathParameters
}

trait QueryParameters
object QueryParameters {
  object Empty extends QueryParameters
}

trait RequestBody
object RequestBody {
  object Empty extends RequestBody
}

object ParamParser {
  import scala.util.control.Exception._
  def getAsInt(data: Map[String, String], key: String): Option[Int] = data.get(key).flatMap {
    catching(classOf[NumberFormatException]) opt _.toInt
  }
  def getAsBoolean(data: Map[String, String], key: String): Option[Boolean] = data.get(key).flatMap {
    _ match {
      case "1" | "t" | "T" | "true" | "TRUE" | "on" | "ON" => Some(true)
      case "f" | "F" | "false" | "FALSE" | "off" | "OFF" => Some(false)
      case _ => None
    }
  }
}
