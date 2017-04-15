package biz.newrope.slsutil4s.lambdaproxy

import scala.collection.JavaConverters._

class RequestContext(
  val accountId: Option[String],
  val resourceId: Option[String],
  val stage: Option[String],
  val authorizer: Option[RequestContext.Authorizer],
  val requestId: Option[String],
  val identity: Option[RequestContext.Identity],
  val resourcePath: Option[String],
  val httpMethod: Option[String],
  val apiId: Option[String]
)

object RequestContext {
  def apply(data: Map[String, Any]) = new RequestContext(
    data.get("accountId").map(_.asInstanceOf[String]),
    data.get("resourceId").map(_.asInstanceOf[String]),
    data.get("stage").map(_.asInstanceOf[String]),
    data.get("authorizer").map(_.asInstanceOf[java.util.Map[String, Any]].asScala.toMap).map(Authorizer(_)),
    data.get("requestId").map(_.asInstanceOf[String]),
    data.get("identity").map(_.asInstanceOf[java.util.Map[String, Any]].asScala.toMap).map(Identity(_)),
    data.get("resourcePath").map(_.asInstanceOf[String]),
    data.get("httpMethod").map(_.asInstanceOf[String]),
    data.get("apiId").map(_.asInstanceOf[String])
  )

  class Authorizer(val principalId: Option[String])

  object Authorizer {
    def apply(data: Map[String, Any]) = new Authorizer(
      data.get("principalId").map(_.asInstanceOf[String])
    )
  }

  class Identity(
    val cognitoIdentityPoolId: Option[String],
    val accountId: Option[String],
    val cognitoIdentityId: Option[String],
    val caller: Option[String],
    val apiKey: Option[String],
    val sourceIp: Option[String],
    val accessKey: Option[String],
    val cognitoAuthenticationType: Option[String],
    val cognitoAuthenticationProvider: Option[String],
    val userArn: Option[String],
    val userAgent: Option[String],
    val user: Option[String]
  )
  object Identity {
    def apply(data: Map[String, Any]) = new Identity(
      data.get("cognitoIdentityPoolId").map(_.asInstanceOf[String]),
      data.get("accountId").map(_.asInstanceOf[String]),
      data.get("cognitoIdentityId").map(_.asInstanceOf[String]),
      data.get("caller").map(_.asInstanceOf[String]),
      data.get("apiKey").map(_.asInstanceOf[String]),
      data.get("sourceIp").map(_.asInstanceOf[String]),
      data.get("accessKey").map(_.asInstanceOf[String]),
      data.get("cognitoAuthenticationType").map(_.asInstanceOf[String]),
      data.get("cognitoAuthenticationProvider").map(_.asInstanceOf[String]),
      data.get("userArn").map(_.asInstanceOf[String]),
      data.get("userAgent").map(_.asInstanceOf[String]),
      data.get("user").map(_.asInstanceOf[String])
    )
  }
}
