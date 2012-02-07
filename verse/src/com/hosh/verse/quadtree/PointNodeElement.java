package com.hosh.verse.quadtree;

import java.awt.Point;

@SuppressWarnings("serial")
public class PointNodeElement<T> extends AbstractNodeElement<T> {

	public PointNodeElement(final Point coordinates, final T element) {
		super(coordinates, element);
	}

}
