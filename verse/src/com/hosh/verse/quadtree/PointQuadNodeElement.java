package com.hosh.verse.quadtree;

import java.awt.Point;

public class PointQuadNodeElement<T> extends AbstractQuadNodeElement<T> {

	public PointQuadNodeElement(final Point coordinates, final T element) {
		super(coordinates, element);
	}

}
