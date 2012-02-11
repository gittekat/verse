package com.hosh.verse.quadtree;

import java.awt.Dimension;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class PointQuadNode<T> extends AbstractQuadNode<T> {

	protected final Map<Quadrant, PointQuadNode<T>> children = new HashMap<Quadrant, PointQuadNode<T>>();
	protected final Vector<PointQuadNodeElement<T>> elements = new Vector<PointQuadNodeElement<T>>();

	public PointQuadNode(final Point startCoords, final Dimension bounds, final int depth) {
		super(startCoords, bounds, depth);
	}

	public PointQuadNode(final Point startCoords, final Dimension size, final int depth, final int maxDepth, final int maxChildren) {
		super(startCoords, size, depth, maxDepth, maxChildren);
	}

	@Override
	public Map<Quadrant, PointQuadNode<T>> getChildren() {
		return children;
	}

	@Override
	public void subdivide() {
		final int depth = this.depth + 1;
		final Dimension newBounds = new Dimension(size.width / 2, size.height / 2);

		final int xStart = startCoords.x;
		final int yStart = startCoords.y;

		final int newXStart = xStart + newBounds.width;
		final int newYStart = yStart + newBounds.height;

		children.put(Quadrant.TOP_LEFT, new PointQuadNode<T>(new Point(xStart, yStart), newBounds, depth, maxDepth, maxElements));

		children.put(Quadrant.TOP_RIGHT, new PointQuadNode<T>(new Point(newXStart, yStart), newBounds, depth, maxDepth, maxElements));

		children.put(Quadrant.BOTTOM_LEFT, new PointQuadNode<T>(new Point(xStart, newYStart), newBounds, depth, maxDepth, maxElements));

		children.put(Quadrant.BOTTOM_RIGHT, new PointQuadNode<T>(new Point(newXStart, newYStart), newBounds, depth, maxDepth, maxElements));
	}

	@Override
	public void clear() {
		for (final PointQuadNode<T> node : children.values()) {
			node.clear();
		}
		elements.clear();
		children.clear();
	}

	protected Quadrant findQuadrant(final Point coords) {
		final boolean left = coords.x > startCoords.x + size.width / 2 ? false : true;
		final boolean top = coords.y > startCoords.y + size.height / 2 ? false : true;

		if (left) {
			if (top) {
				return Quadrant.TOP_LEFT;
			} else {
				return Quadrant.BOTTOM_LEFT;
			}
		} else {
			if (top) {
				return Quadrant.TOP_RIGHT;
			} else {
				return Quadrant.BOTTOM_RIGHT;
			}
		}
	}

	public Vector<PointQuadNodeElement<T>> getElements() {
		return elements;
	}

	public Vector<PointQuadNodeElement<T>> getElements(final Point coordinates) {
		if (children.size() > 0) {
			final PointQuadNode<T> node = children.get(findQuadrant(coordinates));
			return node.getElements(coordinates);
		} else {
			return elements;
		}
	}

	public void insert(final PointQuadNodeElement<T> element) {
		if (depth > maxDepth) {
			System.out.println("[DEBUG] Inserting element into Node at depth " + depth);
		}

		// tree is further subdivided -> add element to quadrant
		if (children.size() > 0) {
			children.get(findQuadrant(element.getCoordinates())).insert(element);
			return;
		}

		elements.add(element);

		if (depth < maxDepth && elements.size() > maxElements) {
			subdivide();

			// move all nodes to appropriate quadrant
			for (final PointQuadNodeElement<T> node : elements) {
				insert(node);
			}

			elements.clear();
		}
	}

	/**
	 * Returns the number of child elements of the node.
	 */
	public int getChildCount() {
		return elements.size();
	}
}
