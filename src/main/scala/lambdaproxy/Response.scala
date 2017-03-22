package biz.newrope.slsutil4s.lambdaproxy

import scala.beans.BeanProperty
import scala.collection.JavaConverters._

import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._

trait ResponseType {
  def body: String
  def headers: Map[String, String]
  def statusCode: Int

  def getBody: String = this.body
  def getStatusCode: Integer = this.statusCode
  def getHeaders: java.util.Map[String, String] = this.headers.asJava
}

object ResponseType {
  val defaultHeaders: Map[String, String] = Map(
    "Access-Control-Allow-Credentials" -> "false",
    "Access-Control-Allow-Headers" -> "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token",
    "Access-Control-Allow-Origin" -> "*"
  )
}

object NoContentResponse extends ResponseType {
  def statusCode: Int = 204
  def headers: Map[String, String] = ResponseType.defaultHeaders
  def body: String = ""
}

case class OKResponse(body: String) extends ResponseType {
  def statusCode: Int = 200
  def headers: Map[String, String] = ResponseType.defaultHeaders
}

object OKResponse {
  def apply(json: JObject): OKResponse = OKResponse(compact(render(json)))
  def apply(jsons: Iterable[JObject]): OKResponse = OKResponse(compact(render(jsons)))
  def apply(map: Map[String, Int]): OKResponse = OKResponse(compact(render(map)))
}

case class BadRequestResponse(message: String) extends ResponseType {
  def statusCode: Int = 400
  def headers: Map[String, String] = ResponseType.defaultHeaders
  def body: String = compact(render(Map("message" -> message)))
}

case class NotFoundResponse(message: String) extends ResponseType {
  def statusCode: Int = 404
  def headers: Map[String, String] = ResponseType.defaultHeaders
  def body: String = compact(render(Map("message" -> message)))
}
