package openjchart.data;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import openjchart.data.statistics.Statistics;

/**
 * Abstract implementation of DataSource.
 * This class provides:
 * <ul>
 * <li>the administration of DataListeners</li>
 * <li>the administration of the Statistics capabilities</li>
 * <li>capability of notification of DataListeners</li>
 * <li>a basic iterator</li>
 * </ul>
 */
public abstract class AbstractDataSource implements DataSource {
	private final Set<DataListener> dataListeners;
	private Statistics statistics;

	/**
	 * Iterator that returns each row of the DataSource.
	 */
	private class DataSourceIterator implements Iterator<Number[]> {
		private int row;

		@Override
		public boolean hasNext() {
			return row < getRowCount();
		}

		@Override
		public Number[] next() {
			return get(row++);
		}

		@Override
		public void remove() {
		}
	}

	/**
	 * Constructor.
	 */
	public AbstractDataSource() {
		dataListeners = new HashSet<DataListener>();
	}

	@Override
	public Statistics getStatistics() {
		if (statistics == null) {
			statistics = new Statistics(this);
		}
		return statistics;
	}

	@Override
	public void addDataListener(DataListener dataListener) {
		dataListeners.add(dataListener);
	}

	@Override
	public void removeDataListener(DataListener dataListener) {
		dataListeners.remove(dataListener);
	}

	@Override
	public Iterator<Number[]> iterator() {
		return new DataSourceIterator();
	}

	/**
	 * Notifies all DataListeners that the data of this DataSource has changed.
	 */
	protected void notifyDataChanged() {
		for (DataListener dataListener : dataListeners) {
			dataListener.dataChanged(this);
		}
	}

}