/*
 * Copyright or © or Copr. Moribus (2013)
 * Copyright or © or Copr. ProkopyL <prokopylmc@gmail.com> (2015)
 * Copyright or © or Copr. Amaury Carrade <amaury@carrade.eu> (2016 – 2020)
 * Copyright or © or Copr. Vlammar <valentin.jabre@gmail.com> (2019 – 2020)
 *
 * This software is a computer program whose purpose is to allow insertion of
 * custom images in a Minecraft world.
 *
 * This software is governed by the CeCILL-B license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL-B
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-B license and that you accept its terms.
 */

package fr.moribus.imageonmap.commands.maptool;

import fr.moribus.imageonmap.Permissions;
import fr.moribus.imageonmap.commands.IoMCommand;
import fr.moribus.imageonmap.map.ImageMap;
import fr.moribus.imageonmap.map.MapManager;
import fr.moribus.imageonmap.map.PosterMap;
import fr.zcraft.zlib.components.commands.CommandException;
import fr.zcraft.zlib.components.commands.CommandInfo;
import fr.zcraft.zlib.components.i18n.I;
import fr.zcraft.zlib.components.rawtext.RawText;
import fr.zcraft.zlib.components.rawtext.RawTextPart;
import fr.zcraft.zlib.tools.items.ItemStackBuilder;
import fr.zcraft.zlib.tools.text.RawMessage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@CommandInfo (name = "list")
public class ListCommand extends IoMCommand
{
    @Override
    protected void run() throws CommandException
    {
        Player player = playerSender();
        List<ImageMap> mapList = MapManager.getMapList(player.getUniqueId());
        
        if(mapList.isEmpty())
        {
            info(I.t("No map found."));
            return;
        }
        
        info(I.tn("{white}{bold}{0} map found.", "{white}{bold}{0} maps found.", mapList.size()));

        RawTextPart rawText = new RawText("");
        rawText = addMap(rawText, mapList.get(0));

        for(int i = 1, c = mapList.size(); i < c; i++)
        {
            rawText = rawText.then(", ").color(ChatColor.GRAY);
            rawText = addMap(rawText, mapList.get(i));
        }

        RawMessage.send(player, rawText.build());
    }

    private RawTextPart<?> addMap(RawTextPart<?> rawText, ImageMap map)
    {
        final String size = map.getType() == ImageMap.Type.SINGLE ? "1 × 1" : ((PosterMap) map).getColumnCount() + " × " + ((PosterMap) map).getRowCount();

        return rawText
                .then(map.getId())
                .color(ChatColor.WHITE)
                .command(GetCommand.class, map.getId())
                .hover(new ItemStackBuilder(Material.FILLED_MAP)
                                .title(ChatColor.GREEN + "" + ChatColor.BOLD + map.getName())
                                .lore(ChatColor.GRAY + map.getId() + ", " + size)
                                .lore("")
                                .lore(I.t("{white}Click{gray} to get this map"))
                                .hideAttributes()
                                .item()
                );
    }

    @Override
    public boolean canExecute(CommandSender sender)
    {
        return Permissions.LIST.grantedTo(sender);
    }
}
