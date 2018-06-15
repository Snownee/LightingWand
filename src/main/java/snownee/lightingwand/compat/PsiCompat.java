package snownee.lightingwand.compat;

import vazkii.psi.api.PsiAPI;
import vazkii.psi.common.lib.LibPieceGroups;

public class PsiCompat
{
    public static void init()
    {
        PsiAPI.registerSpellPieceAndTexture("trickConjureLightInvisible", PieceTrickConjureLightInvisible.class);
        PsiAPI.addPieceToGroup(PieceTrickConjureLightInvisible.class, LibPieceGroups.BLOCK_CONJURATION, false);
    }
}
