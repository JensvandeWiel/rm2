package eu.alpacaislands.rm2

import java.io.File


object Delete {

  /**
   * The string that is printed when user uses --help or -h argument.
   * */
  private val helpString = "placeholder"

  def handle(c: Config): Boolean = {
    if (c.isHelp) {
      println(helpString)
      true
    }

    val results = delete(c)
    val failed = results.filter(_._2 == false).keys.toArray


    if (results.values.forall(_ == true)) {
      true
    } else if (c.isDry) {
      true
    } else {
      false
    }
  }

  private def delete(c: Config): Map[String, Boolean] = {
    var results: Map[String, Boolean] = Map.empty[String, Boolean]

    c.paths.foreach { path => {


      val file = new File(path)

      if (c.isForce) {
        results += (path -> deleteForcefully(file, c.isDry))
      }
      else if (c.isRecursive) {
        results += (path -> deleteRecursively(file, c.isDry))
      }
      else {
        results += (path -> deleteNormally(file, c.isDry))
      }
    }
    }

    results
  }

  /**
   * Deletes file normally
   *
   * @param f   The file to delete.
   * @param dry If true it does not delete the file.
   * @return true if and only if the file or directory is
   *         successfully deleted; false otherwise. Returns true when dry is true
   */
  private def deleteNormally(f: File, dry: Boolean): Boolean = {
    if (!dry) {
      if (!f.exists()) throw new RuntimeException("Path: " + f.getAbsolutePath + " does not exist")

      if (f.isDirectory) throw new RuntimeException("Path: " + f.getAbsolutePath + "is a directory")
    }
    f.deleteWithDry(dry)
  }

  private def deleteRecursively(f: File, dry: Boolean): Boolean = {
    if (!dry) {
      if (!f.exists) {
        throw new RuntimeException(s"Path: ${f.getAbsolutePath} does not exist!")
      }
    }

    if (f.isDirectory) {
      f.listFiles.foreach(deleteRecursively(_, dry))
    }

    f.deleteWithDry(dry)
  }

  private def deleteForcefully(f: File, dry: Boolean): Boolean = {
    // Force delete is actually just deleting recursively
    //TODO deleteForcefully should ignore nonexistent files/directories

    if (!f.exists()) return true


    if (f.isDirectory) {
      f.listFiles.foreach(deleteForcefully(_, dry))
    }
    f.deleteWithDry(dry)
  }


  implicit class RichFile(file: File) {
    /**
     * This function just deletes the given file but adds an option to dry run or actually delete it
     *
     * @param dry is if the file should actually be deleted
     * */
    def deleteWithDry(dry: Boolean = false): Boolean = {
      if (dry) {
        true
      } else {
        file.delete()
      }
    }
  }
}