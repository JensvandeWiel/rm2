package eu.alpacaislands.rm2

import org.scalatest.funsuite.AnyFunSuite

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, File, PrintStream, Console => JavaConsole}
import java.nio.file.{Files, Paths}

class rm2Test extends AnyFunSuite {
  test("canDeleteNormalFile") {
    // Create file
    val tmp = Files.createTempFile("temp", ".tmp")
    //Run actual test
    assert(Delete.handle(new Config(Array(tmp.toRealPath().toString, "-c", "-p", "-w"))))
  }

  test("cantDeleteNonexistentFile") {

    //catch exception
    val e = intercept[RuntimeException] {
      Delete.handle(new Config(Array("foo.bar", "-c", "-p", "-w")))
    }

    assert(e.getMessage.contains("does not exist"))
  }

  test("cantDeleteDirectory") {
    // Create files
    val tmp = Files.createTempDirectory("cantDeleteDirectory")

    //catch exception
    val e = intercept[RuntimeException] {
      Delete.handle(new Config(Array(tmp.toRealPath().toString, "-c", "-p", "-w")))
    }

    //clean up
    tmp.toFile.deleteOnExit()

    assert(e.getMessage.contains("is a directory"))


  }

  test("canDeleteForcefully") {
    // Create files
    val tmp = Files.createTempDirectory("canDeleteForcefully")
    for (i <- 1 to 3) {
      val combinedPath = Paths.get(tmp.toRealPath().toString, s"test$i.tmp")
      new File(combinedPath.toString).createNewFile()
    }
    //Run actual test
    assert(Delete.handle(new Config(Array(tmp.toRealPath().toString, "--force", "-c", "-p", "-w"))))
  }

  test("canDeleteForcefullyWithFakeFiles") {
    //Run actual test
    assert(Delete.handle(new Config(Array("blabla", "--force", "-c", "-p", "-w"))))
  }

  test("canDeleteRecursively") {
    // Create alot of files
    val tmp = Files.createTempDirectory("canDeleteRecursively")
    for (i <- 1 to 3) {
      val combinedPath = Paths.get(tmp.toRealPath().toString, s"test$i")

      val dir = Files.createDirectory(combinedPath)
      for (j <- 1 to 3) {
        val p = Paths.get(dir.toRealPath().toString, s"test$j")
        Files.createDirectory(p)
        for (t <- 1 to 3) {
          val p2 = Paths.get(p.toRealPath().toString, s"test$t.tmp")
          new File(p2.toString).createNewFile()
        }
      }
    }
    //Run actual test
    assert(Delete.handle(new Config(Array(tmp.toRealPath().toString, "-r", "-c", "-p", "-w"))))
  }

  test("canDeleteRecursivelyWithFakeDir") {
    //catch exception
    val e = intercept[RuntimeException] {
      Delete.handle(new Config(Array(Paths.get("foo", "bar").toString, "-c", "-p", "-w")))
    }

    assert(e.getMessage.contains("does not exist"))
  }

  test("canDisplayHelpMessage") {
    //catch help output
    val baos = new ByteArrayOutputStream()
    Console.withOut(new PrintStream(baos)) {
      Delete.handle(new Config(Array("--help")))
    }

    val output = baos.toString
    assert(output == "###########\n    RM2\n###########\nUsuage:\n example: \"rm2 foo.bar --force\"\n\nArguments:\n--force | -f\n  Forcefully deletes given path(s) ignoring everything.\n--recursive | -r\n  Deletes given path(s) recursively.\n--help | -h\n  Displays this message.\n--dry | -d\n  Runs the command without actually removing\n--prompt | -p\n  Asks the user for confirmation before deleting. If force is enabled this \n  option is false by default.\n-c\n  Describes if should not check for right machine, if used it skips.\n--verbose | -v\n  If specified rm2 will run quiet.\n--warn | -w\n  If specified rm2 will not ask for confirmation on warned paths.\n")
  }

  test("canPromptYesOnDeletion") {
    //create temp file
    val file = Files.createTempFile("foo", ".bar").toFile
    val input = "y\n"
    val in = new ByteArrayInputStream(input.getBytes())
    val out = new ByteArrayOutputStream()
    Console.withIn(in) {
      Console.withOut(out) {
        val config = new Config(Array(file.getAbsolutePath, "-c", "-w"))
        // add assertions to verify that the prompt was displayed and the file was deleted
        Delete.handle(config)
      }
    }
    !file.exists()
  }

  test("canPromptNoOnDeletion") {
    //create temp file
    val file = Files.createTempFile("foo", ".bar").toFile
    val input = "n\n"
    val in = new ByteArrayInputStream(input.getBytes())
    val out = new ByteArrayOutputStream()
    Console.withIn(in) {
      Console.withOut(out) {
        val config = new Config(Array(file.getAbsolutePath, "-c", "-w"))
        // add assertions to verify that the prompt was displayed and the file was deleted
        Delete.handle(config)
      }
    }
    println(out.toString)
    //clean up and return
    file.deleteOnExit()
    assert(file.exists)
  }

  test("canConfirmMachine") {
    //create temp file
    val file = Files.createTempFile("foo", ".bar").toFile
    val input = "y\n"
    val in = new ByteArrayInputStream(input.getBytes())
    val out = new ByteArrayOutputStream()
    Console.withIn(in) {
      Console.withOut(out) {
        val config = new Config(Array(file.getAbsolutePath, "-p", "-w"))
        //clean up and return
        file.deleteOnExit()
        assert(Delete.handle(config))
      }
    }
  }

  test("canDenyMachine") {
    //create temp file
    val file = Files.createTempFile("foo", ".bar").toFile
    val input = "n\n"
    val in = new ByteArrayInputStream(input.getBytes())
    val out = new ByteArrayOutputStream()
    Console.withIn(in) {
      Console.withOut(out) {
        val config = new Config(Array(file.getAbsolutePath, "-p", "-w"))
        Delete.handle(config)
      }
    }
    println(out)
    //clean up and return
    file.deleteOnExit()
    assert(file.exists())
  }

  test("canIgnoreWarning") {

    val path = Paths.get("test.test")


    //create temp file
    val file = new File(path.toString)
    file.createNewFile()
    val input = "y\n"
    val in = new ByteArrayInputStream(input.getBytes())
    val out = new ByteArrayOutputStream()
    Console.withIn(in) {
      Console.withOut(out) {
        val config = new Config(Array(file.getAbsolutePath, "-p", "-c"))
        Delete.handle(config)
      }
    }
    //clean up and return
    file.deleteOnExit()
    assert(!file.exists())
  }

  test("canAcceptWarning") {

    val path = Paths.get("test.test")


    //create temp file
    val file = new File(path.toString)
    file.createNewFile()
    val input = "n\n"
    val in = new ByteArrayInputStream(input.getBytes())
    val out = new ByteArrayOutputStream()
    Console.withIn(in) {
      Console.withOut(out) {
        val config = new Config(Array(file.getAbsolutePath, "-p", "-c"))
        Delete.handle(config)
      }
    }
    //clean up and return
    file.deleteOnExit()
    assert(file.exists())
  }

}