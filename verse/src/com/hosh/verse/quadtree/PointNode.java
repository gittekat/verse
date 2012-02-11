package com.hosh.verse.quadtree;

import java.awt.Dimension;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class PointNode<T> extends AbstractNode {

	protected Map<Quadrant, PointNode<T>> children = new HashMap<Quadrant, PointNode<T>>();

	/**
	 * Holds all elements for this node
	 */
	protected Vector<PointNodeElement<T>> elements = new Vector<PointNodeElement<T>>();

	public PointNode(final Point startCoordinates, final Dimension bounds, final int depth) {
		super(startCoordinates, bounds, depth);
	}

	public PointNode(final Point startCoordinates, final Dimension bounds, final int depth, final int maxDepth, final int maxChildren) {
		super(startCoordinates, bounds, depth, maxDepth, maxChildren);
	}

	@Override
	public Map<Quadrant, PointNode<T>> getSubNodes() {
		return this.children;
	}

	protected Quadrant findIndex(final Point coordinates) {
		final boolean left = coordinates.x > startCoordinates.x + bounds.width / 2 ? false : true;
		final boolean top = coordinates.y > startCoordinates.y + bounds.height / 2 ? false : true;

		// top left
		Quadrant index = Quadrant.TOP_LEFT;
		if (left) {
			// left side
			if (!top) {
				// bottom left
				index = Quadrant.BOTTOM_LEFT;
			}
		} else {
			// right side
			if (top) {
				// top right
				index = Quadrant.TOP_RIGHT;
			} else {
				// bottom right
				index = Quadrant.BOTTOM_RIGHT;

			}
		}
		return index;
	}

	public Vector<PointNodeElement<T>> getElements() {
		return this.elements;
	}

	public Vector<PointNodeElement<T>> getElements(final Point coordinates) {
		@SuppressWarnings("unused")
		final boolean left = coordinates.x > startCoordinates.x + bounds.width / 2 ? false : true;
		@SuppressWarnings("unused")
		final boolean top = coordinates.y > startCoordinates.y + bounds.height / 2 ? false : true;

		final int leftWithRadius = Math.abs(startCoordinates.x - bounds.width / 2);

		final int radius = 2;
		final boolean left_ = coordinates.x + radius <= startCoordinates.x + bounds.width ? false : true;
		final boolean right = coordinates.x - radius >= startCoordinates.x;

		// is tree already subdivided
		if (children.size() > 0) {
			final Quadrant index = findIndex(coordinates);
			final PointNode<T> node = this.children.get(index);
			return node.getElements(coordinates);
		} else {
			return this.elements;
		}
	}

	public void insert(final PointNodeElement<T> element) {
		if (depth > maxDepth) {
			System.out.println("[DEBUG] Inserting element into Node at depth " + depth);
		}

		// subdivided tree: add to quadrant
		if (this.children.size() != 0) {
			final Quadrant index = findIndex(element);
			this.children.get(index).insert(element);
			return;
		}

		this.elements.add(element);

		// subdivide if maxElements is not exceeded and is not the deepest node
		if (!(this.depth >= maxDepth) && this.elements.size() > maxElements) {
			this.subdivide();

			// move nodes in appropriate quadrant
			for (final PointNodeElement<T> current : elements) {
				this.insert(current);
			}

			// Remove all elements
			this.elements.clear();

		}
	}

	@Override
	public void subdivide() {
		final int depth = this.depth + 1;

		final int bx = this.startCoordinates.x;
		final int by = this.startCoordinates.y;

		final Dimension newBounds = new Dimension(this.bounds.width / 2, this.bounds.height / 2);

		final int newXStartCoordinate = bx + newBounds.width;
		final int newYStartCoordinate = by + newBounds.height;

		PointNode<T> quadrantNode = null;

		// top left
		quadrantNode = new PointNode<T>(new Point(bx, by), newBounds, depth, this.maxDepth, this.maxElements);
		this.children.put(Quadrant.TOP_LEFT, quadrantNode);

		// top right
		quadrantNode = new PointNode<T>(new Point(newXStartCoordinate, by), newBounds, depth, this.maxDepth, this.maxElements);
		this.children.put(Quadrant.TOP_RIGHT, quadrantNode);

		// bottom left
		quadrantNode = new PointNode<T>(new Point(bx, newYStartCoordinate), newBounds, depth, this.maxDepth, this.maxElements);
		this.children.put(Quadrant.BOTTOM_LEFT, quadrantNode);

		// bottom right
		quadrantNode = new PointNode<T>(new Point(newXStartCoordinate, newYStartCoordinate), newBounds, depth, this.maxDepth,
				this.maxElements);
		this.children.put(Quadrant.BOTTOM_RIGHT, quadrantNode);
	}

	@Override
	public void clear() {
		for (final PointNode<T> node : children.values()) {
			node.clear();
		}
		elements.clear();
		children.clear();
	}

	public int size() {
		int size = 0;
		for (final Map.Entry<Quadrant, PointNode<T>> entry : children.entrySet()) {
			size += entry.getValue().size();
		}
		size += elements.size();
		return size;
	}
}
