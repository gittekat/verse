package com.hosh.verse.common.quadtree;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.badlogic.gdx.math.Vector2;

public class PointQuadNode<T> extends AbstractQuadNode<T> {

	private final Map<Quadrant, PointQuadNode<T>> children = new HashMap<Quadrant, PointQuadNode<T>>();
	private final Vector<PointQuadNodeElement<T>> elements = new Vector<PointQuadNodeElement<T>>();

	public PointQuadNode(final Vector2 startCoords, final Vector2 bounds, final int depth) {
		super(startCoords, bounds, depth);
	}

	public PointQuadNode(final Vector2 startCoords, final Vector2 size, final int depth, final int maxDepth, final int maxChildren) {
		super(startCoords, size, depth, maxDepth, maxChildren);
	}

	@Override
	public Map<Quadrant, PointQuadNode<T>> getChildren() {
		return children;
	}

	@Override
	public void subdivide() {
		final int depth = getDepth() + 1;
		final Vector2 newBounds = new Vector2(getSize().x / 2, getSize().y / 2);

		final int xStart = (int) getStartCoordinates().x;
		final int yStart = (int) getStartCoordinates().y;

		final int newXStart = xStart + (int) newBounds.x;
		final int newYStart = yStart + (int) newBounds.y;

		children.put(Quadrant.TOP_LEFT,
				new PointQuadNode<T>(new Vector2(xStart, yStart), newBounds, depth, getMaxDepth(), getMaxElements()));

		children.put(Quadrant.TOP_RIGHT, new PointQuadNode<T>(new Vector2(newXStart, yStart), newBounds, depth, getMaxDepth(),
				getMaxElements()));

		children.put(Quadrant.BOTTOM_LEFT, new PointQuadNode<T>(new Vector2(xStart, newYStart), newBounds, depth, getMaxDepth(),
				getMaxElements()));

		children.put(Quadrant.BOTTOM_RIGHT, new PointQuadNode<T>(new Vector2(newXStart, newYStart), newBounds, depth, getMaxDepth(),
				getMaxElements()));
	}

	@Override
	public void clear() {
		for (final PointQuadNode<T> node : children.values()) {
			node.clear();
		}
		elements.clear();
		children.clear();
	}

	protected Quadrant findQuadrant(final Vector2 coords) {
		final boolean left = coords.x > getStartCoordinates().x + getSize().x / 2 ? false : true;
		final boolean top = coords.y > getStartCoordinates().y + getSize().y / 2 ? false : true;

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

	public Vector<PointQuadNodeElement<T>> getElements(final Vector2 coordinates) {
		if (children.size() > 0) {
			final PointQuadNode<T> node = children.get(findQuadrant(coordinates));
			return node.getElements(coordinates);
		} else {
			return elements;
		}
	}

	public void insert(final PointQuadNodeElement<T> element) {
		if (getDepth() > getMaxDepth()) {
			System.out.println("[DEBUG] Inserting element into Node at depth " + getDepth());
		}

		// tree is further subdivided -> add element to quadrant
		if (children.size() > 0) {
			children.get(findQuadrant(element.getCoordinates())).insert(element);
			return;
		}

		elements.add(element);

		if (getDepth() < getMaxDepth() && elements.size() > getMaxElements()) {
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
