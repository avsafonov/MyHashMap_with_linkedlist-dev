public class Test {
    public static void main(String[] args) {
        Person Jack = new Person("Jack", 29);
        Person Avi = new Person("Avi", 25);
        Person AAAA = new Person("ADASDASDA", 234);

        MyHashMap<Integer, String> test = new MyHashMap<>();
        test.put(1, "qweqe");
        test.put(2, "adadsa");
        test.put(3, "firstCommit");
        test.put(null,"asd");
        test.put(null, "asdasd");
        test.put(3,null);
    }
}

