package gecko10000.geckoitemedit;

import gecko10000.geckolib.extensions.MMKt;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.strokkur.commands.StringArgType;
import net.strokkur.commands.annotations.*;
import net.strokkur.commands.annotations.arguments.IntArg;
import net.strokkur.commands.annotations.arguments.StringArg;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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

    @Executes("item_name")
    @Permission("geckoedit.command.item_name")
    void itemName(CommandSender sender, @Executor Player player, @StringArg(StringArgType.GREEDY) String mmString) {
        ItemStack item = getItem(player);
        if (item == null) return;
        Component itemName = MMKt.parseMM(mmString, true);
        item.setData(DataComponentTypes.ITEM_NAME, itemName);
        player.sendRichMessage("<green>Changed item name to <name>.", Placeholder.component("name", itemName));
    }

    @Executes("custom_name")
    @Permission("geckoedit.command.custom_name")
    void customName(CommandSender sender, @Executor Player player, @StringArg(StringArgType.GREEDY) String mmString) {
        ItemStack item = getItem(player);
        if (item == null) return;
        Component customName = MMKt.parseMM(mmString, true);
        item.setData(DataComponentTypes.CUSTOM_NAME, customName);
        player.sendRichMessage("<green>Changed custom name to <name>.", Placeholder.component("name", customName));
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
    void addLore(CommandSender sender, @Executor Player player, @IntArg(min = 1, max = 256) int lineNumber,
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

    @Executes("max_stack_size")
    @Permission("geckoedit.command.max_stack_size")
    void setMaxSize(CommandSender sender, @Executor Player player, @IntArg(min = 1, max = 99) int maxSize) {
        ItemStack item = getItem(player);
        if (item == null) return;
        item.setData(DataComponentTypes.MAX_STACK_SIZE, maxSize);
        player.sendRichMessage(
                "<green>Set max stack size to <yellow><max></yellow>.",
                Placeholder.unparsed("max", maxSize + "")
        );
    }

    @Executes("custom_model_data")
    @Permission("geckoedit.command.custom_model_data")
    void setCustomModelData(CommandSender sender, @Executor Player player, @StringArg(StringArgType.GREEDY) String data) {
        ItemStack item = getItem(player);
        if (item == null) return;
        CustomModelData.Builder cmd = CustomModelData.customModelData();
        List<String> strings = new ArrayList<>();
        List<Float> floats = new ArrayList<>();
        for (String split : data.split(" ")) {
            try {
                Float f = Float.parseFloat(split);
                cmd.addFloat(f);
            } catch (NumberFormatException ex) {
                cmd.addString(split);
            }
        }
        item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, cmd.build());
        player.sendRichMessage("<green>Set custom model data to <data>.",
                Placeholder.unparsed("data", data));
    }

    @Executes("enchantment_glint_override")
    @Permission("geckoedit.command.enchantment_glint_override")
    void setEnchantmentGlintOverride(CommandSender sender, @Executor Player player, boolean enabled) {
        ItemStack item = getItem(player);
        if (item == null) return;
        item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, enabled);
        player.sendRichMessage("<green><yellow><state></yellow> enchantment glint override.",
                Placeholder.unparsed("state", enabled ? "Enabled" : "Disabled"));
    }

}
