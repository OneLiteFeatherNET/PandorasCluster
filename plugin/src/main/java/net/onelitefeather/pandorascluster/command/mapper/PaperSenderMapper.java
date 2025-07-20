package net.onelitefeather.pandorascluster.command.mapper;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.onelitefeather.pandorascluster.command.mapper.types.BlockSource;
import net.onelitefeather.pandorascluster.command.mapper.types.ConsoleSource;
import net.onelitefeather.pandorascluster.command.mapper.types.EntitySource;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.SenderMapper;

@SuppressWarnings("UnstableApiUsage")
public final class PaperSenderMapper implements SenderMapper<CommandSourceStack, CommandSender> {

    @Override
    public @NonNull CommandSender map(@NonNull CommandSourceStack base) {
        return base.getSender();
    }

    @Override
    public @NonNull CommandSourceStack reverse(@NonNull CommandSender mapped) {
        return switch (mapped) {
            case ConsoleCommandSender ignored -> new ConsoleSource();
            case Player player -> new EntitySource(player);
            case Entity entity -> new EntitySource(entity);
            default -> new BlockSource((BlockCommandSender) mapped);
        };
    }
}

