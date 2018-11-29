name := "cekic"

version := "0.1"

scalaVersion := "2.12.7"


///////////////////////////////////
////////// Dependencies ///////////
///////////////////////////////////

val log4jV = "1.2.17"
val clV = "1.2"
val jdomV = "2.0.2"
val jgraphV = "5.13.0.0"
val itextpdfV = "2.1.7"

libraryDependencies ++= Seq(
  "log4j"                       % "log4j"                 % log4jV,
  "commons-logging"             % "commons-logging"       % clV,
  "org.jdom"                    % "jdom"                  % jdomV,
  "jgraph"                      % "jgraph"                % jgraphV,
  "com.lowagie"                 % "itext"                 % itextpdfV
)
