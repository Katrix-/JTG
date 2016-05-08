/**
 * This class was created by <Katrix>. It's distributed as
 * part of the Journey To Gensokyo Mod. Get the Source Code in github:
 * https://github.com/Katrix-/JTG
 *
 * Journey To Gensokyo is Open Source and distributed under the
 * a modifed Botania license: https://github.com/Katrix-/JTG/blob/master/LICENSE.md
 */

package katrix.journeyToGensokyo.plugin.botania;

import katrix.journeyToGensokyo.handler.ConfigHandler;
import katrix.journeyToGensokyo.item.JTGItem;
import katrix.journeyToGensokyo.lib.LibOreDictionary;
import net.minecraft.item.ItemStack;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.recipe.RecipePetals;
import vazkii.botania.common.crafting.ModPetalRecipes;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;
import vazkii.botania.common.lexicon.BLexiconEntry;
import vazkii.botania.common.lexicon.page.PagePetalRecipe;
import vazkii.botania.common.lexicon.page.PageText;

public class JTGBotania {

	public static RecipePetals spiritFlowerRecipe;
	public static LexiconEntry spiritFlowerLexicon;
	public static String SPIRIT_FLOWER = "spiritulip";
	
	public static void preInit() {
		if(ConfigHandler.rtyMode) {
			BotaniaAPI.registerManaInfusionRecipe(new ItemStack(JTGItem.gensokyoNotesItem, 1, 4), new ItemStack(JTGItem.gensokyoNotesItem, 1, 3), 100000);
		}
	}

	public static void init() {

		BotaniaAPI.registerSubTile(SPIRIT_FLOWER, SubTileSpiritFlower.class);

		spiritFlowerRecipe = BotaniaAPI.registerPetalRecipe(ItemBlockSpecialFlower.ofType(SPIRIT_FLOWER), ModPetalRecipes.red, ModPetalRecipes.runeMana,
				ModPetalRecipes.green, ModPetalRecipes.runeWrath, ModPetalRecipes.blue, ModPetalRecipes.runeMana, ModPetalRecipes.yellow,
				ModPetalRecipes.runeFire);
		BotaniaAPI.subtilesForCreativeMenu.add(SPIRIT_FLOWER);

		spiritFlowerLexicon = new BLexiconEntry(SPIRIT_FLOWER, BotaniaAPI.categoryGenerationFlowers);
		spiritFlowerLexicon.setLexiconPages(new PageText("0"), new PagePetalRecipe<>("1", spiritFlowerRecipe));

		BotaniaAPI.addOreWeight(LibOreDictionary.ORE_GENSOKYO, 2500);
		BotaniaAPI.addOreWeightNether(LibOreDictionary.ORE_DEMON, 2500);
	}
}
