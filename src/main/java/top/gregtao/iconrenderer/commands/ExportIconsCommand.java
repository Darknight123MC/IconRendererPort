package top.gregtao.iconrenderer.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import top.gregtao.iconrenderer.utils.FileHelper;

import java.io.IOException;
import java.util.List;

public class ExportIconsCommand {
    public static RequiredArgumentBuilder<ServerCommandSource, String> register() {
        return CommandManager.argument("modid", StringArgumentType.string()).executes(ExportIconsCommand::exportIcons);
    }

    public static LiteralArgumentBuilder<ServerCommandSource> registerEAI() {
        return CommandManager.literal("all").executes(ExportIconsCommand::exportAllIcons);
    }

    public static int exportIcons(CommandContext<ServerCommandSource> context) throws CommandException {
            String modId = context.getArgument("modid", String.class);
            try {
                new FileHelper(modId);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 1;
    }

    public static int exportAllIcons(CommandContext<ServerCommandSource> context) throws CommandException {
        List<IModInfo> loadedMods = ModList.get().getMods();
        int i;
        for (i = 0; i < loadedMods.size(); ++i) {
            try {
                new FileHelper(loadedMods.get(i).getModId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 1;
    }
}
