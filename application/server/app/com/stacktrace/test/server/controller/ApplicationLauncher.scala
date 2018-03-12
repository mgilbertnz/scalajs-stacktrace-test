package com.stacktrace.test.server.controller

import javax.inject.{Inject, Provider}
import play.Application
import play.api.mvc._

class ApplicationLauncher @Inject()(controllerComponents: ControllerComponents,
                                    app: Provider[Application]) extends AbstractController(controllerComponents) {

  def index = Action {
    Ok(com.stacktrace.test.server.views.html.main(app.get.isDev))
  }

}
