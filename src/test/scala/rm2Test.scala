package eu.alpacaislands.rm2

import org.scalatest.funsuite.AnyFunSuite
import java.io.{ByteArrayOutputStream, File, PrintStream}
import java.nio.file.{Files, Paths}


class rm2Test extends AnyFunSuite {
  test("canDeleteNormalFile") {
    // Create file
    val tmp = Files.createTempFile("temp", ".tmp")
    //Run actual test
    assert(Delete.handle(new Config(Array(tmp.toRealPath().toString))))
  }

  test("cantDeleteNonexistentFile") {

    //catch exception
    val e = intercept[RuntimeException] {
      Delete.handle(new Config(Array("foo.bar")))
    }

    assert(e.getMessage.contains("does not exist"))
  }

  test("cantDeleteDirectory") {
    // Create files
    val tmp = Files.createTempDirectory("cantDeleteDirectory")

    //catch exception
    val e = intercept[RuntimeException] {
      Delete.handle(new Config(Array(tmp.toRealPath().toString)))
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
    assert(Delete.handle(new Config(Array(tmp.toRealPath().toString, "--force"))))
  }

  test("canDeleteForcefullyWithFakeFiles") {
    //Run actual test
    assert(Delete.handle(new Config(Array("blabla", "--force"))))
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
    assert(Delete.handle(new Config(Array(tmp.toRealPath().toString, "-r"))))
  }

  test("canDeleteRecursivelyWithFakeDir") {
    //catch exception
    val e = intercept[RuntimeException] {
      Delete.handle(new Config(Array(Paths.get("foo", "bar").toString)))
    }

    assert(e.getMessage.contains("does not exist"))
  }

  test("canDisplayHelpMessage") {
    //catch help output
    val baos = new ByteArrayOutputStream()
    Console.withOut(new PrintStream(baos)) {
      Delete.handle(new Config(Array("--help")))
    }
    val output = baos.toString.trim
    assert(output == "placeholder")
  }
}