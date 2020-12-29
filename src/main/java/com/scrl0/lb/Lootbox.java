package com.scrl0.lb;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class Lootbox extends Item {

	public Lootbox(Item.Properties builder) {
		super(builder);
	}
	
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
	      ItemStack itemstack = playerIn.getHeldItem(handIn);

	      playerIn.addStat(Stats.ITEM_USED.get(this));
	      if (!playerIn.abilities.isCreativeMode) {
	         itemstack.shrink(1);
	      }
	      
	      ItemStack[] randomItems = getRandomItems();
	      
	      for(int i = 0; i < randomItems.length; i++) {
	    	  playerIn.inventory.addItemStackToInventory(randomItems[i]);
	      }
	      /*Give experience(WIP)*/
	      /*playerIn.giveExperiencePoints(10);*/
	      
	      return ActionResult.resultSuccess(itemstack);
	   }
	
	public ItemStack[] getRandomItems() throws IllegalArgumentException {
		
		Map<ItemStack[], Double> itemMap = new HashMap<ItemStack[], Double>();
		
		double sumWeight = 0.0d;
		
		ItemStack[][] items = Config.getInstance().outputV();
		double[] itemWeight = Config.getInstance().outputR();
		
		for(int i = 0; i < items.length; i++) {
			itemMap.put(items[i], itemWeight[i]);
			sumWeight += itemWeight[i];
		}
		
		if(sumWeight <= 0) {
			throw new IllegalArgumentException("Argument must be positive");
		}
		
		double random = Math.random()*sumWeight, weight = 0;

		for(Entry<ItemStack[], Double> e : itemMap.entrySet()) {
			weight += e.getValue().doubleValue();
			if(random < weight) {
				return e.getKey();
			}
		}
		return null;
	}

}














