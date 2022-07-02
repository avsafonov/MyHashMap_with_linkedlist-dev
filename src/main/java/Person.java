import java.util.Objects;

public class Person {
    private final String name;
    private int age;

    Person(String name, int age){
        this.name = name;
        this.age = age;
    }

    @Override
    public boolean equals (Object o){
        if (o == this)
            return true;
        if (o instanceof Person && this.name == ((Person) o).name && this.age == ((Person) o).age)
            return true;
        return false;
    }

    @Override
    public int hashCode (){
        return Objects.hash(this.age, this.name);
    }


}

