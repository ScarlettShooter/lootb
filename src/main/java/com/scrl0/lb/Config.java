package com.scrl0.lb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.google.gson.stream.JsonReader;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class Config {
	
	private static Config instance;

	private final File jsonFile;
	
	private int counter = -1;
	
	private ArrayList<ArrayList<String>> itemsBuffer = new ArrayList<>();
	private ArrayList<Double> chanceBuffer = new ArrayList<>();
		
	public Config(File configRoot) {
		this.jsonFile = new File(configRoot.getAbsolutePath() + "/lootboxes.json");
	}
	
	public static void setup(File configRoot) {
		instance = new Config(configRoot);
	}
	
	public static Config getInstance() {
		if(instance == null) {
			throw new RuntimeException("Config hadn't been initilized yet");
		}
		return instance;
	}
	
	public void init()
    {
        try
        {
            InputStream jsonStream = new FileInputStream(jsonFile);
            this.read(jsonStream);
        }
        catch (IOException e)
        {
            Lootboxes.getInstance().LOGGER.info("Could not find lootboxes.json. Downloading it from GitHub...");
			/*WIP
			BufferedInputStream in = new BufferedInputStream(
			        new URL("")
			                .openStream());
			Files.copy(in, Paths.get(jsonFile.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
			Lootboxes.getInstance().LOGGER.info("Done downloading lootboxes.json from GitHub!");
			InputStream jsonStream = new FileInputStream(jsonFile);
			this.read(jsonStream);*/
        }
    }
	
	private void read(InputStream in) throws IOException {
		try (JsonReader jReader = new JsonReader(new InputStreamReader(in))) 
		{
			jReader.beginObject();
			
			while(jReader.hasNext()) 
			{
				String name = jReader.nextName();
				if(name!=null) 
				{
					jReader.beginArray();
					counter++;
					ArrayList<String> items = new ArrayList<>();
					double chance = 0.0;
					while(jReader.hasNext()) 
					{
						jReader.beginObject();
						while(jReader.hasNext()) 
						{
							String subName = jReader.nextName();
							if(subName.equalsIgnoreCase("items")) 
							{
								jReader.beginArray();
								while(jReader.hasNext()) 
								{
									items.add(jReader.nextString());
								}
								Lootboxes.getInstance().LOGGER.debug("mod detected items" + items);
								jReader.endArray();
							} else if(subName.equalsIgnoreCase("chance")) 
							{
								chance = jReader.nextDouble();
								Lootboxes.getInstance().LOGGER.debug("mod detected chances " + chance);
							} else {
								Lootboxes.getInstance().LOGGER.info("Unknown property found in lootboxes.json file. Skipping it.");
								jReader.skipValue();
							}
							
						}
						if (itemLine(items) == null)
                        {
							Lootboxes.getInstance().LOGGER.info("Could not register lootbox due to some error. Please report to the mod author that they are not initializing their loot when they should..");
                        }
						jReader.endObject();
						chanceBuffer.add(counter, chance);
						itemsBuffer.add(counter, items);
						Lootboxes.getInstance().LOGGER.debug("Outputing return arrays. Items: " + itemsBuffer + ". Chances: " + chanceBuffer);
					}
					jReader.endArray();	
				}  else 
				{
					jReader.skipValue();
				}
			}
			jReader.endObject();
		}
		catch (Exception e ) 
		{
			Lootboxes.getInstance().LOGGER.error("There was a parsing error with the lootboxes.json file. Please check for drastic syntax errors and check it at https://jsonlint.com/");
			Lootboxes.getInstance().LOGGER.error(e.getMessage());
			e.printStackTrace();
		}
	}
	/*Converting items to line*/
	private ItemStack[] itemLine(ArrayList<String> items) {
		ArrayList<ItemStack> stack = new ArrayList<>();
		
		if(items !=null) {
			for (int i = 0; i < items.size(); i++)
	        {
				ItemStack state = fromItemString(items.get(i));
	            if (state == null)
	            {
	                BlockState stateB = fromBlockString(items.get(i));
	                if(stateB == null) 
	                {
	                	return null;
	                }
	                stack.add(i, new ItemStack(stateB.getBlock()));
	            }
	            stack.add(i, new ItemStack(state.getItem()));
	        }
		}
		ItemStack[] r = new ItemStack[stack.size()];
		for(int i = 0; i < stack.size(); i++) {
			r[i] = stack.get(i);
		}
		return r;
	}
	/*Unying 2 lines
	private ItemStack[] itemList(ArrayList<ItemStack> items, ArrayList<ItemStack> blocks) {
		ArrayList<ItemStack> result = new ArrayList<>();
		
		for(int i = 0; i < items.size(); i++) {
			result.add(i, items.get(i));
		}
		for(int i = items.size() - 1; i < blocks.size() + items.size() - 1; i++) {
			result.add(i, blocks.get(i-items.size()+1));
		}
		ItemStack[] stack = new ItemStack[result.size()];
		for(int i = 0; i < result.size(); i++) {
			stack[i] = result.get(i);
		}
		
		return stack;
	}*/
	/*Converting from string to BlockState*/
	private BlockState fromBlockString(String iBlockState)
    {
        String[] parts = iBlockState.split(":");
        if (parts.length == 2)
        {
            Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(parts[0], parts[1]));
            return b.getDefaultState();
        }
        return null;
    }
	/*Converting from string to ItemStack*/
	private ItemStack fromItemString(String iItems)
    {
        String[] parts = iItems.split(":");
        if (parts.length == 2)
        {
            Item i = ForgeRegistries.ITEMS.getValue(new ResourceLocation(parts[0], parts[1]));
            return i.getDefaultInstance();
        }
        return null;
    }
	
	public ItemStack[][] outputV() {
		ItemStack[][] returnValue = new ItemStack[counter+1][];
		for(int i = 0; i <= counter; i++) {
					returnValue[i] = itemLine(itemsBuffer.get(i));
		}
		return returnValue;
	}
	public double[] outputR() {
		double[] returnChance = new double[counter+1];
		for(int i = 0; i <= counter; i++) {
			returnChance[i] = chanceBuffer.get(i);
		}
		return returnChance;
	}
}
