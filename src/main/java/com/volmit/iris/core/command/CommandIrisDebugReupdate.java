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

package com.volmit.iris.core.command;

import com.volmit.iris.Iris;
import com.volmit.iris.core.tools.IrisToolbelt;
import com.volmit.iris.engine.framework.Engine;
import com.volmit.iris.util.collection.KList;
import com.volmit.iris.util.mantle.MantleFlag;
import com.volmit.iris.util.plugin.MortarCommand;
import com.volmit.iris.util.plugin.VolmitSender;
import org.bukkit.Chunk;

public class CommandIrisDebugReupdate extends MortarCommand {
    public CommandIrisDebugReupdate() {
        super("reupdate", "rupt");
        requiresPermission(Iris.perm.studio);
        setDescription("Force update a chunk again");
        setCategory("Studio");
    }


    @Override
    public void addTabOptions(VolmitSender sender, String[] args, KList<String> list) {

    }

    @Override
    public boolean handle(VolmitSender sender, String[] args) {

        Chunk c = sender.player().getLocation().getChunk();
        Engine e =  IrisToolbelt.access(sender.player().getWorld()).getEngine();
        e.getMantle().getMantle().flag(c.getX(), c.getZ(), MantleFlag.UPDATE, false);
        e.updateChunk(c);
        return true;
    }

    @Override
    protected String getArgsUsage() {
        return "<number> [|,&,^,>>,<<,%] <other>";
    }
}
