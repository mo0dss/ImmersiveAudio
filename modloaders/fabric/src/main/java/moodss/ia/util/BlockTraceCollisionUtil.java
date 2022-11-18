package moodss.ia.util;

import moodss.ia.ray.BlockRayHitResult;
import moodss.ia.ray.Ray;
import moodss.ia.ray.RayHitResult;
import moodss.plummet.math.MathUtils;
import moodss.plummet.math.vec.Vector3;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import org.jetbrains.annotations.Nullable;

public class BlockTraceCollisionUtil {

    // The chunk coordinate of the most recently stepped through block.
    private static int prevChunkX = Integer.MIN_VALUE;
    private static int prevChunkZ = Integer.MIN_VALUE;

    // The chunk belonging to prevChunkPos.
    private static Chunk prevChunk;

    public static RayHitResult createCollision(Ray ray, Vector3 to, @Nullable Vector3 ignore) {
        return createCollision(MinecraftClient.getInstance().world, ray, to, ignore);
    }

    public static RayHitResult createCollision(World world, Ray ray, Vector3 to, @Nullable Vector3 ignore) {
        Vector3 origin = Ray.getOrigin(ray);

        if(world == null) {
            return BlockRayHitResult.createMissed(ray, new BlockPos(MathUtils.floor(origin.getX()), MathUtils.floor(origin.getY()), MathUtils.floor(origin.getZ())));
        }

        return RaycastUtils.raycast(origin, to, pos -> {
            if(ignore != null) {
                if(pos.getX() == MathUtils.floor(ignore.getX()) || pos.getY() == MathUtils.floor(ignore.getY()) || pos.getZ() == MathUtils.floor(ignore.getZ())) {
                    return BlockRayHitResult.createMissed(ray, pos);
                }
            }

            int chunkX = ChunkSectionPos.getSectionCoord(pos.getX());
            int chunkZ = ChunkSectionPos.getSectionCoord(pos.getZ());

            // Avoid calling into the chunk manager as much as possible through managing chunks locally
            if (prevChunkX != chunkX || prevChunkZ != chunkZ) {
                prevChunk = world.getChunk(chunkX, chunkZ);

                prevChunkX = chunkX;
                prevChunkZ = chunkZ;
            }

            Chunk chunk = prevChunk;
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

                    BlockRayHitResult result = RaycastUtils.raycastBlock(world, ray, to, pos, collisionBlockShape, blockState);
                    BlockRayHitResult fluidResult = RaycastUtils.raycastVoxelShape(collisionFluidShape, ray, to, pos);

                    if (fluidResult == null) {
                        return result;
                    }

                    if (result == null) {
                        return fluidResult;
                    }

                    float blockDist = result.ray().distanceToSquared(ray);
                    float fluidDist = fluidResult.ray().distanceToSquared(ray);
                    return blockDist <= fluidDist ? result : fluidResult;
                }
            }

            return BlockRayHitResult.createMissed(ray, pos);
        }, (pos) -> BlockRayHitResult.createMissed(ray, pos));
    }
}
