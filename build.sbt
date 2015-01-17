name := "Scholargram"

version := "1.0"

lazy val `scholargram` = (project in file(".")).enablePlugins(PlayScala)
  .settings(
    slick <<= slickCodeGenTask, // register manual sbt command
    sourceGenerators in Compile <+= slickCodeGenTask // register automatic code generation on every compile, remove for only manual use,
  )

scalaVersion := "2.11.1"

libraryDependencies ++= Seq( jdbc , anorm , cache , ws,
  "com.typesafe.slick" %% "slick" % "2.1.0",
  "com.typesafe.slick" %% "slick-codegen" % "2.1.0-RC3",
  "com.h2database" % "h2" % "1.3.170",
  "com.typesafe.play" %% "play-slick" % "0.8.1"
)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

val dbCreate = "database/create.sql"

// code generation task
lazy val slick = TaskKey[Seq[File]]("gen-tables")
lazy val slickCodeGenTask = (sourceManaged, dependencyClasspath in Compile, runner in Compile, streams) map { (dir, cp, r, s) =>
  val func = FileFunction.cached(s.cacheDirectory / "slick-gen",
    FilesInfo.lastModified, /* inStyle */
    FilesInfo.exists)({(inFiles: Set[File])=>
    inFiles.flatMap { sqlPath =>
      val outputDir = (dir / "main").getPath // place generated files in sbt's managed sources folder
      val url = "jdbc:h2:mem:scholargram;INIT=runscript from '%s'".format(sqlPath) // connection info for a pre-populated throw-away, in-memory db for this demo, which is freshly initialized on every run
      val jdbcDriver = "org.h2.Driver"
      val slickDriver = "scala.slick.driver.H2Driver"
      val pkg = "models"
      toError(r.run("scala.slick.codegen.SourceCodeGenerator", cp.files, Array(slickDriver, jdbcDriver, url, outputDir, pkg), s.log))
      val fname = outputDir + "/models/Tables.scala"
      Set(file(fname))
    }
  })
  func(Set(file(dbCreate))).toSeq
}

