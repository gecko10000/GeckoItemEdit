package gecko10000.geckoitemedit;

import gecko10000.geckolib.extensions.MMKt;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.strokkur.commands.Aliases;
import net.strokkur.commands.Command;
import net.strokkur.commands.Executes;
import net.strokkur.commands.Literal;
import net.strokkur.commands.arguments.IntArg;
import net.strokkur.commands.arguments.StringArg;
import net.strokkur.commands.arguments.StringArgType;
import net.strokkur.commands.paper.Executor;
import net.strokkur.commands.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.*;

@SuppressWarnings("UnstableApiUsage")
@Command("geckoedit")
@Aliases("ge")
@Permission("geckoedit.command")
public class CommandHandler {

    private final GeckoItemEdit plugin = JavaPlugin.getPlugin(GeckoItemEdit.class);
    private static final Component NO_ITEM_ERROR = MMKt.getMM().deserialize("<red>Hold an item.");

    void register() {
        plugin.getLifecycleManager()
                .registerEventHandler(LifecycleEvents.COMMANDS.newHandler(event ->
                        CommandHandlerBrigadier.register(
                                event.registrar()
                        )
                ));
    }

    private @Nullable ItemStack getItem(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.isEmpty()) {
            player.sendMessage(NO_ITEM_ERROR);
            return null;
        }
        return item;
    }

    private @Nullable Key parseKey(String input, Player player) {
        try {
            return Key.key(input);
        } catch (InvalidKeyException ex) {
            player.sendRichMessage("<red>Invalid key <yellow><key></yellow>.",
                    Placeholder.unparsed("key", input));
            return null;
        }
    }

    @Executes("custom_model_data")
    @Permission("geckoedit.command.custom_model_data")
    void customModelData(CommandSender sender, @Executor Player player, @StringArg(StringArgType.GREEDY) String data) {
        ItemStack item = getItem(player);
        if (item == null) return;
        CustomModelData.Builder cmd = CustomModelData.customModelData();
        for (String split : data.split(" ")) {
            try {
                float f = Float.parseFloat(split);
                cmd.addFloat(f);
            } catch (NumberFormatException ex) {
                cmd.addString(split);
            }
        }
        item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, cmd.build());
        player.sendRichMessage("<green>Set custom model data to <data>.",
                Placeholder.unparsed("data", data));
    }

    @Executes("custom_name")
    @Permission("geckoedit.command.custom_name")
    void customName(CommandSender sender, @Executor Player player, @StringArg(StringArgType.GREEDY) String mmString) {
        ItemStack item = getItem(player);
        if (item == null) return;
        Component customName = MMKt.parseMM(mmString, true);
        item.setData(DataComponentTypes.CUSTOM_NAME, customName);
        player.sendRichMessage("<green>Set custom name to <name>.", Placeholder.component("name", customName));
    }

    @Executes("damage")
    @Permission("geckoedit.command.damage")
    void damage(CommandSender sender, @Executor Player player, @IntArg(min = 0) int damage) {
        ItemStack item = getItem(player);
        if (item == null) return;
        item.setData(DataComponentTypes.DAMAGE, damage);
        player.sendRichMessage("<green>Set damage to <yellow><damage></yellow>.",
                Placeholder.unparsed("damage", damage + ""));
    }

    @Executes("enchantment_glint_override")
    @Permission("geckoedit.command.enchantment_glint_override")
    void enchantmentGlintOverride(CommandSender sender, @Executor Player player, boolean enabled) {
        ItemStack item = getItem(player);
        if (item == null) return;
        item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, enabled);
        player.sendRichMessage("<green><yellow><state></yellow> enchantment glint override.",
                Placeholder.unparsed("state", enabled ? "Enabled" : "Disabled"));
    }

    @Executes("enchantments")
    @Permission("geckoedit.command.enchantments")
    void enchantments(CommandSender sender, @Executor Player player, Enchantment enchantment,
                      @IntArg(min = 0, max = 255) int level) {
        ItemStack item = getItem(player);
        if (item == null) return;
        if (level == 0) {
            int previousLevel = item.removeEnchantment(enchantment);
            if (previousLevel == 0) {
                player.sendRichMessage("<red>No enchantment to remove.");
            } else {
                player.sendRichMessage("<green>Removed <enchant>.",
                        Placeholder.component("enchant", enchantment.displayName(previousLevel)));
            }
            return;
        }
        item.addUnsafeEnchantment(enchantment, level);
        player.sendRichMessage("<green>Added <enchant>.",
                Placeholder.component("enchant", enchantment.displayName(level)));
    }

    @Executes("item_model")
    @Permission("geckoedit.command.item_model")
    void itemModel(CommandSender sender, @Executor Player player, @StringArg(StringArgType.GREEDY) String modelKey) {
        ItemStack item = getItem(player);
        if (item == null) return;
        Key key = parseKey(modelKey, player);
        if (key == null) return;
        item.setData(DataComponentTypes.ITEM_MODEL, key);
        player.sendRichMessage("<green>Set item model to <key>.",
                Placeholder.unparsed("key", key.asString()));
    }

    @Executes("item_name")
    @Permission("geckoedit.command.item_name")
    void itemName(CommandSender sender, @Executor Player player, @StringArg(StringArgType.GREEDY) String mmString) {
        ItemStack item = getItem(player);
        if (item == null) return;
        Component itemName = MMKt.parseMM(mmString, true);
        item.setData(DataComponentTypes.ITEM_NAME, itemName);
        player.sendRichMessage("<green>Set item name to <name>.", Placeholder.component("name", itemName));
    }

    @Executes("lore")
    @Permission("geckoedit.command.lore")
    void removeLore(CommandSender sender, @Executor Player player, int line) {
        ItemStack item = getItem(player);
        if (item == null) return;
        List<Component> lore = item.lore();
        if (lore == null || line > lore.size()) {
            player.sendRichMessage("<red>This item doesn't have that many lines of lore.");
            return;
        }
        lore.remove(line - 1);
        item.lore(lore);
        player.sendRichMessage(
                "<green>Removed line <yellow><line></yellow> of the lore.",
                Placeholder.unparsed("line", line + "")
        );
    }

    @Executes("lore")
    @Permission("geckoedit.command.lore")
    void lore(CommandSender sender, @Executor Player player, @IntArg(min = 1, max = 256) int lineNumber,
              @StringArg(StringArgType.GREEDY) String mmString) {
        ItemStack item = getItem(player);
        if (item == null) return;
        List<Component> lore = item.lore();
        if (lore == null) lore = new ArrayList<>();
        for (int i = lore.size(); i < lineNumber; i++) {
            lore.add(Component.empty());
        }
        Component loreLine = MMKt.parseMM(mmString, true);
        lore.set(lineNumber - 1, loreLine);
        item.lore(lore);
        player.sendRichMessage(
                "<green>Set lore line <yellow><line></yellow> to <lore>.",
                Placeholder.unparsed("line", lineNumber + ""),
                Placeholder.component("lore", loreLine)
        );
    }

    @Executes("max_damage")
    @Permission("geckoedit.command.max_damage")
    void maxDamage(CommandSender sender, @Executor Player player, @IntArg(min = 1) int maxDamage) {
        ItemStack item = getItem(player);
        if (item == null) return;
        item.setData(DataComponentTypes.MAX_DAMAGE, maxDamage);
        player.sendRichMessage("<green>Set max damage to <yellow><max></yellow>.",
                Placeholder.unparsed("max", maxDamage + ""));
    }

    @Executes("max_stack_size")
    @Permission("geckoedit.command.max_stack_size")
    void maxStackSize(CommandSender sender, @Executor Player player, @IntArg(min = 1, max = 99) int maxSize) {
        ItemStack item = getItem(player);
        if (item == null) return;
        item.setData(DataComponentTypes.MAX_STACK_SIZE, maxSize);
        player.sendRichMessage(
                "<green>Set max stack size to <yellow><max></yellow>.",
                Placeholder.unparsed("max", maxSize + "")
        );
    }

    @Executes("rarity")
    @Permission("geckoedit.command.rarity")
    void rarity(CommandSender sender, @Executor Player player,
                @Literal({"common", "uncommon", "rare", "epic"}) String rarity) {
        ItemStack item = getItem(player);
        if (item == null) return;
        item.setData(DataComponentTypes.RARITY, ItemRarity.valueOf(rarity.toUpperCase()));
        player.sendRichMessage("<green>Set item rarity to <yellow><rarity></yellow>.",
                Placeholder.unparsed("rarity", rarity));
    }

    @Executes("stored_enchantments")
    @Permission("geckoedit.command.stored_enchantments")
    void storedEnchantments(CommandSender sender, @Executor Player player, Enchantment enchantment, @IntArg(min = 0,
            max = 255) int level) {
        ItemStack item = getItem(player);
        if (item == null) return;
        ItemEnchantments stored = item.getData(DataComponentTypes.STORED_ENCHANTMENTS);
        if (stored == null) {
            stored = ItemEnchantments.itemEnchantments().build();
        }
        Map<Enchantment, Integer> levels = new HashMap<>(stored.enchantments());
        if (level == 0) {
            Integer prevLevel = levels.remove(enchantment);
            if (prevLevel == null || prevLevel == 0) {
                player.sendRichMessage("<red>No enchantment to remove.");
            } else {
                player.sendRichMessage("<green>Removed <enchant>.",
                        Placeholder.component("enchant", enchantment.displayName(prevLevel)));
            }
            return;
        }
        ItemEnchantments newStored = ItemEnchantments.itemEnchantments()
                .addAll(levels)
                .add(enchantment, level)
                .build();
        item.setData(DataComponentTypes.STORED_ENCHANTMENTS, newStored);
        player.sendRichMessage("<green>Added <enchant>.",
                Placeholder.component("enchant", enchantment.displayName(level)));
    }

    @Executes("tooltip_display")
    @Permission("geckoedit.command.tooltip_display")
    void tooltipDisplay(CommandSender sender, @Executor Player player) {
        ItemStack item = getItem(player);
        if (item == null) return;
        TooltipDisplay existing = item.getData(DataComponentTypes.TOOLTIP_DISPLAY);
        boolean wasHidden = existing != null && existing.hideTooltip();
        boolean hideCompletely = !wasHidden;
        item.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().hideTooltip(hideCompletely).build());
        player.sendRichMessage("<green>Set tooltip display to <yellow><state></yellow>.",
                Placeholder.unparsed("state", hideCompletely ? "hidden" : "shown"));
    }

    @Executes("tooltip_display")
    @Permission("geckoedit.command.tooltip_display")
    void tooltipDisplay(CommandSender sender, @Executor Player player, DataComponentType typeToHide) {
        ItemStack item = getItem(player);
        if (item == null) return;
        TooltipDisplay existing = item.getData(DataComponentTypes.TOOLTIP_DISPLAY);
        Set<DataComponentType> hidden = existing == null ? new HashSet<>() : existing.hiddenComponents();
        boolean unhid = false;
        if (!hidden.add(typeToHide)) {
            unhid = true;
            hidden.remove(typeToHide);
        }
        item.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay()
                .hiddenComponents(hidden)
                .build());
        player.sendRichMessage("<green><action> <type>.",
                Placeholder.unparsed("action", unhid ? "Unhid" : "Hid"),
                Placeholder.unparsed("type", typeToHide.key().asString()));
    }

    @Executes("tooltip_style")
    @Permission("geckoedit.command.tooltip_style")
    void tooltipStyle(CommandSender sender, @Executor Player player, @StringArg(StringArgType.GREEDY) String styleKey) {
        ItemStack item = getItem(player);
        if (item == null) return;
        Key key = parseKey(styleKey, player);
        if (key == null) return;
        item.setData(DataComponentTypes.TOOLTIP_STYLE, key);
        player.sendRichMessage("<green>Set tooltip style to <key>.",
                Placeholder.unparsed("key", key.asString()));
    }

    @Executes("unbreakable")
    @Permission("geckoedit.command.unbreakable")
    void unbreakable(CommandSender sender, @Executor Player player) {
        ItemStack item = getItem(player);
        if (item == null) return;
        boolean removed = item.hasData(DataComponentTypes.UNBREAKABLE);
        if (removed) {
            item.unsetData(DataComponentTypes.UNBREAKABLE);
        } else {
            item.setData(DataComponentTypes.UNBREAKABLE);
        }
        player.sendRichMessage("<green><state> unbreakability.",
                Placeholder.unparsed("state", removed ? "Removed" : "Added"));
    }

    @Executes("use_cooldown")
    @Permission("geckoedit.command.use_cooldown")
    void useCooldown(CommandSender sender, @Executor Player player, float useCooldown) {
        useCooldown(sender, player, useCooldown, null);
    }

    @Executes("use_cooldown")
    @Permission("geckoedit.command.use_cooldown")
    void useCooldown(CommandSender sender, @Executor Player player, float useCooldown,
                     @StringArg(StringArgType.GREEDY) String cooldownGroup) {
        ItemStack item = getItem(player);
        if (item == null) return;
        UseCooldown.Builder builder = UseCooldown.useCooldown(useCooldown);
        Key key = null;
        if (cooldownGroup != null) {
            key = parseKey(cooldownGroup, player);
            if (key == null) return;
            builder.cooldownGroup(key);
        }
        item.setData(DataComponentTypes.USE_COOLDOWN, builder.build());
        if (key != null) {
            player.sendRichMessage("<green>Set cooldown of <seconds> seconds in group <group>.",
                    Placeholder.unparsed("seconds", useCooldown + ""),
                    Placeholder.unparsed("group", key.asString()));
        } else {
            player.sendRichMessage("<green>Set cooldown of <seconds> seconds.",
                    Placeholder.unparsed("seconds", useCooldown + ""));
        }
    }

    @Executes("use_remainder")
    @Permission("geckoedit.command.use_remainder")
    void useRemainder(CommandSender sender, @Executor Player player) {
        ItemStack item = getItem(player);
        if (item == null) return;
        ItemStack offhand = player.getInventory().getItemInOffHand();
        if (offhand.isEmpty()) {
            player.sendRichMessage("<red>Hold the remainder in your offhand.");
            return;
        }
        item.setData(DataComponentTypes.USE_REMAINDER, UseRemainder.useRemainder(offhand));
    }

}
