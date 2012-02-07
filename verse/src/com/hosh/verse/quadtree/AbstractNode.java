package com.hosh.verse.quadtree;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Map;

public abstract class AbstractNode<T> {

	protected final static int MAX_ELEMENTS = 4;
	protected final static int MAX_DEPTH = 4;

	public static enum Quadrant {
		TOP_LEFT, BOTTOM_RIGHT, BOTTOM_LEFT, TOP_RIGHT
	}

	protected Dimension bounds;
	protected Point startCoordinates;
	protected int maxDepth;
	protected int maxElements;
	protected int depth;

	public AbstractNode(final Point startCoordinates, final Dimension bounds, final int depth) {
		this(startCoordinates, bounds, depth, MAX_ELEMENTS, MAX_DEPTH);
	}

	public AbstractNode(final Point startCoordinates, final Dimension bounds, final int depth, final int maxDepth, final int maxElements) {
		this.startCoordinates = startCoordinates;
		this.bounds = bounds;
		this.maxDepth = maxDepth;
		this.maxElements = maxElements;
		this.depth = depth;
	}

	public Dimension getBounds() {
		return this.bounds;
	}

	public Point getStartCoordinates() {
		return this.startCoordinates;
	}

	public int getMaxElements() {
		return this.maxElements;
	}

	public int getDepth() {
		return this.depth;
	}

	public int getMaxDepth() {
		return this.maxDepth;
	}

	public abstract void subdivide();

	public abstract void clear();

	public abstract Map<Quadrant, AbstractNode<T>> getSubNodes();

}
