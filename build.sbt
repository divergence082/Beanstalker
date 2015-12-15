name := "beanstalker"

version := "0.0.1"

scalaVersion := "2.11.7"

organization := "com.github.div082"

resolvers ++= Seq("maven.mei.fm" at "http://maven.mei.fm/nexus/content/groups/public/")

libraryDependencies ++= Seq(
  "net.liftweb"             %% "lift-json"        % "2.6.2",
  "com.dinstone.beanstalkc" %  "beanstalk-client" % "2.0.0"
)
