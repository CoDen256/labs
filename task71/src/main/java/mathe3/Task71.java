package mathe3;

import java.util.Arrays;

public class Task71 {

    public static void main(String[] args) {
//        new Plotter()
        String bitValue = Integer.toBinaryString(12322);
        String bitValue1 = Integer.toBinaryString(99999);
        boolean[] product = sum(bits("00011000000100010"), bits(bitValue1));
        System.out.println(Integer.parseInt(str(product), 2));
    }
    public static boolean[] multiply(boolean[] a, boolean[] b){
        return null;
    }


    public static boolean[] sum(boolean[] a, boolean[] b){
        if( a.length != b.length)
            throw new IllegalArgumentException("a and b must have same length");


        boolean[] c = new boolean[a.length+1];
        for (int i = a.length - 1; i >= 0; i--) {
            c[i] = (a[i] && b[i]) || (b[i] && c[i+1]) || (a[i] && c[i+1]); // carry
            c[i+1] = a[i] ^ b[i] ^ c[i+1];
        }

        return removeLeadingZeros(c);
    }

    public static boolean[] removeLeadingZeros(boolean[] arr){
        int index = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i]){
                index = i;
                break;
            }
        }
        boolean[] result = new boolean[arr.length - index];
        System.arraycopy(arr, index, result, 0, result.length);
        return result;
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
