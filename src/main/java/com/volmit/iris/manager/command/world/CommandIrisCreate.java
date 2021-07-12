package com.volmit.iris.manager.command.world;

import com.volmit.iris.Iris;
import com.volmit.iris.IrisSettings;
import com.volmit.iris.generator.IrisWorldManager;
import com.volmit.iris.manager.IrisDataManager;
import com.volmit.iris.manager.link.MultiverseCoreLink;
import com.volmit.iris.object.IrisDimension;
import com.volmit.iris.pregen.Pregenerator;
import com.volmit.iris.scaffold.IrisWorldCreator;
import com.volmit.iris.scaffold.engine.IrisAccess;
import com.volmit.iris.util.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class CommandIrisCreate extends MortarCommand
{
	public CommandIrisCreate()
	{
		super("create", "c", "cr", "new", "+");
		requiresPermission(Iris.perm.studio);
		setCategory("Create");
		setDescription("Create a new Iris World!");
	}

	@Override
	public void addTabOptions(MortarSender sender, String[] args, KList<String> list) {
		if (args.length == 0 || args[args.length - 1].equals("")) { //They are about to type a new argument
			list.addAll(getBase(args));
			return;
		}

		String[] split = args[args.length - 1].split("\\Q=\\E");
		if (split.length == 0) { //They haven't typed the = yet so just keep the options there
			list.addAll(getBase(args));
			return;
		}

		String pre = split[0].toLowerCase();

		if (pre.equals("type")) {
			for (String s : Iris.proj.getListing(true).keySet()) {
				list.add("type=" + s);
			}
			if (!list.contains("type=overworld")) {
				list.contains("type=overworld");
			}
		} else if (pre.equals("seed")) {
			list.add("seed=1337");
			list.add("seed=" + new Random().nextInt());
			list.add("seed=random");
		} else if (pre.equals("pregen")) {
			list.add("500");
			list.add("1000");
			list.add("2000");
			list.add("5k");
			list.add("10k");
			list.add("25k");
		}
	}

	private KList<String> getBase(String[] args) {
		KList<String> list = new KList<>();
		boolean seed = true;
		boolean type = true;
		boolean pregen = true;

		for (String s : args) {
			if (s.toLowerCase().startsWith("seed=")) seed = false;
			else if (s.toLowerCase().startsWith("type=")) type = false;
			else if (s.toLowerCase().startsWith("pregen=")) pregen = false;
		}

		if (seed) list.add("seed=");
		if (type) list.add("type=");
		if (pregen) list.add("pregen=");
		return list;
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		if(args.length < 1)
		{
			sender.sendMessage("/iris create <NAME> [type=overworld] [seed=1337] [pregen=5000]");
			return true;
		}
		Random random = new Random();
		String worldName = args[0];
		String type = IrisSettings.get().getGenerator().getDefaultWorldType();
		long seed = random.nextLong(); //Random seed when creating a world
		AtomicInteger pregen = new AtomicInteger(256);
		boolean multiverse = Iris.linkMultiverseCore.supported();

		for(String i : args)
		{
			type = i.startsWith("type=") ? i.split("\\Q=\\E")[1] : type;
			seed = i.startsWith("seed=") ? (i.split("\\Q=\\E")[1].equalsIgnoreCase("random") ? random.nextLong() : Long.valueOf(i.split("\\Q=\\E")[1])) : seed;
			pregen.set(i.startsWith("pregen=") ? getVal(i.split("\\Q=\\E")[1]) : pregen.get());
		}

		Iris.linkMultiverseCore.assignWorldType(worldName, type);
		World world = null;
		IrisDimension dim;
		File folder = new File(worldName);

		World finalWorld = world;
		Runnable onDone = () -> {

			sender.sendMessage(worldName + " Spawn Area generated.");
			sender.sendMessage("You must remember to either have multiverse installed or use the Bukkit method, otherwise the world will go corrupt!");
			sender.sendMessage("Wiki: https://volmitsoftware.gitbook.io/iris/getting-started");

			O<Boolean> b = new O<Boolean>();
			b.set(true);

			if(sender.isPlayer())
			{
				try
				{
					sender.player().teleport(finalWorld.getSpawnLocation());
				}

				catch(Throwable e)
				{

				}
			}

			if(pregen.get() > 0)
			{
				b.set(false);
				int size = pregen.get();
				size *= 2;
				sender.sendMessage("Pregenerating " + worldName + " " + size + " x " + size);
				sender.sendMessage("Expect server lag during this time. Use '/iris pregen stop' to cancel");

				new Pregenerator(finalWorld, size, () ->
				{
					b.set(true);
				});
			}

			World ww = finalWorld;
			if (ww == null){
				sender.sendMessage("World not created, can not finish");
				return;
			}
			J.a(() ->
			{
				while(!b.get())
				{
					J.sleep(1000);
				}


				Bukkit.getScheduler().scheduleSyncDelayedTask(Iris.instance, () ->
				{
					ww.save();
					sender.sendMessage("All Done!");
				});
			});
		};


		if(multiverse)
		{
			dim = IrisDataManager.loadAnyDimension(type);

			if(dim == null)
			{
				sender.sendMessage("Cant find dimension type: " + type + ". Did you forget to /ir download " + type + "?");
				return true;
			}

			if(dim.getEnvironment() == null)
			{
				dim.setEnvironment(World.Environment.NORMAL);
			}

			if(Iris.linkMultiverseCore == null)
			{
				Iris.linkMultiverseCore = new MultiverseCoreLink();
			}

			String command = "mv create " + worldName + " " + Iris.linkMultiverseCore.envName(dim.getEnvironment());
			command += " -s " + seed;
			command += " -g Iris:" + dim.getLoadKey();
			sender.sendMessage("Delegating " + command);
			Bukkit.dispatchCommand(sender, command);
			world= Bukkit.getWorld(worldName);
		}
		else
		{
			if(folder.exists())
			{
				sender.sendMessage("That world folder already exists!");
				return true;
			}

			File iris = new File(folder, "iris");
			iris.mkdirs();

			dim = Iris.proj.installIntoWorld(sender, type, folder);

			WorldCreator wc = new IrisWorldCreator().dimension(dim).name(worldName)
					.productionMode().seed(seed).create();
			sender.sendMessage("Generating with " + Iris.getThreadCount() + " threads per chunk");
			O<Boolean> done = new O<Boolean>();
			done.set(false);

			J.a(() ->
			{
				double last = 0;
				int req = 800;
				while(!done.get())
				{
					boolean derp = false;
					double v = (double) ((IrisAccess) wc.generator()).getGenerated() / (double) req;

					if(last > v || v > 1)
					{
						derp = true;
						v = last;
					}

					else
					{
						last = v;
					}

					sender.sendMessage("Generating " + Form.pc(v) + (derp ? " (Waiting on Server...)" : ""));
					J.sleep(3000);
				}
			});

			world = wc.createWorld();

			done.set(true);
		}


		sender.sendMessage(worldName + " Spawn Area generated.");
		sender.sendMessage("You must remember to either have multiverse installed or use the Bukkit method, otherwise the world will go corrupt!");
		sender.sendMessage("Wiki: https://volmitsoftware.gitbook.io/iris/getting-started");

		O<Boolean> b = new O<Boolean>();
		b.set(true);

		if(sender.isPlayer())
		{
			try
			{
				sender.player().teleport(world.getSpawnLocation());
			}

			catch(Throwable e)
			{

			}
		}

		if(pregen.get() * 2 > 0)
		{
			b.set(false);
			sender.sendMessage("Pregenerating " + worldName + " " + pregen + " x " + pregen);
			sender.sendMessage("Expect server lag during this time. Use '/iris pregen stop' to cancel");

			new Pregenerator(world, pregen.get() * 2, () ->
			{
				b.set(true);
			});
		}

		World ww = world;
		if (ww == null){
			sender.sendMessage("World not created, can not finish");
			return true;
		}
		J.a(() ->
		{
			while(!b.get())
			{
				J.sleep(1000);
			}


			Bukkit.getScheduler().scheduleSyncDelayedTask(Iris.instance, () ->
			{
				ww.save();
				sender.sendMessage("All Done!");
			});
		});

		return true;
	}

	private int getVal(String arg) {

		if(arg.toLowerCase().endsWith("c") || arg.toLowerCase().endsWith("chunks"))
		{
			return Integer.parseInt(arg.toLowerCase().replaceAll("\\Qc\\E", "").replaceAll("\\Qchunks\\E", "")) * 16;
		}

		if(arg.toLowerCase().endsWith("r") || arg.toLowerCase().endsWith("regions"))
		{
			return Integer.parseInt(arg.toLowerCase().replaceAll("\\Qr\\E", "").replaceAll("\\Qregions\\E", "")) * 512;
		}

		if(arg.toLowerCase().endsWith("k"))
		{
			return Integer.parseInt(arg.toLowerCase().replaceAll("\\Qk\\E", "")) * 1000;
		}

		return Integer.parseInt(arg.toLowerCase());
	}

	@Override
	protected String getArgsUsage()
	{
		return "<name> [type=overworld] [seed=1337] [pregen=5000]";
	}
}
