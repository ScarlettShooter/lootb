package com.scrl0.lb;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Lootboxes.MOD_ID)
public class Lootboxes {
	
	private static Lootboxes instance;

	public static final String MOD_ID = "lb";
	public Logger LOGGER = LogManager.getLogger();
	
	public Lootboxes() {
		
		instance = this;
		
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
		
	}
	public static Lootboxes getInstance()
    {
        return instance;
    }
	
	public void setup(final FMLCommonSetupEvent event) {
		Config.setup(new File("./config"));
		Config.getInstance().init();
	}
	
	@Mod.EventBusSubscriber(modid=MOD_ID, bus=Mod.EventBusSubscriber.Bus.MOD)
	public static class ReistryEvents {
		@SubscribeEvent
		public static void onItemRegistry(final RegistryEvent.Register<Item> e) {
			Item.Properties p = new Item.Properties().group(ItemGroup.MISC); 
			
			getInstance().LOGGER.info("Registering lootboxes;");
			e.getRegistry().register(new Lootbox(p).setRegistryName(MOD_ID, "lootbox"));
		}
	}
}
