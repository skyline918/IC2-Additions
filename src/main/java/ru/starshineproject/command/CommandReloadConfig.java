package ru.starshineproject.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import ru.starshineproject.IC2Additions;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class CommandReloadConfig extends CommandBase {

    @Override
    public @Nonnull
    String getName() {
        return "ic2additions-config-reload";
    }

    @Override
    public @Nonnull
    String getUsage(@Nonnull ICommandSender sender) {
        return "/ic2additions-config-reload";
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        ConfigManager.sync(IC2Additions.MOD_ID, Config.Type.INSTANCE);
    }

}
