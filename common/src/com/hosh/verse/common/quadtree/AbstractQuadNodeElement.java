package com.hosh.verse.common.quadtree;

import com.badlogic.gdx.math.Vector2;

public abstract class AbstractQuadNodeElement<T> {
	private T element;
	private Vector2 coords;

	public AbstractQuadNodeElement(final Vector2 coordinates, final T element) {
		this.coords = coordinates;
		this.element = element;
	}

	public AbstractQuadNodeElement(final T element) {
		this(new Vector2(), element);
	}

	public T getElement() {
		return element;
	}

	public Vector2 getCoordinates() {
		return coords;
	}

}
