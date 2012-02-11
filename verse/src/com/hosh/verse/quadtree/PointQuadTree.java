package com.hosh.verse.quadtree;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Vector;

public class PointQuadTree<T> extends AbstractQuadTree<T> {

	protected PointNode<T> rootNode;

	public PointQuadTree(final Point startCoordinates, final Dimension size) {
		super(startCoordinates, size);
		this.rootNode = new PointNode<T>(startCoordinates, size, 0);
	}

	public PointQuadTree(final Point startCoordinates, final Dimension size, final int maxDepth, final int maxChildren) {
		super(startCoordinates, size);
		this.rootNode = new PointNode<T>(startCoordinates, size, 0, maxDepth, maxChildren);
	}

	public void insert(final int x, final int y, final T element) {
		insert(new Point(x, y), element);
	}

	public void insert(final Point point, final Dimension size, final T element) {

		// Check bounds
		if (point.x > startCoordinates.x + size.width || point.x < startCoordinates.x) {
			throw new IndexOutOfBoundsException("The x coordinate must be within bounds of [" + startCoordinates.x + "] to [" + size.width
					+ "]");
		}
		if (point.y > startCoordinates.y + size.height || point.y < startCoordinates.y) {
			throw new IndexOutOfBoundsException("The y coordinate must be within bounds of [" + startCoordinates.y + "] to [" + size.height
					+ "]");
		}

		// Check right bottom
		if (point.x + size.width > startCoordinates.x + size.width || point.x < startCoordinates.x) {
			throw new IndexOutOfBoundsException("The x coordinate must be within bounds of [" + startCoordinates.x + "] to [" + size.width
					+ "]");
		}
		if (point.y + size.width > startCoordinates.y + size.height || point.y < startCoordinates.y) {
			throw new IndexOutOfBoundsException("The y coordinate must be within bounds of [" + startCoordinates.y + "] to [" + size.height
					+ "]");
		}

		this.rootNode.insert(new PointNodeElement<T>(point, element));

	}

	public void insert(final Point point, final T element) {

		// Check bounds
		if (point.x > startCoordinates.x + size.width || point.x < startCoordinates.x) {
			throw new IndexOutOfBoundsException("The x coordinate must be within bounds of [" + startCoordinates.x + "] to [" + size.width
					+ "]");
		}
		if (point.y > startCoordinates.y + size.height || point.y < startCoordinates.y) {
			throw new IndexOutOfBoundsException("The y coordinate must be within bounds of [" + startCoordinates.y + "] to [" + size.height
					+ "]");
		}

		this.rootNode.insert(new PointNodeElement<T>(point, element));
	}

	@Override
	public PointNode<T> getRootNode() {
		return this.rootNode;
	}

	public Vector<? extends AbstractNodeElement<T>> getElements(final Point coordinates) {
		return this.rootNode.getElements(coordinates);
	}

	@Override
	public void clear() {
		this.rootNode.clear();
	}

	public int size() {
		return rootNode.size();
	}
}
