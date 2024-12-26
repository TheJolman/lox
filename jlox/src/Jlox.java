public class Jlox {
  final static String PROJECT_NAME = "jlox";

  public static void main(String args[]) {
    if (args.length != 0) {
      System.out.println(args + " takes no arguments.");
      System.exit(0);
    }
    System.out.println("This is project " + PROJECT_NAME + ".");
    Test.poop();
    System.exit(0);
  }
}
