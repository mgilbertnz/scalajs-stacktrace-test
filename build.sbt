name in ThisBuild := "crm-stacktrace-test"
version in ThisBuild := "0.104.0"
scalaVersion := CrmProject.Version.scalaVersion

lazy val client = AppModules.client
lazy val server = AppModules.server

val insertCommand: State => State =
  (state: State) =>
    state.copy(remainingCommands = Exec("project server", None) +: state.remainingCommands)

onLoad in Global := (insertCommand(_: State)) compose (onLoad in Global).value
