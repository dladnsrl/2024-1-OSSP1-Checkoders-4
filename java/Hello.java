public class Hello {
    public static void main(String[] args) {
        if (args.length > 0) {
            for (String arg : args) {
                System.out.print(arg);
            }
        } else {
            System.out.println("인자가 전달되지 않았습니다.");
        }
    }
}

/*
public class Hello {
    public static void main(String[] args) {
        System.out.println("123123");
    }
}
*/
