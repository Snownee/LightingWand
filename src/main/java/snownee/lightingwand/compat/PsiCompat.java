package snownee.lightingwand.compat;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import snownee.lightingwand.LW;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.common.lib.LibPieceGroups;

public class PsiCompat
{
    public static void init()
    {
        PsiAPI.registerSpellPiece("trickConjureLightInvisible", PieceTrickConjureLightInvisible.class);

        String textureName = "trick_conjure_light_invisible";
        if (Loader.isModLoaded("magipsi"))
        {
            textureName = "magical_" + textureName;
        }
        PsiAPI.simpleSpellTextures.put("trickConjureLightInvisible", new ResourceLocation(LW.MODID, String.format("textures/spell/%s.png", textureName)));

        PsiAPI.addPieceToGroup(PieceTrickConjureLightInvisible.class, LibPieceGroups.BLOCK_CONJURATION, false);
    }
}
