public class Main {
    private static int a = 5;

    public static void main(String[] args) {
        int a = 3;
        int b = 5;
        if (a > b) {
            System.out.println("ddd");
        }
        int t = test2(2, 15);
        System.out.println(t);
    }
    public static int test2(int x, int y) {
        int b, c, d;
        b = Integer.parseInt(String.valueOf(y / 3));
        System.out.println("What");
        c = Integer.parseInt(String.valueOf(y + 2));
        System.out.println("What");
        d = Integer.parseInt(String.valueOf(y + 5));
        if (x > b || x > c && x > d) {
            return 3;
        } else {
            return 4;
        }
    }
    public static int test(int x, int y) {
        int z = y - 5;
        int r = z + 5;
        int k = Integer.parseInt("4");
        if (x > 0)
            z = x + y;
        else
            z = x - y;
        if (k > 3) {
            a = 5;
        }
        if (r != 15) {
            return 1;
        } else {
            return z;
        }
    }
}