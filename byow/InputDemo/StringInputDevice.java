<<<<<<< HEAD
package byow.InputDemo;
=======
package proj3.byow.InputDemo;
>>>>>>> bb1ffa14e8f97a1cf7337129d485377ff86d7dcc

/**
 * Created by hug.
 */
public class StringInputDevice implements InputSource  {
    private String input;
    private int index;

    public StringInputDevice(String s) {
        index = 0;
        input = s;
    }

    public char getNextKey() {
        char returnChar = input.charAt(index);
        index += 1;
        return returnChar;
    }

    public boolean possibleNextInput() {
        return index < input.length();
    }
}
