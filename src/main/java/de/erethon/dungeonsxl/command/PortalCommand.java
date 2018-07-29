/*
 * Copyright (C) 2012-2018 Frank Baumann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.erethon.dungeonsxl.command;

import de.erethon.caliburn.CaliburnAPI;
import de.erethon.caliburn.item.ExItem;
import de.erethon.caliburn.item.VanillaItem;
import de.erethon.commons.chat.MessageUtil;
import de.erethon.commons.command.DRECommand;
import de.erethon.dungeonsxl.DungeonsXL;
import de.erethon.dungeonsxl.config.DMessage;
import de.erethon.dungeonsxl.global.DPortal;
import de.erethon.dungeonsxl.player.DGamePlayer;
import de.erethon.dungeonsxl.player.DGlobalPlayer;
import de.erethon.dungeonsxl.player.DPermission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class PortalCommand extends DRECommand {

    DungeonsXL plugin = DungeonsXL.getInstance();
    CaliburnAPI caliburn = plugin.getCaliburn();

    public PortalCommand() {
        setCommand("portal");
        setMinArgs(0);
        setMaxArgs(1);
        setHelp(DMessage.HELP_CMD_PORTAL.getMessage());
        setPermission(DPermission.PORTAL.getNode());
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        DGlobalPlayer dGlobalPlayer = plugin.getDPlayers().getByPlayer(player);

        if (dGlobalPlayer instanceof DGamePlayer) {
            MessageUtil.sendMessage(player, DMessage.ERROR_LEAVE_DUNGEON.getMessage());
            return;
        }

        ExItem material = null;

        if (args.length == 2) {
            material = caliburn.getExItem(args[1]);
        }

        if (material == null) {
            material = VanillaItem.NETHER_PORTAL;
        }

        DPortal dPortal = dGlobalPlayer.getPortal();

        if (dPortal == null) {
            dPortal = new DPortal(plugin.getGlobalProtections().generateId(DPortal.class, player.getWorld()), player.getWorld(), material, false);
            dGlobalPlayer.setCreatingPortal(dPortal);
            dPortal.setWorld(player.getWorld());
            dGlobalPlayer.setCachedItem(player.getInventory().getItemInMainHand());
            player.getInventory().setItemInMainHand(VanillaItem.WOODEN_SWORD.toItemStack());
            MessageUtil.sendMessage(player, DMessage.PLAYER_PORTAL_INTRODUCTION.getMessage());

        } else {
            dPortal.delete();
            dGlobalPlayer.setCreatingPortal(null);
            player.getInventory().setItemInMainHand(dGlobalPlayer.getCachedItem());
            dGlobalPlayer.setCachedItem(null);
            MessageUtil.sendMessage(player, DMessage.PLAYER_PORTAL_ABORT.getMessage());
        }
    }

}