package eu.alpacaislands.rm2

import java.io.File
import java.net.InetAddress
import java.nio.file.Path
import scala.io.Source
import scala.io.StdIn.readLine
import scala.xml.{Elem, XML}


object Delete {

  /**
   * The string that is printed when user uses --help or -h argument.
   * */
  private val helpString = "###########\n    RM2\n###########\nUsuage:\n example: \"rm2 foo.bar --force\"\n\nArguments:\n--force | -f\n  Forcefully deletes given path(s) ignoring everything.\n--recursive | -r\n  Deletes given path(s) recursively.\n--help | -h\n  Displays this message.\n--dry | -d\n  Runs the command without actually removing\n--prompt | -p\n  Asks the user for confirmation before deleting. If force is enabled this \n  option is false by default.\n-c\n  Describes if should not check for right machine, if used it skips.\n--verbose | -v\n  If specified rm2 will run quiet.\n--warn | -w\n  If specified rm2 will not ask for confirmation on warned paths.\n"

  def handle(c: Config): Boolean = {



    if (c.isHelp) {
      print(helpString)
      return true
    }
    if (c.shouldCheckMachine) {
      if (!isRightMachine()) return false
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

  private def isRightMachine(): Boolean = {
    val ip: String = InetAddress.getLocalHost.getHostName
    val confirm = readLine(s"Are you sure you want to run the command on $ip? (y/n): ")

    if (confirm == "y") {
      true
    } else if (confirm == "n") {
      false
    } else {
      println("Invalid input: " + confirm)
      isRightMachine()
    }
  }

  private def delete(c: Config): Map[String, Boolean] = {
    var results: Map[String, Boolean] = Map.empty[String, Boolean]

    c.paths.foreach { path => {


      val file = new File(path)
      if (c.shouldWarn && c.checkPath(file.toPath)) {
        val confirm = readLine(s"Are you sure you want to delete ${file.toPath.toRealPath()}, because this path is marked as important. (y/n): ")

        if (confirm.toLowerCase == "y") {
          if (c.isForce) {
            results += (path -> deleteForcefully(file, c.isDry))
          }
          else if (c.isRecursive) {
            results += (path -> deleteRecursively(file, c.isDry, c.shouldPrompt))
          }
          else {
            results += (path -> deleteNormally(file, c.isDry, c.shouldPrompt))
          }
        } else {
          println("Skipping")
          false
        }
      } else {
        if (c.isForce) {
          results += (path -> deleteForcefully(file, c.isDry))
        }
        else if (c.isRecursive) {
          results += (path -> deleteRecursively(file, c.isDry, c.shouldPrompt))
        }
        else {
          results += (path -> deleteNormally(file, c.isDry, c.shouldPrompt))
        }
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
  private def deleteNormally(f: File, dry: Boolean, shouldPrompt: Boolean): Boolean = {
    if (!dry) {
      if (!f.exists()) throw new RuntimeException("Path: " + f.getAbsolutePath + " does not exist")

      if (f.isDirectory) throw new RuntimeException("Path: " + f.getAbsolutePath + "is a directory")
    }
    if (shouldPrompt) {
      val confirm = readLine(s"Are you sure you want to delete ${f.getAbsolutePath}? (y/n): ")

      if (confirm.toLowerCase == "y") {
        f.deleteWithDry(dry)
      } else {
        println("Skipping")
        false
      }
    } else {
      println(s"Deleting ${f.getAbsolutePath}")
      f.deleteWithDry(dry)
    }
  }

  private def deleteRecursively(f: File, dry: Boolean, shouldPrompt: Boolean): Boolean = {
    if (!dry) {
      if (!f.exists) {
        throw new RuntimeException(s"Path: ${f.getAbsolutePath} does not exist!")
      }
    }

    if (f.isDirectory) {
      f.listFiles.foreach(deleteRecursively(_, dry, shouldPrompt))
    }

    if (shouldPrompt) {
      val confirm = readLine(s"Are you sure you want to delete ${f.getAbsolutePath}? (y/n): ")

      if (confirm.toLowerCase == "y") {
        f.deleteWithDry(dry)
      } else {
        false
      }
    } else {
      f.deleteWithDry(dry)
    }


  }

  private def deleteForcefully(f: File, dry: Boolean): Boolean = {
    // Force delete is actually just deleting recursively

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