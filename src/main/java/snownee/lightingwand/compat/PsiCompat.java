package snownee.lightingwand.compat;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import snownee.lightingwand.LW;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.common.lib.LibPieceGroups;

public class PsiCompat {

    public static final ResourceLocation PIECE_ID = new ResourceLocation(LW.MODID, "trick_conjure_invisible_light");

    public static void init() {
        PsiAPI.registerSpellPiece(PIECE_ID, ConjureInvisibleLightPieceTrick.class);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            String textureName = PIECE_ID.getPath();
            if (ModList.get().isLoaded("magipsi")) {
                textureName = "magical_" + textureName;
            }
            PsiAPI.registerPieceTexture(PIECE_ID, new ResourceLocation(LW.MODID, "spell/" + textureName));
        });

        PsiAPI.addPieceToGroup(ConjureInvisibleLightPieceTrick.class, new ResourceLocation("psi", LibPieceGroups.BLOCK_CONJURATION), false);
    }
}
