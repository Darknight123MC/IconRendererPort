package top.gregtao.iconrenderer;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.gregtao.iconrenderer.commands.ExportIconsCommand;

@Mod("iconr")
public class IconRenderer {
    public static final Logger logger = LoggerFactory.getLogger("Icon Renderer");

    public IconRenderer() {
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void registerCommands(RegisterClientCommandsEvent event) {
        final CommandDispatcher<ServerCommandSource> dispatcher = event.getDispatcher();
        dispatcher.register(CommandManager.literal("exporticons").then(ExportIconsCommand.register()));
        dispatcher.register(CommandManager.literal("exporticons").then(ExportIconsCommand.registerEAI()));
    }
}
