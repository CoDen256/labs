package mathe3;

import java.util.Arrays;

public class Task71 {

    public static void main(String[] args) {

    }

    public static boolean[] multiply(boolean[] aSrc, boolean[] bSrc){
        boolean[] a = addLeadingZerosToMatchSize(aSrc, Math.max(aSrc.length, bSrc.length));
        boolean[] b = addLeadingZerosToMatchSize(bSrc, Math.max(aSrc.length, bSrc.length));

        boolean[][] subproducts = new boolean[a.length][2 * a.length - 1];
        for (int i = 0; i < a.length; i++) {
            if (b[a.length-i-1]){
                boolean[] shiftedToLeft = addTrailingZeros(a, i);
                subproducts[i] = addLeadingZerosToMatchSize(shiftedToLeft, 2 * a.length - 1);
            }
        }

        boolean[] c = new boolean[a.length * 2];
        for (boolean[] subproduct : subproducts) {
            c = sum(c, subproduct);
        }
        return removeLeadingZeros(c);
    }


    public static boolean[] sum(boolean[] aSrc, boolean[] bSrc){
        boolean[] a = addLeadingZerosToMatchSize(aSrc, Math.max(aSrc.length, bSrc.length));
        boolean[] b = addLeadingZerosToMatchSize(bSrc, Math.max(aSrc.length, bSrc.length));

        boolean[] c = new boolean[a.length+1];
        for (int i = a.length - 1; i >= 0; i--) {
            c[i] = (a[i] && b[i]) || (b[i] && c[i+1]) || (a[i] && c[i+1]); // carry
            c[i+1] = a[i] ^ b[i] ^ c[i+1];
        }

        return removeLeadingZeros(c);
    }

    public static boolean[] addTrailingZeros(boolean[] a, int amount){
        return Arrays.copyOf(a, a.length + amount);
    }

    public static boolean[] addLeadingZerosToMatchSize(boolean[] a, int totalSize){
        if (a.length > totalSize) throw new IllegalArgumentException("A length should be less than totalSize");
        boolean[] result = new boolean[totalSize];
        System.arraycopy(a, 0, result, totalSize-a.length, a.length);
        return result;
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
