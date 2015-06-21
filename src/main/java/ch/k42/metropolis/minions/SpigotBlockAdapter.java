package ch.k42.metropolis.minions;

import ch.n1b.worldedit.jnbt.ByteArrayTag;
import ch.n1b.worldedit.jnbt.ByteTag;
import ch.n1b.worldedit.jnbt.CompoundTag;
import ch.n1b.worldedit.jnbt.DoubleTag;
import ch.n1b.worldedit.jnbt.EndTag;
import ch.n1b.worldedit.jnbt.FloatTag;
import ch.n1b.worldedit.jnbt.IntArrayTag;
import ch.n1b.worldedit.jnbt.IntTag;
import ch.n1b.worldedit.jnbt.ListTag;
import ch.n1b.worldedit.jnbt.LongTag;
import ch.n1b.worldedit.jnbt.NBTConstants;
import ch.n1b.worldedit.jnbt.ShortTag;
import ch.n1b.worldedit.jnbt.StringTag;
import ch.n1b.worldedit.jnbt.Tag;
import ch.n1b.worldedit.schematic.block.BaseBlock;
import ch.n1b.worldedit.schematic.data.DataException;
import com.google.common.base.Preconditions;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.NBTBase;
import net.minecraft.server.v1_8_R3.NBTTagByte;
import net.minecraft.server.v1_8_R3.NBTTagByteArray;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagDouble;
import net.minecraft.server.v1_8_R3.NBTTagEnd;
import net.minecraft.server.v1_8_R3.NBTTagFloat;
import net.minecraft.server.v1_8_R3.NBTTagInt;
import net.minecraft.server.v1_8_R3.NBTTagIntArray;
import net.minecraft.server.v1_8_R3.NBTTagList;
import net.minecraft.server.v1_8_R3.NBTTagLong;
import net.minecraft.server.v1_8_R3.NBTTagShort;
import net.minecraft.server.v1_8_R3.NBTTagString;
import net.minecraft.server.v1_8_R3.TileEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Thomas
 * @version metropolis 21.06.2015.
 */
public class SpigotBlockAdapter implements BlockAdapter {

    private final Field nbtListTagListField;
    private final Method nbtCreateTagMethod;

    public SpigotBlockAdapter() throws NoSuchFieldException, NoSuchMethodException {
        CraftServer.class.cast(Bukkit.getServer());
        this.nbtListTagListField = NBTTagList.class.getDeclaredField("list");
        this.nbtListTagListField.setAccessible(true);
        this.nbtCreateTagMethod = NBTBase.class.getDeclaredMethod("createTag", new Class[]{Byte.TYPE});
        this.nbtCreateTagMethod.setAccessible(true);
    }

    private static void readTagIntoTileEntity(NBTTagCompound tag, TileEntity tileEntity) {
        tileEntity.a(tag);
    }

    private static void readTileEntityIntoTag(TileEntity tileEntity, NBTTagCompound tag) {
        tileEntity.b(tag);
    }

    @Override
    public BaseBlock getBlock(Location location) throws DataException {
        Preconditions.checkNotNull(location);
        CraftWorld craftWorld = (CraftWorld)location.getWorld();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        Block bukkitBlock = location.getBlock();
        BaseBlock block = new BaseBlock(bukkitBlock.getTypeId(), bukkitBlock.getData());
        TileEntity te = craftWorld.getHandle().getTileEntity(new BlockPosition(x, y, z));
        if(te != null) {
            NBTTagCompound tag = new NBTTagCompound();
            readTileEntityIntoTag(te, tag);
            block.setNbtData((CompoundTag)this.toNative(tag));
        }

        return block;
    }

    @Override
    public boolean setBlock(Location location, BaseBlock block, boolean notifyAndLight) {
        Preconditions.checkNotNull(location);
        Preconditions.checkNotNull(block);
        CraftWorld craftWorld = (CraftWorld)location.getWorld();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        boolean changed = location.getBlock().setTypeIdAndData(block.getId(), (byte)block.getData(), notifyAndLight);
        CompoundTag nativeTag = block.getNbtData();
        if(nativeTag != null) {
            TileEntity tileEntity = craftWorld.getHandle().getTileEntity(new BlockPosition(x, y, z));
            if(tileEntity != null) {
                NBTTagCompound tag = (NBTTagCompound)this.fromNative(nativeTag);
                tag.set("x", new NBTTagInt(x));
                tag.set("y", new NBTTagInt(y));
                tag.set("z", new NBTTagInt(z));
                readTagIntoTileEntity(tag, tileEntity);
            }
        }

        return changed;
    }

