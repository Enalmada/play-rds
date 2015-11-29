package controllers

import javax.inject.Inject

import play.api.Play._
import play.api.Play.current
import play.api.mvc.{Controller, Action}
import rds.StagingHelper
import play.api.i18n.{I18nSupport, MessagesApi}

import scala.util.{Success, Failure}


class Application @Inject()(implicit val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def index = Action { implicit request =>
    Ok(views.html.index())
  }

  def refreshStaging = Action { implicit request =>

    val flashMessage: (String, String) = StagingHelper.refreshStaging match {
      case Failure(exception) => "error" -> exception.getMessage
      case Success(result) => result
    }

    Redirect(routes.Application.index()).flashing(flashMessage)

  }


}
