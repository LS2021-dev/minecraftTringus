package de.ferargent.minecraftTringus;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class commandTabComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, Command command, @NotNull String s, String[] args) {
        if (command.getName().equalsIgnoreCase("sealspawn")) {
            if (args.length == 1) {
                List<String> completions = new ArrayList<>();
                completions.add("true");
                completions.add("false");
                return completions;
            }
        } else if (command.getName().equalsIgnoreCase("setwarp")) {
            if (args.length == 1) {
                List<String> completions = new ArrayList<>();
                completions.add("~");
                completions.add("target");
                return completions;
            } else if (args.length == 2 || args.length == 3) {
                List<String> completions = new ArrayList<>();
                completions.add("~");
                return completions;
            }
        }
        return null;
    }
}
