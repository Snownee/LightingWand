package snownee.lightingwand.compat;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import snownee.lightingwand.common.ModConstants;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;

public class PieceTrickConjureLightInvisible extends PieceTrick
{

    private ParamVector position;

    public PieceTrickConjureLightInvisible(Spell spell)
    {
        super(spell);
    }

    @Override
    public void initParams()
    {
        addParam(position = new ParamVector(SpellParam.GENERIC_NAME_POSITION, SpellParam.BLUE, false, false));
    }

    @Override
    public void addToMetadata(SpellMetadata meta) throws SpellCompilationException
    {
        super.addToMetadata(meta);
        addStats(meta);
    }

    private void addStats(SpellMetadata meta)
    {
        meta.addStat(EnumSpellStat.POTENCY, 15);
        meta.addStat(EnumSpellStat.COST, 20);
    }

    @Override
    public Object execute(SpellContext context) throws SpellRuntimeException
    {
        Vector3 positionVal = this.<Vector3>getParamValue(context, position);

        if (positionVal == null)
            throw new SpellRuntimeException(SpellRuntimeException.NULL_VECTOR);
        if (!context.isInRadius(positionVal))
            throw new SpellRuntimeException(SpellRuntimeException.OUTSIDE_RADIUS);

        BlockPos pos = new BlockPos(positionVal.x, positionVal.y, positionVal.z);
        World world = context.caster.getEntityWorld();
        if (world.isRemote || !world.isBlockLoaded(pos) || !world.isBlockModifiable(context.caster, pos))
        {
            return null;
        }

        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (block != ModConstants.LIGHT
                && (block == null || block.isAir(state, world, pos) || block.isReplaceable(world, pos)))
        {
            world.setBlockState(pos, ModConstants.LIGHT.getDefaultState());
        }

        return null;
    }
}
