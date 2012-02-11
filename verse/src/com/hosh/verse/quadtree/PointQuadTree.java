package com.hosh.verse.quadtree;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Vector;

import com.google.common.base.Preconditions;

public class PointQuadTree<T> extends AbstractQuadTree<T> {

	private PointQuadNode<T> rootNode;

	public PointQuadTree(final Point startCoordinates, final Dimension size) {
		super(startCoordinates, size);
		rootNode = new PointQuadNode<T>(startCoordinates, size, 0);
	}

	public PointQuadTree(final Point startCoordinates, final Dimension size, final int maxDepth, final int maxChildren) {
		super(startCoordinates, size);
		rootNode = new PointQuadNode<T>(startCoordinates, size, 0, maxDepth, maxChildren);
	}

	public void insert(final int x, final int y, final T element) {
		insert(new Point(x, y), element);
	}

	public void insert(final Point coords, final T element) {
		Preconditions.checkArgument(coords.x <= getStartCoordinates().x + getSize().width && coords.x >= getStartCoordinates().x,
				"The x coordinate must be within bounds of the x starting coordinate and the rightmost border of the element");
		Preconditions.checkArgument(coords.y <= getStartCoordinates().y + getSize().height && coords.y >= getStartCoordinates().y,
				"The y coordinate must be within bounds of the y starting coordinate and the topmost border of the element");
		rootNode.insert(new PointQuadNodeElement<T>(coords, element));
	}

	@Override
	public PointQuadNode<T> getRootNode() {
		return rootNode;
	}

	public Vector<? extends AbstractQuadNodeElement<T>> getElements(final Point coordinates) {
		return rootNode.getElements(coordinates);
	}

	@Override
	public void clear() {
		rootNode.clear();
	}

	/**
	 * Returns the total number of elements in the tree.
	 */
	public int size() {
		return getTotalChildCount(rootNode);
	}

	private int getTotalChildCount(final PointQuadNode<T> node) {
		int count = node.getChildCount();
		for (final PointQuadNode<T> child : node.getChildren().values()) {
			count += getTotalChildCount(child);
		}
		return count;
	}

}
