package com.volmit.iris.generator.decorator;

import com.volmit.iris.object.DecorationPart;
import com.volmit.iris.object.IrisBiome;
import com.volmit.iris.object.IrisDecorator;
import com.volmit.iris.scaffold.cache.Cache;
import com.volmit.iris.scaffold.engine.Engine;
import com.volmit.iris.scaffold.hunk.Hunk;
import org.bukkit.block.data.BlockData;

public class IrisShoreLineDecorator extends IrisEngineDecorator
{
    public IrisShoreLineDecorator(Engine engine) {
        super(engine, "Shore Line", DecorationPart.SHORE_LINE);
    }

    @Override
    public void decorate(int x, int z, int realX, int realX1, int realX_1, int realZ, int realZ1, int realZ_1, Hunk<BlockData> data, IrisBiome biome, int height, int max) {

        if(height == getDimension().getFluidHeight()) {
            if (Math.round(getComplex().getHeightStream().get(realX1, realZ)) < getComplex().getFluidHeight() ||
                    Math.round(getComplex().getHeightStream().get(realX_1, realZ)) < getComplex().getFluidHeight() ||
                    Math.round(getComplex().getHeightStream().get(realX, realZ1)) < getComplex().getFluidHeight() ||
                    Math.round(getComplex().getHeightStream().get(realX, realZ_1)) < getComplex().getFluidHeight()
            )
            {
                IrisDecorator decorator = getDecorator(biome, realX, realZ);

                if(decorator != null)
                {
                    if(!decorator.isStacking())
                    {
                        data.set(x, height+1, z, decorator.getBlockData100(biome, getRng(), realX, realZ, getData()));
                    }
                    else
                    {
                        int stack = decorator.getHeight(getRng().nextParallelRNG(Cache.key(realX, realZ)), realX, realZ, getData());

                        BlockData top = decorator.getBlockDataForTop(biome, getRng(), realX, realZ, getData());
                        BlockData fill = decorator.getBlockData100(biome, getRng(), realX, realZ, getData());

                        for(int i = 0; i < stack; i++)
                        {
                            double threshold = ((double)i) / (stack - 1);
                            data.set(x, height+1+i, z, threshold >= decorator.getTopThreshold() ? top : fill);
                        }
                    }
                }
            }
        }
    }
}
