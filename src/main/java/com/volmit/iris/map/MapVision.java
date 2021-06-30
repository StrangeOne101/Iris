package com.volmit.iris.map;

import com.volmit.iris.Iris;
import com.volmit.iris.generator.IrisComplex;
import com.volmit.iris.util.J;
import com.volmit.iris.util.KMap;
import com.volmit.iris.util.KSet;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapVision extends JPanel {

    private long seed;
    private int threadId = 0;

    private static final int S = 128;

    private IrisComplex complex;
    private RenderType currentType = RenderType.BIOME_LAND;

    private int mouseX;
    private int mouseY;
    private double draggedOffsetX;
    private double draggedOffsetY;
    private int windowOffsetX = getWidth() / 2;
    private int windowOffsetY = getHeight() / 2;
    private boolean dirty = true; //Whether to repaint textures
    private double scale = 1;

    //private Tile[][] tiles = new Tile[128][128];
    private KMap<Integer, Tile> tiles = new KMap<>();

    private Set<Tile> visibleTiles = new KSet<>();

    private short[][] spiral;

    private final Color overlay = new Color(50, 50, 50);

    private static final int targetFPS = 60;

    private final ExecutorService executorService = Executors.newFixedThreadPool(8, r -> {
        threadId++;
        Thread t = new Thread(r);
        t.setName("Iris Map Renderer " + threadId);
        t.setPriority(Thread.MIN_PRIORITY);
        t.setUncaughtExceptionHandler((et, e) ->
        {
            Iris.info("Exception encountered in " + et.getName());
            e.printStackTrace();
        });

        return t;
    });

    public MapVision(IrisComplex worldComplex)
    {
        this.complex = worldComplex;
        this.setVisible(true);
        generateSpiral(64);

        addMouseWheelListener((mouseWheelEvent) -> {
            this.scale = Math.max(this.scale + mouseWheelEvent.getWheelRotation(), 1);
        });
        addMouseMotionListener(new MouseMotionListener()
        {
            @Override
            public void mouseMoved(MouseEvent e)
            {
                Point cp = e.getPoint();
                mouseX = cp.x;
                mouseY = cp.y;
            }

            @Override
            public void mouseDragged(MouseEvent e)
            {
                Point cp = e.getPoint();
                draggedOffsetX -= (mouseX - cp.x) * scale;
                draggedOffsetY -= (mouseY - cp.y) * scale;
                mouseX = cp.x;
                mouseY = cp.y;
                dirty = true;
            }
        });
        for (int i = 0; i < 64; i++) {
            short x = spiral[i][0];
            short y = spiral[i][1];
            Iris.info(x + ", " + y);
            queue(x, y);
        }
    }

    public void open() {
        JFrame frame = new JFrame("Iris Map (" + complex.getData().getDataFolder().getName() + ")");
        frame.add(this);
        frame.setSize(1440, 820);
        frame.setVisible(true);
        File file = Iris.getCached("Iris Icon", "https://raw.githubusercontent.com/VolmitSoftware/Iris/master/icon.png");

        if(file != null) {
            try {
                frame.setIconImage(ImageIO.read(file));
            } catch(IOException ignored) { }
        }
    }

    @Override
    public void paint(Graphics gx) {
        //super.paint(gx);
        int offsetX = (int) (draggedOffsetX * scale) + windowOffsetX;
        int offsetY = (int) (draggedOffsetY * scale) + windowOffsetY;

        if (dirty) {
            for (Tile tile : visibleTiles) {
                int x = (tile.getX() << 7) + offsetX;
                int y = (tile.getY() << 7) + offsetY;

                gx.drawImage(tile.getImage(), x, y, (int)(S / scale), (int)(S / scale), null);
            }
            dirty = false;
        }

        gx.setColor(overlay);
        gx.fillRect(getWidth() - 400, 4, 396, 28);
        gx.setColor(Color.WHITE);
        int x = (mouseX << 2) - offsetX - windowOffsetX;
        int y = (mouseY << 2) - offsetY - windowOffsetY;
        String text = complex.getLandBiomeStream().get(x, y).getName().toUpperCase() + " [" + x+ ", " + y + "]";
        gx.setFont(new Font("Arial", Font.BOLD, 16));
        gx.drawString(text, getWidth() - 400 + 6, 24);

        J.a(() ->
        {
            J.sleep(1000 / targetFPS);
            repaint();
        });
    }



    public void recalculate() {
        short centerTileX = (short) ((draggedOffsetX * scale) / S);
        short centerTileY = (short) ((draggedOffsetY * scale) / S);

        int woh = Math.max(getWidth(), getHeight());
        int newSize = (int) (woh / S * scale) + 1;
        int checkSize = newSize / 2 + 1;
        generateSpiral(newSize);

        Set<Tile> checked = new HashSet<>();

        for (short[] coords : spiral) { //Start from the center of the spiral and work outwards to find new tiles to queue
            short x = (short)(coords[0] + centerTileX);
            short y = (short)(coords[1] + centerTileY);
            Tile tile = getTile(x, y);

            if (tile == null) {
                queue(x, y);
            } else {
                checked.add(tile);
            }

            if (Math.abs(y) > checkSize) { //When it goes offscreen
                if (Math.abs(x) > checkSize) { //Cancel the task if both sides are offscreen
                    break;
                }
                continue; //Else, just go to the next tile to test
            }
        }

        Set<Tile> clone = new HashSet(visibleTiles); //Clone the visible tiles
        clone.removeAll(checked);                    //Remove the ones that we know are onscreen

        for (Tile t : clone) { //Loop through the invisible tiles and mark them for removal from memory
            queueForRemoval(t);
        }
    }

    public void queue(short tileX, short tileY) {
        //TODO
        executorService.submit(() -> {
            Tile tile = new Tile(tileX, tileY);
            tile.render(complex, currentType);
            tiles.put(getTileId(tileX, tileY), tile);
            visibleTiles.add(tile);
            dirty = true;
        });

    }

    public void queueForRemoval(Tile tile) {
        visibleTiles.remove(tile);
        executorService.submit(() -> {
            tiles.remove(getTileId(tile.getX(), tile.getY()));
        });
        //TODO
    }

    /**
     * Get a tile based on the X and Z coords of the tile
     * @param tileX
     * @param tileZ
     * @return
     */
    @Nullable
    public Tile getTile(short tileX, short tileZ) {
        return tiles.get(getTileId(tileX, tileZ));
    }

    public int getTileId(short tileX, short tileZ) {
        return tileX | tileZ << 16;
    }

    public void generateSpiral(int size) {
        if (size % 2 == 0) size++;
        short[][] newSpiral = new short[size * size][2];

        int x = 0; // current position; x
        int y = 0; // current position; y
        int d = 0; // current direction; 0=RIGHT, 1=DOWN, 2=LEFT, 3=UP
        int s = 1; // chain size
        int c = 0; // count

        // starting point
        x = ((int)(size/2.0))-1;
        y = ((int)(size/2.0))-1;
        int offset = (size / 2) - 1;

        for (int k=1; k<=(size-1); k++)
        {
            for (int j=0; j<(k<(size-1)?2:3); j++)
            {
                for (int i=0; i<s; i++)
                {
                    short[] coords = {(short) (x - offset), (short) (y - offset)};
                    newSpiral[c] = coords;
                    c++;

                    switch (d)
                    {
                        case 0: y = y + 1; break;
                        case 1: x = x + 1; break;
                        case 2: y = y - 1; break;
                        case 3: x = x - 1; break;
                    }
                }
                d = (d+1)%4;
            }
            s = s + 1;
        }

        spiral = newSpiral;
    }
}
