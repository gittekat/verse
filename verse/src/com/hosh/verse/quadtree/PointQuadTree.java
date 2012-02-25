package com.hosh.verse.quadtree;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import com.badlogic.gdx.math.Vector2;
import com.google.common.base.Preconditions;

public class PointQuadTree<T> extends AbstractQuadTree<T> {

	private PointQuadNode<T> rootNode;

	public PointQuadTree(final Vector2 startCoordinates, final Vector2 size) {
		super(startCoordinates, size);
		rootNode = new PointQuadNode<T>(startCoordinates, size, 0);
	}

	public PointQuadTree(final Vector2 startCoordinates, final Vector2 size, final int maxDepth, final int maxChildren) {
		super(startCoordinates, size);
		rootNode = new PointQuadNode<T>(startCoordinates, size, 0, maxDepth, maxChildren);
	}

	public void insert(final int x, final int y, final T element) {
		insert(new Vector2(x, y), element);
	}

	public void insert(final Vector2 coords, final T element) {
		Preconditions.checkArgument(coords.x <= getStartCoordinates().x + getSize().x && coords.x >= getStartCoordinates().x,
				"The x coordinate must be within bounds of the x starting coordinate and the rightmost border of the element");
		Preconditions.checkArgument(coords.y <= getStartCoordinates().y + getSize().y && coords.y >= getStartCoordinates().y,
				"The y coordinate must be within bounds of the y starting coordinate and the topmost border of the element");
		rootNode.insert(new PointQuadNodeElement<T>(coords, element));
	}

	@Override
	public PointQuadNode<T> getRootNode() {
		return rootNode;
	}

	public Vector<? extends AbstractQuadNodeElement<T>> getElements(final Vector2 coordinates) {
		return rootNode.getElements(coordinates);
	}

	public Set<T> getElements(final int posX, final int posY, final int radius) {
		final Vector<? extends AbstractQuadNodeElement<T>> e1 = getElements(new Vector2(posX - radius, posY - radius));
		final Vector<? extends AbstractQuadNodeElement<T>> e2 = getElements(new Vector2(posX - radius, posY + radius));
		final Vector<? extends AbstractQuadNodeElement<T>> e3 = getElements(new Vector2(posX + radius, posY - radius));
		final Vector<? extends AbstractQuadNodeElement<T>> e4 = getElements(new Vector2(posX + radius, posY + radius));

		final Set<T> visibleActors = new HashSet<T>();
		for (final AbstractQuadNodeElement<T> e : e1) {
			visibleActors.add(e.getElement());
		}
		for (final AbstractQuadNodeElement<T> e : e2) {
			visibleActors.add(e.getElement());
		}
		for (final AbstractQuadNodeElement<T> e : e3) {
			visibleActors.add(e.getElement());
		}
		for (final AbstractQuadNodeElement<T> e : e4) {
			visibleActors.add(e.getElement());
		}

		return visibleActors;
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
