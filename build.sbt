name          := "reads"
organization  := "ohnosequences"
description   := ""

bucketSuffix  := "era7.com"

libraryDependencies ++= Seq(
  "ohnosequences" %% "fastarious" % "0.9.0-24-ge0d632e"
)

// // For resolving dependency versions conflicts:
// dependencyOverrides ++= Set()

// // If you need to deploy this project as a Statika bundle:
// generateStatikaMetadataIn(Compile)

// // This includes tests sources in the assembled fat-jar:
// fullClasspath in assembly := (fullClasspath in Test).value

// // This turns on fat-jar publishing during release process:
// publishFatArtifact in Release := true

// shows time for each test:
testOptions in Test += Tests.Argument("-oD")
// disables parallel exec
parallelExecution in Test := false
