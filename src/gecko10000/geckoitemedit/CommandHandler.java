package gecko10000.geckoitemedit;

import gecko10000.geckolib.extensions.MMKt;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.strokkur.commands.StringArgType;
import net.strokkur.commands.annotations.*;
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

    @Executes("itemname")
    @Permission("geckoedit.command.itemname")
    void itemName(CommandSender sender, @Executor Player player, @StringArg(StringArgType.GREEDY) String mmString) {
        ItemStack item = getItem(player);
        if (item == null) return;
        Component itemName = MMKt.parseMM(mmString, true);
        item.setData(DataComponentTypes.ITEM_NAME, itemName);
        sender.sendRichMessage("<green>Changed item name to <name>.", Placeholder.component("name", itemName));
    }

    @Executes("customname")
    @Permission("geckoedit.command.customname")
    void customName(CommandSender sender, @Executor Player player, @StringArg(StringArgType.GREEDY) String mmString) {
        ItemStack item = getItem(player);
        if (item == null) return;
        Component customName = MMKt.parseMM(mmString, true);
        item.setData(DataComponentTypes.CUSTOM_NAME, customName);
        sender.sendRichMessage("<green>Changed custom name to <name>.", Placeholder.component("name", customName));
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
    }

    @Executes("lore")
    @Permission("geckoedit.command.lore")
    void addLore(CommandSender sender, @Executor Player player, int line,
                 @StringArg(StringArgType.GREEDY) String mmString) {
        ItemStack item = getItem(player);
        if (item == null) return;
        List<Component> lore = item.lore();
        if (lore == null) lore = new ArrayList<>();
        for (int i = lore.size(); i < line; i++) {
            lore.add(Component.empty());
        }
        Component loreLine = MMKt.parseMM(mmString, true);
        lore.set(line - 1, loreLine);
        item.lore(lore);
    }

}
