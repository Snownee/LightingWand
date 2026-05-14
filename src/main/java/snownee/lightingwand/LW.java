package snownee.lightingwand;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.resources.Identifier;

public class LW {
	public static final String ID = "lightingwand";
	public static final Logger LOGGER = LogUtils.getLogger();

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(ID, path);
	}
}
