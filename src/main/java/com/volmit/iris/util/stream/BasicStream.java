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

package com.volmit.iris.util.stream;

public abstract class BasicStream<T> extends BasicLayer implements ProceduralStream<T> {
    private final ProceduralStream<T> source;

    public BasicStream(ProceduralStream<T> source) {
        super();
        this.source = source;
    }

    public BasicStream() {
        this(null);
    }


    @Override
    public ProceduralStream<T> getTypedSource() {
        return source;
    }

    @Override
    public ProceduralStream<?> getSource() {
        return getTypedSource();
    }

    @Override
    public abstract T get(double x, double z);

    @Override
    public abstract T get(double x, double y, double z);

    @Override
    public abstract double toDouble(T t);

    @Override
    public abstract T fromDouble(double d);
}
