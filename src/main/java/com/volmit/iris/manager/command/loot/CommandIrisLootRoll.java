package com.volmit.iris.manager.command.loot;

import com.volmit.iris.Iris;
import com.volmit.iris.util.KList;
import com.volmit.iris.util.MortarCommand;
import com.volmit.iris.util.MortarSender;

public class CommandIrisLootRoll extends MortarCommand {


    public CommandIrisLootRoll() {
        super("roll", "r");
        requiresPermission(Iris.perm);
        setCategory("Loot");
        setDescription("Review what a roll of the loot table will look like");
    }

    @Override
    public boolean handle(MortarSender sender, String[] args) {
        return false;
    }

    @Override
    public void addTabOptions(MortarSender sender, String[] args, KList<String> list) {

    }

    @Override
    protected String getArgsUsage() {
        return null;
    }
}
