package com.hosh.verse.common.quadtree;

import com.badlogic.gdx.math.Vector2;

public class PointQuadNodeElement<T> extends AbstractQuadNodeElement<T> {

	public PointQuadNodeElement(final Vector2 coordinates, final T element) {
		super(coordinates, element);
	}

}
