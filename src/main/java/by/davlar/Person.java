package by.davlar;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Person {
    private int id;
    private String name;
    private int salary;
    private int tax;
}
