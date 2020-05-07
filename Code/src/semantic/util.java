package semantic;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class util {

  public static List<Integer> getNumbers(String tempString) {
    String regEx = "[^0-9]+";
    Pattern p = Pattern.compile(regEx);
    Matcher m = p.matcher(tempString);
    String[] numbers = m.replaceAll(" ").trim().split(" ");
    List<Integer> retNumbers = new ArrayList<>();
    for (String number : numbers) {
      retNumbers.add(Integer.parseInt(number));
    }
    return retNumbers;
  }

  public static int getTypeSize(String typeString) {
    if (typeString.contains("char")) {
      return 1;
    } else if (typeString.contains("double")) {
      return 8;
    } else {
      return 4;
    }
  }

  public static int getMulSum(List<Integer> integerList) {
    int result = 1;
    for (Integer integer : integerList) {
      result = result * integer;
    }
    return result;
  }

  public static void main(String[] args) {
    System.out.println(getNumbers("int"));
  }
}
