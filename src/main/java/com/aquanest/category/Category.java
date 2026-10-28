//package com.aquanest.category;
//
//public class Category {
//}




package com.aquanest.category;

import jakarta.persistence.*;

@Entity
@Table(
        name = "categories",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_categories_name",
                columnNames = "name"
        )
)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(length = 500)
    private String description;

//    @Column(nullable = false)
//    private boolean active = true;
@Column(nullable = false)
private boolean active = true;

    protected Category() {
    }

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }
}