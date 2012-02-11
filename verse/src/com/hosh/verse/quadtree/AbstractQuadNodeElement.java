package com.hosh.verse.quadtree;

import java.awt.Point;

public abstract class AbstractQuadNodeElement<T> {
	private T element;
	private Point coords;

	public AbstractQuadNodeElement(final Point coordinates, final T element) {
		this.coords = coordinates;
		this.element = element;
	}

	public AbstractQuadNodeElement(final T element) {
		this(new Point(), element);
	}

	public T getElement() {
		return element;
	}

	public Point getCoordinates() {
		return coords;
	}

}
