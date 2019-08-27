package chapter03;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ExecuteAroundPattern {

  public static String processFile(BufferedReaderProcessor bufferedReaderProcessor) throws IOException {
    try (BufferedReader br = new BufferedReader(new FileReader("resources/data.txt"))) {
      return bufferedReaderProcessor.process(br);
    }
  }

  public static void main(String[] args) throws IOException {
    String oneLine = processFile((BufferedReader::readLine));
    System.out.println(oneLine);
  }

}
