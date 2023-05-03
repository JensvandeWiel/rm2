package eu.alpacaislands.rm2

object Main {
  def main(args: Array[String]): Unit = {
    var c = new Config(args)
    Delete.handle(c)
  }
}