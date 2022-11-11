package moodss.ia.util;

import moodss.ia.ray.BlockCollisionObelisk;
import moodss.ia.ray.CollisionObelisk;
import moodss.ia.ray.Ray;
import moodss.plummet.math.vec.Vector3;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;

public class BlockTraceCollisionUtil {

    // The chunk coordinate of the most recently stepped through block.
    private static int prevChunkX = Integer.MIN_VALUE;
    private static int prevChunkZ = Integer.MIN_VALUE;

    // The chunk belonging to prevChunkPos.
    private static Chunk prevChunk;

    public static CollisionObelisk createCollision(Vector3 from, Vector3 to) {
        return createCollision(MinecraftClient.getInstance().world, from, to);
    }

    public static CollisionObelisk createCollision(World world, Vector3 from, Vector3 to) {
        CollisionObelisk missed = new CollisionObelisk(new Ray(from, Vector3.ZERO, true), CollisionObelisk.Type.MISS);
        if(world == null) {
            return missed;
        }

        return RaycastUtils.raycast(from, to, (pos) -> {
            int chunkX = ChunkSectionPos.getSectionCoord(pos.getX());
            int chunkZ = ChunkSectionPos.getSectionCoord(pos.getZ());

            // Avoid calling into the chunk manager as much as possible through managing chunks locally
            if (prevChunkX != chunkX || prevChunkZ != chunkZ) {
                prevChunk = world.getChunk(chunkX, chunkZ);

                prevChunkX = chunkX;
                prevChunkZ = chunkZ;
            }

            final Chunk chunk = prevChunk;
            if (chunk != null) {
                // We operate directly on chunk sections to avoid interacting with BlockPos and to squeeze out as much
                // performance as possible here
                ChunkSection section = chunk.getSectionArray()[chunk.getSectionIndex(pos.getY())];

                // If the section doesn't exist or it's empty, assume that the block is air
                if (section != null && !section.isEmpty()) {
                    // Retrieve the block state from the chunk section directly to avoid associated overhead
                    BlockState blockState = section.getBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
                    FluidState fluidState = section.getFluidState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);

                    VoxelShape collisionBlockShape = blockState.getCollisionShape(world, pos);
                    VoxelShape collisionFluidShape = fluidState.getShape(world, pos);
                    BlockCollisionObelisk result = RaycastUtils.raycastBlock(world, from, to, pos, collisionBlockShape, blockState);
                    BlockCollisionObelisk fluidResult = RaycastUtils.raycastVoxelShape(collisionFluidShape, from, to, pos);

                    if(fluidResult == null) {
                        return result;
                    }

                    if(result == null) {
                        return fluidResult;
                    }

                    float blockDist = result.getRay().distanceToSquared(from);
                    float fluidDist = fluidResult.getRay().distanceToSquared(from);

                    return blockDist <= fluidDist ? result : fluidResult;
                }
            }
            return missed;
        }, () -> missed);
    }
}
