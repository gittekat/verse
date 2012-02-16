package com.hosh.verse.quadtree;

import java.util.Map;

import com.badlogic.gdx.math.Vector2;

public abstract class AbstractQuadNode<T> {

	protected final static int MAX_ELEMENTS = 4;
	protected final static int MAX_DEPTH = 4;
	private final Vector2 size;
	private final Vector2 startCoords;
	private final int maxDepth;
	private final int maxElements;
	private final int depth;

	public static enum Quadrant {
		TOP_LEFT, BOTTOM_RIGHT, BOTTOM_LEFT, TOP_RIGHT
	}

	public AbstractQuadNode(final Vector2 startCoordinates, final Vector2 bounds, final int depth) {
		this(startCoordinates, bounds, depth, MAX_ELEMENTS, MAX_DEPTH);
	}

	public AbstractQuadNode(final Vector2 startCoordinates, final Vector2 size, final int depth, final int maxDepth, final int maxElements) {
		this.startCoords = startCoordinates;
		this.size = size;
		this.maxDepth = maxDepth;
		this.maxElements = maxElements;
		this.depth = depth;
	}

	public Vector2 getSize() {
		return size;
	}

	public int getMaxDepth() {
		return this.maxDepth;
	}

	public int getDepth() {
		return this.depth;
	}

	public Vector2 getStartCoordinates() {
		return startCoords;
	}

	public int getMaxElements() {
		return maxElements;
	}

	public abstract void subdivide();

	public abstract Map<Quadrant, ? extends AbstractQuadNode<T>> getChildren();

	public abstract void clear();

}
