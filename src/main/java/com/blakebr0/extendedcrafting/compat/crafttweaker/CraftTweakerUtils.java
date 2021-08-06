package com.blakebr0.extendedcrafting.compat.crafttweaker;

import com.blamejared.crafttweaker.api.data.IData;
import com.blamejared.crafttweaker.api.data.NBTConverter;
import net.minecraft.nbt.Tag;

public final class CraftTweakerUtils {
	public static String writeTag(Tag nbt) {
        IData data = NBTConverter.convert(nbt);
        return data == null ? "" : data.asString();
	}
}
