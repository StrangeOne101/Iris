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

package com.volmit.iris.engine.object.biome;

import com.volmit.iris.engine.object.annotations.Desc;

@Desc("Represents a biome type")
public enum InferredType {
    @Desc("Represents any shore biome type")
    SHORE,

    @Desc("Represents any land biome type")
    LAND,

    @Desc("Represents any sea biome type")
    SEA,

    @Desc("Represents any cave biome type")
    CAVE,

    @Desc("Represents any river biome type")
    RIVER,

    @Desc("Represents any lake biome type")
    LAKE,

    @Desc("Defers the type to whatever another biome type that already exists is.")
    DEFER
}
