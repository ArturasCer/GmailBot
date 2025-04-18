package org.example.data;


import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class Company {
    private String name;
    private String email;
    private String link;

    @Override
    public String toString() {
        return name + " " + link + " " + email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return Objects.equals(name, company.name) && Objects.equals(email, company.email) && Objects.equals(link, company.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, link);
    }
}
