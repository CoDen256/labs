package mathe3;

import java.util.Arrays;

public class Task71 {

    public static void main(String[] args) {
//        new Plotter()
        String bitValue = Integer.toBinaryString(12322);
        String bitValue1 = Integer.toBinaryString(99999);
        boolean[] sum = sum(bits(bitValue), bits(bitValue1));
        System.out.println(Integer.parseInt(str(sum), 2));
    }


    public static boolean[] sum(boolean[] aSrc, boolean[] bSrc){
        int length = Math.max(aSrc.length, bSrc.length);
        // fill with zeros from left to match the length of both
        boolean[] a = new boolean[length];
        System.arraycopy(aSrc, 0, a, length-aSrc.length, aSrc.length);
        boolean[] b = new boolean[length];
        System.arraycopy(bSrc, 0, b, length-bSrc.length, bSrc.length);

        boolean[] c = new boolean[length+1];
        for (int i = length - 1; i >= 0; i--) {
            c[i] = (a[i] && b[i]) || (b[i] && c[i+1]) || (a[i] && c[i+1]); // carry
            c[i+1] = a[i] ^ b[i] ^ c[i+1];
        }
        int offset = c[0] ? 1 : 0;
        boolean[] result = new boolean[length + offset];
        System.arraycopy(c, offset, result, 0, result.length);
        return c;
    }


    public static boolean[] bits(String bitValue){
        char[] chars = bitValue.toCharArray();
        boolean[] bitArray = new boolean[chars.length];
        for (int i = 0; i < chars.length; i++) {
            bitArray[i] = chars[i] == '1';
        }
        return bitArray;
    }

    public static String str(boolean[] bits){
        StringBuilder str = new StringBuilder();
        for (boolean bit : bits) str.append(bit ? 1 : 0);
        return str.toString();
    }

}
