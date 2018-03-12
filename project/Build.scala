import CrmProject._
import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport.{Universal, topLevelDirectory}
import com.typesafe.sbt.web.Import.{Assets, WebKeys, pipelineStages}
import com.typesafe.sbt.web.SbtWeb
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.scalajs.sbtplugin.cross.CrossProject
import play.routes.compiler.InjectedRoutesGenerator
import play.sbt.PlayImport._
import play.sbt.PlayScala
import play.sbt.routes.RoutesKeys.routesGenerator
import sbt.Keys.{libraryDependencies, _}
import sbt.{Compile, Credentials, Path, Project, Test, file, _}
import webscalajs.ScalaJSWeb
import webscalajs.WebScalaJS.autoImport.{scalaJSPipeline, scalaJSProjects}

object CrmProject {

  object Version {
    val scalaVersion = "2.12.4"
    val playVersion = "2.6.12"
  }

  type PE = Project => Project

  type CPE = CrossProject => CrossProject

  def commonBaseSettings: PE = _
    .configure(preventDocPublish)
    .settings(
      scalaVersion := Version.scalaVersion,
      scalacOptions ++= scalaCompilerOptions,
    )

  def jvmBaseSettings: PE = _
    .configure(commonBaseSettings)

  def playBaseSettings: PE = _
    .configure(commonBaseSettings)
    .settings(
      routesGenerator := InjectedRoutesGenerator
    )

  def jsBaseSettings: PE = _
    .configure(commonBaseSettings, scalaJsSettings)

  def crossBaseSettings: CPE = _
    .jvmConfigure(jvmBaseSettings)
    .jsConfigure(jsBaseSettings)

  def preventDocPublish: PE = _.settings(
    publishArtifact in(Compile, packageDoc) := false,
    publishArtifact in packageDoc := false,
    sources in(Compile, doc) := Seq.empty
  )

  val scalaCompilerOptions = Seq(
    "-feature",
    "-deprecation",
    "-unchecked",
    "-Xfatal-warnings",
    "-Ywarn-inaccessible",
    "-Ywarn-nullary-override",
    "-Ywarn-numeric-widen",
    "-Ywarn-value-discard",
    "-Ywarn-dead-code",
  )

  val serverLibs = Seq(
    guice
  )

  def scalaJsSettings: PE = _.settings(
    scalaJSModuleKind := ModuleKind.NoModule
  )

}

object AppModules {

  lazy val client = (project in file("application/client"))
    .configure(jsBaseSettings)
    .settings(
      scalaJSUseMainModuleInitializer := true,
      mainClass in Compile := Some("com.stacktrace.test.client.ApplicationMain"),
      (emitSourceMaps in fullOptJS) := true
    )
    .enablePlugins(ScalaJSPlugin, ScalaJSWeb, SbtWeb)

  lazy val server = (project in file("application/server"))
    .configure(playBaseSettings)
    .settings(
      name := (name in ThisBuild).value,
      topLevelDirectory in Universal := None,
      scalaJSProjects := Seq(client),
      pipelineStages in Assets := Seq(scalaJSPipeline),
      WebKeys.exportedMappings in Assets := Seq(),
      libraryDependencies ++= Seq(specs2 % Test) ++ CrmProject.serverLibs
    )
    .enablePlugins(PlayScala)

}
