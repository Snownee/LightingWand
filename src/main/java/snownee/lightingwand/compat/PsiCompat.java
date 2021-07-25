//package snownee.lightingwand.compat;
//
//import net.minecraft.resources.ResourceLocation;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.fml.DistExecutor;
//import net.minecraftforge.fml.ModList;
//import snownee.lightingwand.LW;
//import vazkii.psi.api.ClientPsiAPI;
//import vazkii.psi.api.PsiAPI;
//
//public class PsiCompat {
//
//    // from LibPieceGroups
//    public static final String BLOCK_CONJURATION = "block_conjuration";
//
//    public static final ResourceLocation PIECE_ID = new ResourceLocation(LW.MODID, "trick_conjure_invisible_light");
//
//    public static void init() {
//        PsiAPI.registerSpellPiece(PIECE_ID, ConjureInvisibleLightPieceTrick.class);
//
//        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
//            String textureName = PIECE_ID.getPath();
//            if (ModList.get().isLoaded("magipsi")) {
//                textureName = "magical_" + textureName;
//            }
//            ClientPsiAPI.registerPieceTexture(PIECE_ID, new ResourceLocation(LW.MODID, "spell/" + textureName));
//        });
//
//        PsiAPI.addPieceToGroup(ConjureInvisibleLightPieceTrick.class, new ResourceLocation("psi", BLOCK_CONJURATION), false);
//    }
//}
