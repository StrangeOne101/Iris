package com.volmit.iris.manager.gui;

import com.volmit.iris.Iris;
import com.volmit.iris.scaffold.engine.IrisAccess;
import com.volmit.iris.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IrisVision extends JPanel implements MouseWheelListener
{
	private static final long serialVersionUID = 2094606939770332040L;
	private IrisRenderer renderer;
	private int posX = 0;
	private int posZ = 0;
	private double scale = 128;
	private double mscale = 1D;
	private int w = 0;
	private int h = 0;
	private double lx = Double.MAX_VALUE; //Mouse X
	private double lz = Double.MAX_VALUE; //Mouse Y
	private double ox = 0;
	private double oz = 0;
	private double oxp = 0;
	private double ozp = 0;
	double tfps = 240D;
	private RollingSequence rs = new RollingSequence(512);
	private O<Integer> fastThreadCount = new O<>();
	private int maxFastThreads = 20;
	private int maxThreads = 9;
	private int tid = 0;
	private KMap<Integer, KMap<Long, BufferedImage>> positions = new KMap<>();
	private KMap<Integer, KMap<Long, BufferedImage>> fastpositions = new KMap<>();
	private KSet<Long> working = new KSet<>();
	private KSet<Long> workingfast = new KSet<>();

	private final ExecutorService e = Executors.newFixedThreadPool(8, r -> {
		tid++;
		Thread t = new Thread(r);
		t.setName("Iris HD Renderer " + tid);
		t.setPriority(Thread.MIN_PRIORITY);
		t.setUncaughtExceptionHandler((et, e) ->
		{
			Iris.info("Exception encountered in " + et.getName());
			e.printStackTrace();
		});

		return t;
	});

	private final ExecutorService eh = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), r -> {
		tid++;
		Thread t = new Thread(r);
		t.setName("Iris Renderer " + tid);
		t.setPriority(Thread.NORM_PRIORITY);
		t.setUncaughtExceptionHandler((et, e) ->
		{
			Iris.info("Exception encountered in " + et.getName());
			e.printStackTrace();
		});

		return t;
	});

	public IrisVision()
	{
		fastThreadCount.set(8);
		renderer = new IrisRenderer(null);
		rs.put(1);
		addMouseWheelListener(this);
		addMouseMotionListener(new MouseMotionListener()
		{
			@Override
			public void mouseMoved(MouseEvent e)
			{
				Point cp = e.getPoint();
				lx = (cp.getX());
				lz = (cp.getY());
			}

			@Override
			public void mouseDragged(MouseEvent e)
			{
				Point cp = e.getPoint();
				ox += (lx - cp.getX()) * scale;
				oz += (lz - cp.getY()) * scale;
				lx = cp.getX();
				lz = cp.getY();
			}
		});
	}

	public BufferedImage getTile(KSet<Long> fg, int scale, int x, int z)
	{
		BlockPosition key = new BlockPosition(scale, Math.floorDiv(x, scale), Math.floorDiv(z, scale));
		long longKey = key.asLong();

		fg.add(longKey);

		if (!positions.containsKey(scale)) {
			//System.out.println("Adding scale p " + scale);
			positions.put(scale, new KMap<>());
		}
		if (!fastpositions.containsKey(scale)) {
			//System.out.println("Adding scale fp " + scale);
			fastpositions.put(scale, new KMap<>());
		}

		if(positions.get(scale).containsKey(longKey))
		{
			//System.out.println("Loading " + longKey);
			return positions.get(scale).get(longKey);
		}

		if(fastpositions.get(scale).containsKey(longKey))
		{
			//System.out.println("here");
			if(!working.contains(longKey) && working.size() < maxThreads)
			{

				fastThreadCount.set(fastThreadCount.get() - 1);
				//System.out.println("here2");
				if(fastThreadCount.get() >= 0)
				{
					//System.out.println("Starting " + longKey);
					//System.out.println("here3" + " : " + m.get());
					working.add(longKey);
					double mk = mscale;
					double mkd = this.scale;
					e.submit(() ->
					{
						PrecisionStopwatch ps = PrecisionStopwatch.start();
						//System.out.println("Finished " + longKey);
						BufferedImage b = renderer.render(x * mscale, z * mscale, scale * mscale, scale);
						rs.put(ps.getMilliseconds());
						working.remove(longKey);

						/**if(mk == mscale && mkd == this.scale)
						{**/
							//System.out.println("Scale " + (mscale));
							positions.get(scale).put(longKey, b);
						//}
					});

				}
			}

			return fastpositions.get(scale).get(longKey);
		}

		if (workingfast.contains(longKey)) //Skip because it isn't done yet
		{
			return null;
		}

		fastThreadCount.set(fastThreadCount.get() - 1);

		if(fastThreadCount.get() >= 0)
		{
			//System.out.println("Starting fast " + longKey);
			workingfast.add(longKey);
			double mk = mscale;
			double mkd = this.scale;
			eh.submit(() ->
			{
				//System.out.println("Finished fast " + longKey);
				//System.out.println("Rendering " + (x * mscale) + ", " + (z * mscale));
				PrecisionStopwatch ps = PrecisionStopwatch.start();
				BufferedImage b = renderer.render(x * mscale, z * mscale, scale * mscale, scale / 12);
				rs.put(ps.getMilliseconds());
				workingfast.remove(longKey);

				//if(mk == mscale && mkd == this.scale)
				//{
					fastpositions.get(scale).put(longKey, b);
				//}
			});
		}
		return null;
	}

	@Override
	public void paint(Graphics gx)
	{
		if (ox < oxp)
		{
			oxp -= Math.abs(ox - oxp) * 0.36;
		}
		else if(ox > oxp)
		{
			oxp += Math.abs(oxp - ox) * 0.36;
		}

		if (oz < ozp)
		{
			ozp -= Math.abs(oz - ozp) * 0.36;
		}
		else if(oz > ozp)
		{
			ozp += Math.abs(ozp - oz) * 0.36;
		}

		PrecisionStopwatch p = PrecisionStopwatch.start();
		Graphics2D g = (Graphics2D) gx;
		w = getWidth();
		h = getHeight();
		double vscale = scale;
		scale = w / 32D;

		if (scale != vscale)
		{
			positions.clear();
		}

		KSet<Long> gg = new KSet<>();
		int iscale = (int) scale;
		g.setColor(Color.white);
		g.clearRect(0, 0, w, h);
		posX = (int) oxp;
		posZ = (int) ozp;
		fastThreadCount.set(maxFastThreads);

		for(int r = 0; r < Math.max(w, h); r += iscale)
		{
			for(int i = -iscale; i < w + iscale; i += iscale)
			{
				for(int j = -iscale; j < h + iscale; j += iscale)
				{
					int a = i - (w / 2);
					int b = j - (h / 2);
					if(a * a + b * b <= r * r)
					{
						//System.out.println("Scale " + iscale);
						BufferedImage t = getTile(gg, iscale, Math.floorDiv((posX / iscale) + i, iscale) * iscale, Math.floorDiv((posZ / iscale) + j, iscale) * iscale);

						if(t != null)
						{
							g.drawImage(t, i - ((posX / iscale) % (iscale)), j - ((posZ / iscale) % (iscale)), iscale, iscale,
									(img, infoflags, x, y, width, height) -> true);
						}
						else
						{
							g.setColor(Color.BLACK);
							g.setFont(new Font("Consolas", Font.PLAIN, 18));
							g.drawString("Image map not found", 20, 20);
						}
					}
				}
			}
		}

		p.end();

		/*for(Long i : positions.k())
		{
			if(!gg.contains(i))
			{
				positions.remove(i);
			}
		}*/

		if(!isVisible())
		{
			return;
		}

		if(!getParent().isVisible())
		{
			return;
		}

		if(!getParent().getParent().isVisible())
		{
			return;
		}

		J.a(() ->
		{
			J.sleep(1);
			repaint();
		});
	}

	private static void createAndShowGUI(Renderer r, int s)
	{
		JFrame frame = new JFrame("Vision");
		IrisVision nv = new IrisVision();
		nv.renderer = new IrisRenderer(r);
		frame.add(nv);
		frame.setSize(1440, 820);
		frame.setVisible(true);
		File file = Iris.getCached("Iris Icon", "https://raw.githubusercontent.com/VolmitSoftware/Iris/master/icon.png");

		if(file != null)
		{
			try
			{
				frame.setIconImage(ImageIO.read(file));
			}
			catch(IOException ignored)
			{

			}
		}
	}

	public static void launch(IrisAccess g, int i) {
		J.a(() ->
		{
			createAndShowGUI((x, z) -> g.getEngineAccess(i).draw(x, z), i);
		});
	}

	public void mouseWheelMoved(MouseWheelEvent e)
	{
		int notches = e.getWheelRotation();
		if(e.isControlDown())
		{
			return;
		}

		Iris.info("Blocks/Pixel: " + (mscale) + ", Blocks Wide: " + (w * mscale));
		positions.clear();
		fastpositions.clear();
		mscale = Math.max(1, notches > 0 ? mscale * 0.5 : mscale * 2);
		mscale = mscale + ((0.044 * mscale) * notches);
		mscale = Math.max(mscale, 0.00001);
	}
}
