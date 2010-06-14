/*
 * GRAL: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2009-2010 Erich Seifert <info[at]erichseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.erichseifert.gral.plots.areas;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Area;

import de.erichseifert.gral.plots.DataPoint2D;
import de.erichseifert.gral.util.GeometryUtils;
import de.erichseifert.gral.util.SettingChangeEvent;
import de.erichseifert.gral.util.Settings;
import de.erichseifert.gral.util.SettingsListener;
import de.erichseifert.gral.util.Settings.Key;

/**
 * Abstract class that renders an area in two-dimensional space.
 * Functionality includes:
 * <ul>
 * <li>Punching data points out of the area's shape</li>
 * <li>Administration of settings</li>
 * </ul>
 */
public abstract class AbstractAreaRenderer2D implements AreaRenderer2D, SettingsListener {
	private final Settings settings;

	/**
	 * Creates a new AbstractLineRenderer2D with default settings.
	 */
	public AbstractAreaRenderer2D() {
		this.settings = new Settings(this);

		setSettingDefault(GAP, 0.0);
		setSettingDefault(GAP_ROUNDED, false);
		setSettingDefault(COLOR, Color.GRAY);
	}

	/**
	 * Returns the shape of an area from which the shapes of the specified
	 * points are subtracted.
	 * @param area Shape of the area.
	 * @param dataPoints Data points on the line.
	 * @return Punched shape.
	 */
	protected Shape punch(Shape area, Iterable<DataPoint2D> dataPoints) {
		// Subtract shapes of data points from the area to yield gaps.
		double gapSize = this.<Double>getSetting(GAP);
		boolean isGapRounded = this.<Boolean>getSetting(GAP_ROUNDED);
		Area punched = GeometryUtils.punch(area, gapSize, isGapRounded, dataPoints);

		return punched;
	}

	@Override
	public <T> T getSetting(Key key) {
		return settings.<T>get(key);
	}

	@Override
	public <T> void setSetting(Key key, T value) {
		settings.<T>set(key, value);
	}

	@Override
	public <T> void removeSetting(Key key) {
		settings.remove(key);
	}

	@Override
	public <T> void setSettingDefault(Key key, T value) {
		settings.<T>setDefault(key, value);
	}

	@Override
	public <T> void removeSettingDefault(Key key) {
		settings.removeDefault(key);
	}

	@Override
	public void settingChanged(SettingChangeEvent event) {
	}

}