package controllers

import javax.inject.Inject

import play.api.Play._
import play.api.Play.current
import play.api.mvc.{Controller, Action}
import rds.StagingHelper
import play.api.i18n.{I18nSupport, MessagesApi}


class Application @Inject()(implicit val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def index = Action { implicit request =>
    Ok(views.html.index())
  }

  def refreshStaging = Action { implicit request =>
    val result:(String, String) = StagingHelper.refreshStaging
    Redirect(routes.Application.index()).flashing(result)

  }


}
