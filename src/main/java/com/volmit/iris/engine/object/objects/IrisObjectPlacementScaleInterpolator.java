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

package com.volmit.iris.engine.object.objects;

import com.volmit.iris.engine.object.annotations.Desc;

@Desc("Use 3D Interpolation on scaled objects if they are larger than the origin.")
public enum IrisObjectPlacementScaleInterpolator {
    @Desc("Don't interpolate, big cubes")
    NONE,
    @Desc("Uses linear interpolation in 3 dimensions, generally pretty good, but slow")
    TRILINEAR,
    @Desc("Uses cubic spline interpolation in 3 dimensions, even better, but extreme slowdowns")
    TRICUBIC,
    @Desc("Uses hermite spline interpolation in 3 dimensions, even better, but extreme slowdowns")
    TRIHERMITE
}
