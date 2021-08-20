package slimeknights.tconstruct.tools.modifiers.upgrades.general;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.modifiers.SingleLevelModifier;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.HarvestLevels;

import java.util.List;

public class EmeraldModifier extends SingleLevelModifier {
  public EmeraldModifier() {
    super(0x41f384);
  }

  @Override
  public void addVolatileData(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData) {
    IModifiable.setRarity(volatileData, Rarity.UNCOMMON);
  }

  @Override
  public void addToolStats(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, IModDataReadOnly volatileData, int level, ModifierStatsBuilder builder) {
    ToolStats.DURABILITY.multiply(builder, 1 + (level * 0.5f));
    ToolStats.HARVEST_LEVEL.set(builder, HarvestLevels.IRON);
  }

  @Override
  public float getEntityDamage(IModifierToolStack tool, int level, ToolAttackContext context, float baseDamage, float damage) {
    LivingEntity living = context.getLivingTarget();
    if (living != null && living.getCreatureAttribute() == CreatureAttribute.ILLAGER) {
      damage += level * 2.5f * tool.getModifier(ToolStats.ATTACK_DAMAGE);
    }
    return damage;
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, boolean isAdvanced, boolean detailed) {
    addDamageTooltip(tool, level * 2.5f, tooltip);
  }
}