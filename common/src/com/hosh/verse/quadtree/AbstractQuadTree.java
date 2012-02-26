package com.hosh.verse.quadtree;

import com.badlogic.gdx.math.Vector2;

public abstract class AbstractQuadTree<T> {

	private Vector2 size;
	private Vector2 startCoords;

	public AbstractQuadTree(final Vector2 startCoordinates, final Vector2 size) {
		this.size = size;
		this.startCoords = startCoordinates;
	}

	public Vector2 getSize() {
		return size;
	}

	public Vector2 getStartCoordinates() {
		return startCoords;
	}

	public abstract void clear();

	public abstract AbstractQuadNode<T> getRootNode();

}
