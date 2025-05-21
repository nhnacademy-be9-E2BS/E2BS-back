package com.nhnacademy.back.product.category.domain.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long categoryId;

	@ManyToOne
	@JoinColumn(name = "category_id2")
	private Category parent;

	@Column(length = 30, nullable = false)
	private String categoryName;

	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<Category> children = new ArrayList<>();

	public Category(String categoryName, Category parent) {
		this.categoryName = categoryName;
		this.parent = parent;
	}

	public void setCategory(String categoryName) {
		this.categoryName = categoryName;
	}
}
