/*
 * Iris is a World Generator for Minecraft Bukkit Servers
 * Copyright (c) 2021 Arcane Arts (Volmit Software)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.volmit.iris.util.stream.interpolation;

import com.volmit.iris.util.interpolation.IrisInterpolation;
import com.volmit.iris.util.stream.BasicStream;
import com.volmit.iris.util.stream.ProceduralStream;

public class TriHermiteStream<T> extends BasicStream<T> implements Interpolator<T> {
    private final int rx;
    private final int ry;
    private final int rz;
    private final double tension;
    private final double bias;

    public TriHermiteStream(ProceduralStream<T> stream, int rx, int ry, int rz, double tension, double bias) {
        super(stream);
        this.rx = rx;
        this.ry = ry;
        this.rz = rz;
        this.tension = tension;
        this.bias = bias;
    }

    public T interpolate(double x, double y, double z) {
        int fx = (int) Math.floor(x / rx);
        int fy = (int) Math.floor(y / ry);
        int fz = (int) Math.floor(z / rz);
        int x0 = Math.round((fx - 1) * rx);
        int y0 = Math.round((fy - 1) * ry);
        int z0 = Math.round((fz - 1) * rz);
        int x1 = Math.round(fx * rx);
        int y1 = Math.round(fy * ry);
        int z1 = Math.round(fz * rz);
        int x2 = Math.round((fx + 1) * rx);
        int y2 = Math.round((fy + 1) * ry);
        int z2 = Math.round((fz + 1) * rz);
        int x3 = Math.round((fx + 2) * rx);
        int y3 = Math.round((fy + 2) * ry);
        int z3 = Math.round((fz + 2) * rz);
        double px = IrisInterpolation.rangeScale(0, 1, x1, x2, x);
        double py = IrisInterpolation.rangeScale(0, 1, y1, y2, y);
        double pz = IrisInterpolation.rangeScale(0, 1, z1, z2, z);

        //@builder
        return getTypedSource().fromDouble(IrisInterpolation.trihermite(
                getTypedSource().getDouble(x0, y0, z0),
                getTypedSource().getDouble(x0, y0, z1),
                getTypedSource().getDouble(x0, y0, z2),
                getTypedSource().getDouble(x0, y0, z3),
                getTypedSource().getDouble(x1, y0, z0),
                getTypedSource().getDouble(x1, y0, z1),
                getTypedSource().getDouble(x1, y0, z2),
                getTypedSource().getDouble(x1, y0, z3),
                getTypedSource().getDouble(x2, y0, z0),
                getTypedSource().getDouble(x2, y0, z1),
                getTypedSource().getDouble(x2, y0, z2),
                getTypedSource().getDouble(x2, y0, z3),
                getTypedSource().getDouble(x3, y0, z0),
                getTypedSource().getDouble(x3, y0, z1),
                getTypedSource().getDouble(x3, y0, z2),
                getTypedSource().getDouble(x3, y0, z3),
                getTypedSource().getDouble(x0, y1, z0),
                getTypedSource().getDouble(x0, y1, z1),
                getTypedSource().getDouble(x0, y1, z2),
                getTypedSource().getDouble(x0, y1, z3),
                getTypedSource().getDouble(x1, y1, z0),
                getTypedSource().getDouble(x1, y1, z1),
                getTypedSource().getDouble(x1, y1, z2),
                getTypedSource().getDouble(x1, y1, z3),
                getTypedSource().getDouble(x2, y1, z0),
                getTypedSource().getDouble(x2, y1, z1),
                getTypedSource().getDouble(x2, y1, z2),
                getTypedSource().getDouble(x2, y1, z3),
                getTypedSource().getDouble(x3, y1, z0),
                getTypedSource().getDouble(x3, y1, z1),
                getTypedSource().getDouble(x3, y1, z2),
                getTypedSource().getDouble(x3, y1, z3),
                getTypedSource().getDouble(x0, y2, z0),
                getTypedSource().getDouble(x0, y2, z1),
                getTypedSource().getDouble(x0, y2, z2),
                getTypedSource().getDouble(x0, y2, z3),
                getTypedSource().getDouble(x1, y2, z0),
                getTypedSource().getDouble(x1, y2, z1),
                getTypedSource().getDouble(x1, y2, z2),
                getTypedSource().getDouble(x1, y2, z3),
                getTypedSource().getDouble(x2, y2, z0),
                getTypedSource().getDouble(x2, y2, z1),
                getTypedSource().getDouble(x2, y2, z2),
                getTypedSource().getDouble(x2, y2, z3),
                getTypedSource().getDouble(x3, y2, z0),
                getTypedSource().getDouble(x3, y2, z1),
                getTypedSource().getDouble(x3, y2, z2),
                getTypedSource().getDouble(x3, y2, z3),
                getTypedSource().getDouble(x0, y3, z0),
                getTypedSource().getDouble(x0, y3, z1),
                getTypedSource().getDouble(x0, y3, z2),
                getTypedSource().getDouble(x0, y3, z3),
                getTypedSource().getDouble(x1, y3, z0),
                getTypedSource().getDouble(x1, y3, z1),
                getTypedSource().getDouble(x1, y3, z2),
                getTypedSource().getDouble(x1, y3, z3),
                getTypedSource().getDouble(x2, y3, z0),
                getTypedSource().getDouble(x2, y3, z1),
                getTypedSource().getDouble(x2, y3, z2),
                getTypedSource().getDouble(x2, y3, z3),
                getTypedSource().getDouble(x3, y3, z0),
                getTypedSource().getDouble(x3, y3, z1),
                getTypedSource().getDouble(x3, y3, z2),
                getTypedSource().getDouble(x3, y3, z3),
                px, pz, py, tension, bias));
        //@done
    }

    @Override
    public double toDouble(T t) {
        return getTypedSource().toDouble(t);
    }

    @Override
    public T fromDouble(double d) {
        return getTypedSource().fromDouble(d);
    }

    @Override
    public T get(double x, double z) {
        return interpolate(x, 0, z);
    }

    @Override
    public T get(double x, double y, double z) {
        return interpolate(x, y, z);
    }
}