    private Tag toNative(NBTBase foreign) {
        if(foreign == null) {
            return null;
        } else if(!(foreign instanceof NBTTagCompound)) {
            if(foreign instanceof NBTTagByte) {
                return new ByteTag(((NBTTagByte)foreign).f());
            } else if(foreign instanceof NBTTagByteArray) {
                return new ByteArrayTag(((NBTTagByteArray)foreign).c());
            } else if(foreign instanceof NBTTagDouble) {
                return new DoubleTag(((NBTTagDouble)foreign).g());
            } else if(foreign instanceof NBTTagFloat) {
                return new FloatTag(((NBTTagFloat)foreign).h());
            } else if(foreign instanceof NBTTagInt) {
                return new IntTag(((NBTTagInt)foreign).d());
            } else if(foreign instanceof NBTTagIntArray) {
                return new IntArrayTag(((NBTTagIntArray)foreign).c());
            } else if(foreign instanceof NBTTagList) {
                try {
                    return this.toNativeList((NBTTagList)foreign);
                } catch (Throwable var7) {
                    Minions.w("Failed to convert NBTTagList" + var7.getMessage());
                    return new ListTag(ByteTag.class, new ArrayList());
                }
            } else if(foreign instanceof NBTTagLong) {
                return new LongTag(((NBTTagLong)foreign).c());
            } else if(foreign instanceof NBTTagShort) {
                return new ShortTag(((NBTTagShort)foreign).e());
            } else if(foreign instanceof NBTTagString) {
                return new StringTag(((NBTTagString)foreign).a_());
            } else if(foreign instanceof NBTTagEnd) {
                return new EndTag();
            } else {
                throw new IllegalArgumentException("Don\'t know how to make native " + foreign.getClass().getCanonicalName());
            }
        } else {
            HashMap e = new HashMap();
            Set foreignKeys = ((NBTTagCompound)foreign).c();
            Iterator i$ = foreignKeys.iterator();

            while(i$.hasNext()) {
                String str = (String)i$.next();
                NBTBase base = ((NBTTagCompound)foreign).get(str);
                e.put(str, this.toNative(base));
            }

            return new CompoundTag(e);
        }
    }

    private ListTag toNativeList(NBTTagList foreign) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        ArrayList values = new ArrayList();
        int type = foreign.f();
        List foreignList = (List)this.nbtListTagListField.get(foreign);

        for(int cls = 0; cls < foreign.size(); ++cls) {
            NBTBase element = (NBTBase)foreignList.get(cls);
            values.add(this.toNative(element));
        }

        Class var7 = NBTConstants.getClassFromType(type);
        return new ListTag(var7, values);
    }

    private NBTBase fromNative(Tag foreign) {
        if(foreign == null) {
            return null;
        } else if(foreign instanceof CompoundTag) {
            NBTTagCompound e1 = new NBTTagCompound();
            Iterator foreignList1 = ((CompoundTag)foreign).getValue().entrySet().iterator();

            while(foreignList1.hasNext()) {
                Entry i$1 = (Entry)foreignList1.next();
                e1.set((String)i$1.getKey(), this.fromNative((Tag)i$1.getValue()));
            }

            return e1;
        } else if(foreign instanceof ByteTag) {
            return new NBTTagByte(((ByteTag)foreign).getValue().byteValue());
        } else if(foreign instanceof ByteArrayTag) {
            return new NBTTagByteArray(((ByteArrayTag)foreign).getValue());
        } else if(foreign instanceof DoubleTag) {
            return new NBTTagDouble(((DoubleTag)foreign).getValue().doubleValue());
        } else if(foreign instanceof FloatTag) {
            return new NBTTagFloat(((FloatTag)foreign).getValue().floatValue());
        } else if(foreign instanceof IntTag) {
            return new NBTTagInt(((IntTag)foreign).getValue().intValue());
        } else if(foreign instanceof IntArrayTag) {
            return new NBTTagIntArray(((IntArrayTag)foreign).getValue());
        } else if(!(foreign instanceof ListTag)) {
            if(foreign instanceof LongTag) {
                return new NBTTagLong(((LongTag)foreign).getValue().longValue());
            } else if(foreign instanceof ShortTag) {
                return new NBTTagShort(((ShortTag)foreign).getValue().shortValue());
            } else if(foreign instanceof StringTag) {
                return new NBTTagString(((StringTag)foreign).getValue());
            } else if(foreign instanceof EndTag) {
                try {
                    return (NBTBase)this.nbtCreateTagMethod.invoke((Object)null, new Object[]{Byte.valueOf((byte)0)});
                } catch (Exception var6) {
                    return null;
                }
            } else {
                throw new IllegalArgumentException("Don\'t know how to make NMS " + foreign.getClass().getCanonicalName());
            }
        } else {
            NBTTagList e = new NBTTagList();
            ListTag foreignList = (ListTag)foreign;
            Iterator i$ = foreignList.getValue().iterator();

            while(i$.hasNext()) {
                Tag t = (Tag)i$.next();
                e.add(this.fromNative(t));
            }

            return e;
        }
    }
}
