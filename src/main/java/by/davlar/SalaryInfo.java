package by.davlar;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SalaryInfo {
    private String info;
    private List<Person> employees;
}
