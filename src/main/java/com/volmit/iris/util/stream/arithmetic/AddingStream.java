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

package com.volmit.iris.util.stream.arithmetic;

import com.volmit.iris.util.function.Function2;
import com.volmit.iris.util.function.Function3;
import com.volmit.iris.util.stream.BasicStream;
import com.volmit.iris.util.stream.ProceduralStream;

public class AddingStream<T> extends BasicStream<T> {
    private final Function3<Double, Double, Double, Double> add;

    public AddingStream(ProceduralStream<T> stream, Function3<Double, Double, Double, Double> add) {
        super(stream);
        this.add = add;
    }

    public AddingStream(ProceduralStream<T> stream, Function2<Double, Double, Double> add) {
        this(stream, (x, y, z) -> add.apply(x, z));
    }

    public AddingStream(ProceduralStream<T> stream, double add) {
        this(stream, (x, y, z) -> add);
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
        return fromDouble(add.apply(x, 0D, z) + getTypedSource().getDouble(x, z));
    }

    @Override
    public T get(double x, double y, double z) {
        return fromDouble(add.apply(x, y, z) + getTypedSource().getDouble(x, y, z));
    }

}
