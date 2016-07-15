package net.shortninja.staffplus.server.compatibility.v1_7;

import java.util.Set;

import net.minecraft.server.v1_7_R4.ChatSerializer;
import net.minecraft.server.v1_7_R4.ItemStack;
import net.minecraft.server.v1_7_R4.NBTTagCompound;
import net.minecraft.server.v1_7_R4.PacketPlayOutChat;
import net.shortninja.staffplus.server.compatibility.IProtocol;
import net.shortninja.staffplus.util.lib.json.JsonMessage;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;

public class Protocol_v1_7_R4 implements IProtocol
{
	@Override
	public org.bukkit.inventory.ItemStack addNbtString(org.bukkit.inventory.ItemStack item, String value)
	{
		ItemStack craftItem = CraftItemStack.asNMSCopy(item);
		NBTTagCompound nbtCompound = craftItem.getTag() == null ? new NBTTagCompound() : craftItem.getTag();
		
		nbtCompound.setString(NBT_IDENTIFIER, value);
		craftItem.setTag(nbtCompound);
		
		return CraftItemStack.asCraftMirror(craftItem);
	}
	
	@Override
	public String getNbtString(org.bukkit.inventory.ItemStack item)
	{
		ItemStack craftItem = CraftItemStack.asNMSCopy(item);
		
		if(craftItem == null)
		{
			return "";
		}
		
		NBTTagCompound nbtCompound = craftItem.getTag() == null ? new NBTTagCompound() : craftItem.getTag();
		
		return nbtCompound.getString(NBT_IDENTIFIER);
	}
	
	@Override
	public void registerCommand(String match, Command command)
	{
		((CraftServer) Bukkit.getServer()).getCommandMap().register(match, command);
	}
	
	@Override
	public void listVanish(Player player, boolean shouldEnable)
	{
		if(shouldEnable)
		{
			player.setPlayerListName("");
		}else player.setPlayerListName(player.getName());
	}
	
	@Override
	public void sendHoverableJsonMessage(Set<Player> players, String message, String hoverMessage)
	{
		JsonMessage json = new JsonMessage().append(message).setHoverAsTooltip(hoverMessage).save();
		PacketPlayOutChat packet = new PacketPlayOutChat(ChatSerializer.a(json.getMessage()));
		
		for(Player player : players)
		{
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		}
	}
}