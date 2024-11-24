package de.crafty.skylife.world;

import de.crafty.skylife.SkyLife;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SkyLifeWorldDataLoader extends SavedData {

    private HashMap<BlockPos, UUID> spawns = new HashMap<>();

    public static SavedData.Factory<SkyLifeWorldDataLoader> getPersistentStateType() {
        return new SavedData.Factory<>(SkyLifeWorldDataLoader::new, SkyLifeWorldDataLoader::fromNbt, null);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registryLookup) {
        tag.put("originalSpawns", this.createSpawnTag());
        return tag;
    }

    public static SkyLifeWorldDataLoader fromNbt(CompoundTag tag, HolderLookup.Provider registryLookup){
        SkyLifeWorldDataLoader loader = new SkyLifeWorldDataLoader();
        loader.loadSpawnsFromTag(tag.getCompound("originalSpawns"));
        return loader;
    }

    public static SkyLifeWorldDataLoader getInstance(MinecraftServer server){

        DimensionDataStorage persistentStateManager = server.getLevel(Level.OVERWORLD).getDataStorage();
        return persistentStateManager.computeIfAbsent(SkyLifeWorldDataLoader.getPersistentStateType(), SkyLife.MODID);
    }

    private CompoundTag createSpawnTag(){
        CompoundTag spawnTag = new CompoundTag();

        //Index saving for sorted Spawns
        int i = 0;
        for(BlockPos blockPos : this.spawns.keySet()){
            String encodedBlockPos = String.format("%s;%s;%s;%s", i, blockPos.getX(), blockPos.getY(), blockPos.getZ());
            UUID uuid = this.spawns.get(blockPos);
            spawnTag.putString(encodedBlockPos, uuid == null ? "" : uuid.toString());
            i++;
        }

        return spawnTag;
    }

    private void loadSpawnsFromTag(CompoundTag tag){

        HashMap<BlockPos, UUID> spawns = new HashMap<>();

        tag.getAllKeys().forEach(encodedBlockPos -> {
            String[] split = encodedBlockPos.split(";");
            BlockPos pos = new BlockPos(Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
            UUID uuid = tag.getString(encodedBlockPos).isEmpty() ? null : UUID.fromString(tag.getString(encodedBlockPos));
            spawns.put(pos, uuid);
        });
        this.spawns = spawns;
    }

    public void createOriginalSpawns(List<BlockPos> positions){
        HashMap<BlockPos, UUID> spawns = new HashMap<>();
        positions.forEach(blockPos -> {
            spawns.put(blockPos, null);
        });
        this.spawns = spawns;
        this.setDirty();
    }

    public BlockPos assignNextSpawn(UUID uuid){
        for(BlockPos pos : this.spawns.keySet()){
            if(this.spawns.get(pos) != null)
                continue;

            this.spawns.put(pos, uuid);
            this.setDirty();
            return pos;
        }
        return new BlockPos(0, 200, 0);
    }
}
