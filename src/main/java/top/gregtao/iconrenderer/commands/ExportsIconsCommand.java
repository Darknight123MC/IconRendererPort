package top.gregtao.iconrenderer.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import top.gregtao.iconrenderer.utils.FileHelper;

import java.io.IOException;

public class ExportsIconsCommand {
    public static RequiredArgumentBuilder<ServerCommandSource, String> register() {
        return CommandManager.argument("modid", StringArgumentType.string()).executes(ExportsIconsCommand::exportIcons);
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
}
