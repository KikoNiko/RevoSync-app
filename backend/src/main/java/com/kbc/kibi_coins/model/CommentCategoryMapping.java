package com.kbc.kibi_coins.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "comment_category_mapping")
public class CommentCategoryMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "comment", nullable = false, unique = true)
    private String comment;
    @Column(name = "category_name", nullable = false)
    private String categoryName;

}
