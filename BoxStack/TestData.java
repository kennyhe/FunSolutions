class TestData {
    public static void main(String [] args) {
        int x = rand(20);
        System.out.println(x);
        for (int i = 0; i < x; i++) {
           System.out.println("" + rand(100) + " " + rand(100) + " " + rand(100));
        }
    }

    static int rand(int x) {
        return (int)(x * Math.random() + 1);
    }

}

