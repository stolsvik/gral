/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009, by Erich Seifert and Michael Seifert.
 *
 * This file is part of OpenJChart.
 *
 * OpenJChart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenJChart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenJChart.  If not, see <http://www.gnu.org/licenses/>.
 */

package openjchart.plots.axes;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import openjchart.plots.DataPoint2D;
import openjchart.util.MathUtils;

/**
 * Class that renders 2-dimensional axes with a logarithmic scale.
 */
public class LogarithmicRenderer2D extends AbstractAxisRenderer2D {

	/**
	 * Creates a new LogarithmicRenderer2D object.
	 */
	public LogarithmicRenderer2D() {
	}

	@Override
	public double worldToView(Axis axis, Number value, boolean extrapolate) {
		checkAxisBounds(axis);
		double min = axis.getMin().doubleValue();
		double max = axis.getMax().doubleValue();
		double val = value.doubleValue();
		if (!extrapolate) {
			if (val <= min) {
				return 0.0;
			}
			if (val >= max) {
				return getShapeLength();
			}
		}
		double minLog = (min > 0.0) ? Math.log10(min) : 0.0;
		double maxLog = (max > 0.0) ? Math.log10(max) : 1.0;
		return (Math.log10(value.doubleValue()) - minLog)*getShapeLength() / (maxLog - minLog);
	}

	@Override
	public Number viewToWorld(Axis axis, double value, boolean extrapolate) {
		checkAxisBounds(axis);
		double min = axis.getMin().doubleValue();
		double max = axis.getMax().doubleValue();
		if (!extrapolate) {
			if (value <= 0.0) {
				return min;
			}
			if (value >= getShapeLength()) {
				return max;
			}
		}
		double minLog = (min > 0.0) ? Math.log10(min) : 0.0;
		double maxLog = (max > 0.0) ? Math.log10(max) : 1.0;
		return Math.pow(10.0, value*(maxLog - minLog)/getShapeLength() + minLog);
	}

	@Override
	public List<DataPoint2D> getTicks(Axis axis) {
		checkAxisBounds(axis);
		double tickSpacing = getSetting(KEY_TICK_SPACING);
		double min = axis.getMin().doubleValue();
		double max = axis.getMax().doubleValue();

		final double BASE = 10.0;
		double powerMin = Math.pow(BASE, Math.floor(Math.log10(min)));
		double powerMax = Math.pow(BASE, Math.floor(Math.log10(max)));

		// Add custom ticks
		List<DataPoint2D> ticks = new LinkedList<DataPoint2D>();
		ticks.addAll(getCustomTicks(axis));
		Set<Double> tickPositions = new HashSet<Double>();
		Set<Double> tickPositionsCustom = getTickPositionsCustom();
		// Add standard ticks
		for (double power = powerMin; power <= powerMax; power *= BASE) {
			double step = power*tickSpacing;
			double powerNext = power*BASE;
			for (double tickPositionWorld = step; tickPositionWorld <= powerNext;
					tickPositionWorld = MathUtils.round(tickPositionWorld + step, 1e-14)) {
				if (tickPositionWorld < min) {
					continue;
				} else if (tickPositionWorld > max) {
					break;
				}
				DataPoint2D tick = getTick(axis, tickPositionWorld);
				if (tick.getPosition() != null
						&& !tickPositions.contains(tickPositionWorld)
						&& !tickPositionsCustom.contains(tickPositionWorld)) {
					ticks.add(tick);
					tickPositions.add(tickPositionWorld);
				}
			}
		}

		return ticks;
	}

	private static void checkAxisBounds(Axis axis) {
		if (axis.getMin().doubleValue() <= 0.0 || axis.getMax().doubleValue() <= 0.0) {
			throw new IllegalStateException("All axis bounds must be greater than zero for a logarithmic axis renderer.");
		}
	}

}
