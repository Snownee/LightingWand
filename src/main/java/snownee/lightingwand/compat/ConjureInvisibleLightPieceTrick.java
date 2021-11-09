package snownee.lightingwand.compat;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import snownee.lightingwand.common.LightBlock;
import snownee.lightingwand.common.ModConstants;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;

public class ConjureInvisibleLightPieceTrick extends PieceTrick {

	SpellParam<Vector3> position;
	SpellParam<Number> light;

	public ConjureInvisibleLightPieceTrick(Spell spell) {
		super(spell);
	}

	@Override
	public void initParams() {
		addParam(position = new ParamVector(SpellParam.GENERIC_NAME_POSITION, SpellParam.BLUE, false, false));
		addParam(light = new ParamNumber(SpellParam.GENERIC_NAME_NUMBER, SpellParam.RED, true, false));
	}

	@Override
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
		super.addToMetadata(meta);
		double lightVal = ensurePositiveAndNonzero(this, light, 15);
		if (lightVal > 15) {
			throw new SpellCompilationException("psi.spellerror.lightingwand.light", x, y);
		}
		meta.addStat(EnumSpellStat.POTENCY, 40);
		meta.addStat(EnumSpellStat.COST, 120);
	}

	@Override
	public Object execute(SpellContext context) throws SpellRuntimeException {
		Vector3 positionVal = this.getParamValue(context, position);
		Number lightVal = this.getParamValue(context, light);

		if (positionVal == null) {
			throw new SpellRuntimeException(SpellRuntimeException.NULL_VECTOR);
		}
		if (!context.isInRadius(positionVal)) {
			throw new SpellRuntimeException(SpellRuntimeException.OUTSIDE_RADIUS);
		}

		BlockPos pos = positionVal.toBlockPos();

		World world = context.caster.getEntityWorld();

		if (!world.isBlockModifiable(context.caster, pos)) {
			return null;
		}

		if (world.getBlockState(pos).getBlock() != ModConstants.LIGHT) {
			BlockState state = ModConstants.LIGHT.getDefaultState();
			if (lightVal != null) {
				state = state.with(LightBlock.LIGHT, MathHelper.ceil(lightVal.doubleValue()));
			}
			conjure(world, pos, context.caster, state);
		}

		return null;
	}

	public static boolean conjure(World world, BlockPos pos, PlayerEntity player, BlockState state) {
		if (!world.isBlockPresent(pos) || !world.isBlockModifiable(player, pos)) {
			return false;
		}

		BlockState inWorld = world.getBlockState(pos);
		if (inWorld.getBlock().isAir(inWorld, world, pos) || inWorld.getMaterial().isReplaceable()) {
			return world.setBlockState(pos, state);
		}
		return false;
	}

	// from SpellHelpers
	public static double ensurePositiveAndNonzero(SpellPiece piece, SpellParam<Number> param, double def) throws SpellCompilationException {
		double val = piece.getParamEvaluationeOrDefault(param, def).doubleValue();
		if (val <= 0) {
			throw new SpellCompilationException(SpellCompilationException.NON_POSITIVE_VALUE, piece.x, piece.y);
		}

		return val;
	}
}
