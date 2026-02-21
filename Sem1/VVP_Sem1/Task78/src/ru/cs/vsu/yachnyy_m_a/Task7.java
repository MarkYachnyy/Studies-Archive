package ru.cs.vsu.yachnyy_m_a;

public class Task7 {

    public static void main(String[] args){
        int[] a = solution(new int[]{1,2,2,4,-7});
        for (int i: a){
            System.out.println(i);
        }
    }

    private static int[] solution(int[] arr) {
        int index = 0;
        int length = 0;

        for (int i = 2; i <= arr.length; i++) {
            for (int j = 0; j <= arr.length - i; j++) {
                boolean b = true;
                for (int k = j+1; k < j + i; k++) {
                    if (Math.abs(arr[k]) % 2 != 1 - Math.abs(arr[k - 1]) % 2) {
                        b = false;
                        break;
                    }
                }
                if (b) {
                    index = j;
                    length = i;
                }
            }
        }

        return new int[]{index,length};
    }
}
