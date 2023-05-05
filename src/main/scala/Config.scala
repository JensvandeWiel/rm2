package eu.alpacaislands.rm2

import java.nio.file.{Path, Paths}
import scala.io.Source
import scala.xml.{Elem, XML}


/**
 * Handles the config of the deletion, It parses the arguments passed to the application and it has boolean values if certain arguments are present
 * */
class Config(args: Array[String]) {

  /**
   * Contains all arguments
   * */
  private val arguments: Array[String] = parseArgs(args)._2

  /**
   * Contains all paths that are to be deleted.
   */
  val paths: Array[String] = parseArgs(args)._1

  /**
   * Contains all the paths that need to be extra warned
   */
  val warnPaths: Array[Path] = getWarnedDirectories

  /**
   * Describes if it should warn for warned directories.
   * */
  val shouldWarn: Boolean = !(arguments.contains("warn") || arguments.contains("w"))


  /**
   * Describes if user wants to force deletion
   * */
  val isForce: Boolean = arguments.contains("force") || arguments.contains("f")

  /**
   * Describes if user want to remove recursive
   * */
  val isRecursive: Boolean = arguments.contains("recursive") || arguments.contains("r")

  /**
   * Describes if user prompted fot the help command
   * */
  val isHelp: Boolean = arguments.contains("help") || arguments.contains("h")

  /**
   * Describes if it should delete or run without deleting (if found it will run without deleting).
   * */
  val isDry: Boolean = arguments.contains("dry") || arguments.contains("d")

  /**
   * Describes if it should prompt before deletion. If force is enabled this option is false by default. If force is not enabled its true by default.
   */
  val shouldPrompt: Boolean = {
    if (isForce) {
      false
    }
    !(arguments.contains("prompt") || arguments.contains("p"))
  }

  /**
   * Describes if should not check for right machine
   */
  val shouldCheckMachine: Boolean = !arguments.contains("c")


  //Todo make app verbose and add option to run quiet
  val isVerbose: Boolean = (!arguments.contains("verbose") || arguments.contains("v"))

  /**
   *
   * @param args the arguments passed to sort
   * @return Array[String] of the files parsed as arguments an Array[String] of arguments parsed as arguments
   *
   * */
  private def parseArgs(args: Array[String]): (Array[String], Array[String]) = {
    var files: Array[String] = Array.apply[String]()
    var arguments: Array[String] = Array.apply[String]()


    if (args.length == 0) {
      throw new RuntimeException("No file specified.")
    } else {
      //TODO rewrite so it only removes the first one or 2 "-" and not the middle and maybe make two seperate arrays for short "-" and long "--" args
      args.foreach { arg => {
        if (arg.startsWith("--") || arg.startsWith("-")) {
          arguments = arguments :+ arg.replaceAll("-", "").toLowerCase
        } else {
          files = files :+ arg
        }
      }
      }
      (files, arguments)
    }
  }

  private def getWarnedDirectories: Array[Path] = {
    val warnFile = Source.fromResource("warn.xml").mkString
    val warnXML: Elem = XML.loadString(warnFile)
    (warnXML \\ "directory").map(dir => Paths.get(dir.text)).toArray
  }

  def checkPath(p: Path): Boolean = {
    warnPaths.exists(warnPath => warnPath.compareTo(p) > 0)
  }
}