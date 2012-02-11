package com.hosh.verse.quadtree;

import java.awt.Dimension;
import java.awt.Point;

public abstract class AbstractQuadTree<T> {

	protected Dimension size;
	protected Point startCoords;

	public AbstractQuadTree(final Point startCoordinates, final Dimension size) {
		this.size = size;
		this.startCoords = startCoordinates;
	}

	public Dimension getSize() {
		return size;
	}

	public Point getStartCoordinates() {
		return startCoords;
	}

	public abstract void clear();

	public abstract AbstractQuadNode<T> getRootNode();

}
