package eu.alpacaislands.rm2


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
  //TODO Add help functionality
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
    !arguments.contains("prompt") || !arguments.contains("p")
  }

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
}