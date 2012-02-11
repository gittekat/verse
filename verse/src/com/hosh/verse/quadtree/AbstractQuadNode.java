package com.hosh.verse.quadtree;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Map;

public abstract class AbstractQuadNode<T> {

	protected final static int MAX_ELEMENTS = 4;
	protected final static int MAX_DEPTH = 4;
	protected final Dimension size;
	protected final Point startCoords;
	protected final int maxDepth;
	protected final int maxElements;
	protected final int depth;

	public static enum Quadrant {
		TOP_LEFT, BOTTOM_RIGHT, BOTTOM_LEFT, TOP_RIGHT
	}

	public AbstractQuadNode(final Point startCoordinates, final Dimension bounds, final int depth) {
		this(startCoordinates, bounds, depth, MAX_ELEMENTS, MAX_DEPTH);
	}

	public AbstractQuadNode(final Point startCoordinates, final Dimension size, final int depth, final int maxDepth, final int maxElements) {
		this.startCoords = startCoordinates;
		this.size = size;
		this.maxDepth = maxDepth;
		this.maxElements = maxElements;
		this.depth = depth;
	}

	public Dimension getSize() {
		return size;
	}

	public int getMaxDepth() {
		return this.maxDepth;
	}

	public int getDepth() {
		return this.depth;
	}

	public Point getStartCoordinates() {
		return startCoords;
	}

	public int getMaxElements() {
		return maxElements;
	}

	public abstract void subdivide();

	public abstract Map<Quadrant, ? extends AbstractQuadNode<T>> getChildren();

	public abstract void clear();

}
