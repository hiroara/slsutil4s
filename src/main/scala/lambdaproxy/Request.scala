package biz.newrope.slsutil4s.lambdaproxy

import scala.util.{Try, Success, Failure}
import scala.collection.JavaConverters._

import org.json4s._
import org.json4s.native.JsonMethods._

case class Request[P <: PathParameters, Q <: QueryParameters, B <: RequestBody, H <: RequestHeaders](
  val path: P, val query: Q, val body: B, val rawBody: Option[String], val headers: H, val requestContext: Option[RequestContext]
)

object Request {
  val emptyPathParameters: (Map[String, String]) => PathParameters = (_) => PathParameters.Empty
  val emptyQueryParameters: (Map[String, String]) => QueryParameters = (_) => QueryParameters.Empty
  val emptyRequestBody: (JValue) => RequestBody = (_) => RequestBody.Empty
  val emptyRequestHeaders: (Map[String, String]) => RequestHeaders = (_) => RequestHeaders.Empty

  def apply[P <: PathParameters, Q <: QueryParameters, B <: RequestBody, H <: RequestHeaders](data: java.util.Map[String, Object])(
    path: Map[String, String] => P = emptyPathParameters,
    query: Map[String, String] => Q = emptyQueryParameters,
    body: JValue => B = emptyRequestBody,
    headers: Map[String, String] => H = emptyRequestHeaders
  ): Request[P, Q, B, H] = apply(data.asScala.filterNot(_._2 == null).toMap, path, query, body, headers)

  def apply[P <: PathParameters, Q <: QueryParameters, B <: RequestBody, H <: RequestHeaders](
    data: Map[String, Any],
    path: Map[String, String] => P,
    query: Map[String, String] => Q,
    body: JValue => B,
    headers: Map[String, String] => H
  ): Request[P, Q, B, H] = {
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
      rawBody,
      data.get("headers").map(_.asInstanceOf[java.util.Map[String, String]]) match {
        case Some(data) => headers(data.asScala.filterNot(_._2 == null).toMap)
        case None => headers(Map())
      },
      data.get("requestContext").map(_.asInstanceOf[java.util.Map[String, Any]].asScala.toMap).map(RequestContext(_))
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

trait RequestHeaders
object RequestHeaders {
  object Empty extends RequestHeaders
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
