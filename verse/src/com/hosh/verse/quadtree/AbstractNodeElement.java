package com.hosh.verse.quadtree;

import java.awt.Point;

@SuppressWarnings("serial")
public abstract class AbstractNodeElement<T> extends Point {

	private T element;

	public AbstractNodeElement(final Point coordinates, final T element) {
		super(coordinates);
		this.element = element;
	}

	public AbstractNodeElement(final T element) {
		this.element = element;
	}

	public T getElement() {
		return element;
	}

}
