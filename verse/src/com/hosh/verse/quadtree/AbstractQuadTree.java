package com.hosh.verse.quadtree;

import java.awt.Dimension;
import java.awt.Point;

public abstract class AbstractQuadTree<T> {

	protected Dimension size;
	protected Point startCoordinates;

	public AbstractQuadTree(final Point startCoordinates, final Dimension size) {
		this.size = size;
		this.startCoordinates = startCoordinates;
	}

	public Dimension getSize() {
		return this.size;
	}

	public Point getStartCoordinates() {
		return this.startCoordinates;
	}

	public abstract void clear();

	public abstract AbstractNode<T> getRootNode();

}
