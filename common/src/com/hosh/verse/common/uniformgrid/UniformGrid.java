package com.hosh.verse.common.uniformgrid;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.google.common.eventbus.Subscribe;
import com.hosh.verse.common.CollisionChecker;
import com.hosh.verse.common.IPositionable;

public class UniformGrid {
	private final int dimensionX;
	private final int dimensionY;

	private final int gridSize;
	private final int gridX;
	private final int gridY;

	// id x entity
	private Map<Integer, IPositionable> idMapper; // TODO really needed?

	// entity x mortonNumber
	private Map<IPositionable, Integer> mortonMapper;

	// mortonNumber x [bucket: id x entity]
	private Map<Integer, Map<Integer, IPositionable>> buckets;

	private Logger logger;

	public UniformGrid(int dimensionX, int dimensionY, final int gridSize) {
		logger = LoggerFactory.getLogger(UniformGrid.class);

		this.gridSize = gridSize;

		if (dimensionX % gridSize != 0) {
			dimensionX = (dimensionX / gridSize + 1) * gridSize;
			logger.info("dimensionX increased to the next power of {}", gridSize);
		}
		if (dimensionY % gridSize != 0) {
			dimensionY = (dimensionY / gridSize + 1) * gridSize;
		}

		this.dimensionX = dimensionX;
		this.dimensionY = dimensionY;

		gridX = dimensionX / gridSize;
		gridY = dimensionY / gridSize;

		idMapper = new HashMap<Integer, IPositionable>();
		mortonMapper = new HashMap<IPositionable, Integer>();
		buckets = new HashMap<Integer, Map<Integer, IPositionable>>();
	}

	public void getEntities(final Rectangle rect) {

	}

	public void addEntity(final IPositionable entity) {
		if (entity.getPos().x >= getDimensionX() || entity.getPos().y >= getDimensionY()) {
			logger.error("entity could not be inserted (out of bounds): {} x {}", entity.getPos().x, entity.getPos().y);
			return;
		}

		idMapper.put(entity.getId(), entity);

		final int mortonNumber = getMortonNumber(entity.getPos());
		addToMortonMapper(entity, mortonNumber);

		addToBuckets(entity, mortonNumber);
	}

	private void addToMortonMapper(final IPositionable entity, final Integer mortonNumber) {
		mortonMapper.put(entity, mortonNumber);
	}

	private void addToBuckets(final IPositionable entity, final int mortonNumber) {
		Map<Integer, IPositionable> bucket;
		if (buckets.containsKey(mortonNumber)) {
			bucket = buckets.get(mortonNumber);
		} else {
			bucket = new HashMap<Integer, IPositionable>();
		}
		bucket.put(entity.getId(), entity);
		buckets.put(mortonNumber, bucket);
	}

	public void removeEntity(final IPositionable entity) {
		if (idMapper.containsKey(entity.getId()) && mortonMapper.containsValue(entity)) {
			idMapper.remove(entity.getId());
			mortonMapper.remove(entity);

			final int mortonNumber = getMortonNumber(entity.getPos());
			final Map<Integer, IPositionable> bucket = buckets.get(mortonNumber);
			bucket.remove(entity);
		} else {
			logger.error("removeEntity: entity not found!");
		}
	}

	@Subscribe
	public void updateEntity(final IPositionable entity) {
		final Integer oldMortonNumber = mortonMapper.get(entity);
		final int newMortonNumber = getMortonNumber(entity.getPos());

		if (oldMortonNumber == newMortonNumber) {
			return;
		}

		mortonMapper.put(entity, newMortonNumber);

		final Map<Integer, IPositionable> bucket = buckets.get(oldMortonNumber);
		bucket.remove(oldMortonNumber);

		addToBuckets(entity, newMortonNumber);
	}

	public IPositionable getEntity(final int id) {
		return idMapper.get(id);
	}

	public int getMortonNumber(final Vector2 pos) {
		return CollisionChecker.mortonNumber((int) pos.x / gridSize, (int) pos.y / gridSize);
	}

	public boolean checkIntegrity() {
		logger.info("{} objects", idMapper.size());
		logger.info("{} objects", mortonMapper.size());
		logger.info("in {}/{} buckets", buckets.size(), getGridX() * getGridY());

		if (buckets.size() > getGridX() * getGridY()) {
			logger.error("too many buckets!");
			return false;
		}

		for (final Map.Entry<Integer, IPositionable> entry : idMapper.entrySet()) {
			final int mortonNumber = getMortonNumber(entry.getValue().getPos());
			final Map<Integer, IPositionable> bucket = buckets.get(mortonNumber);
			if (bucket == null) {
				return false;
			}
			final IPositionable obj = bucket.get(entry.getKey());
			if (obj == null) {
				return false;
			}
		}

		for (final Map.Entry<IPositionable, Integer> entry : mortonMapper.entrySet()) {
			final Map<Integer, IPositionable> bucket = buckets.get(entry.getValue());
			if (bucket == null) {
				return false;
			}

			final IPositionable obj = bucket.get(entry.getKey().getId());
			if (obj == null) {
				return false;
			}
		}

		int inBuckets = 0;
		for (final Map<Integer, IPositionable> bucket : buckets.values()) {
			inBuckets += bucket.size();
		}

		if (inBuckets != idMapper.size()) {
			return false;
		}

		if (inBuckets != mortonMapper.size()) {
			return false;
		}
		return true;
	}

	public Collection<IPositionable> getEntities() {
		return idMapper.values();
	}

	public Set<IPositionable> getEntities2() {
		return mortonMapper.keySet();
	}

	public int size() {
		return idMapper.size();
	}

	public int getDimensionX() {
		return dimensionX;
	}

	public int getDimensionY() {
		return dimensionY;
	}

	public int getGridSize() {
		return gridSize;
	}

	public int getGridX() {
		return gridX;
	}

	public int getGridY() {
		return gridY;
	}

	// DEBUG
	public int getMortonNumber(final IPositionable entity) {
		return mortonMapper.get(entity);
	}
}
