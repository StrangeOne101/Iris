package com.volmit.iris.manager.command.loot;

import com.volmit.iris.Iris;
import com.volmit.iris.util.KList;
import com.volmit.iris.util.MortarCommand;
import com.volmit.iris.util.MortarSender;

public class CommandIrisLoot extends MortarCommand {



    public CommandIrisLoot() {
        super("loot", "l", "lt");
        requiresPermission(Iris.perm);
        setCategory("Loot");
        setCategory("Iris loot commands");
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
