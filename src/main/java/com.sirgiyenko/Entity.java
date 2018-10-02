package com.sirgiyenko;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class Entity {

	private Long id;
	private String title;
	private Instant dateIn;
	private BigDecimal price;

	public Entity(long id, String title, Instant dateIn, BigDecimal price) {
		this.id = id;
		this.title = title;
		this.dateIn = dateIn;
		this.price = price;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Instant getDateIn() {
		return dateIn;
	}

	public void setDateIn(Instant dateIn) {
		this.dateIn = dateIn;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Entity entity = (Entity) o;
		return Objects.equals(id, entity.id) &&
				Objects.equals(title, entity.title) &&
				Objects.equals(dateIn, entity.dateIn) &&
				Objects.equals(price, entity.price);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, title, dateIn, price);
	}
}