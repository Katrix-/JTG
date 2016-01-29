/**
 * This class was created by <Katrix>, base on a class from Touhou Items Mod. It's distributed as
 * part of the Journey To Gensokyo Mod. Get the Source Code in github:
 * https://github.com/Katrix-/JTG
 *
 * Journey To Gensokyo is Open Source and distributed under the
 * a modifed Botania license: https://github.com/Katrix-/JTG/blob/master/LICENSE.md
 */

package katrix.journeyToGensokyo.plugin.thsc.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import cpw.mods.fml.common.registry.EntityRegistry;
import katrix.journeyToGensokyo.JourneyToGensokyo;
import katrix.journeyToGensokyo.handler.ConfigHandler;
import katrix.journeyToGensokyo.lib.LibEntityName;
import katrix.journeyToGensokyo.lib.LibMobID;
import katrix.journeyToGensokyo.lib.LibMod;
import katrix.journeyToGensokyo.lib.LibSpecialShotId;
import katrix.journeyToGensokyo.util.LogHelper;
import katrix.journeyToGensokyo.util.MathHelperJTG;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import thKaguyaMod.DanmakuConstants;
import thKaguyaMod.ShotData;
import thKaguyaMod.THShotLib;
import thKaguyaMod.entity.living.EntityDanmakuMob;
import thKaguyaMod.entity.shot.EntityOnmyoudama;
import thKaguyaMod.entity.spellcard.EntitySpellCard;
import thKaguyaMod.init.THKaguyaConfig;
import thKaguyaMod.init.THKaguyaItems;
import thKaguyaMod.item.ItemTHShot;

public class EntityReimuHostile extends EntityDanmakuMob implements IMerchant {
	
    private EntityPlayer buyingPlayer;
    private MerchantRecipeList buyingList;
	//public static final Map merchantSellingList = new HashMap();

	public EntityReimuHostile(World world) {
		super(world);

		setSize(1.0F, 1.8F);

		experienceValue = 250;

		setDanmakuMobName("Reimu Hakurei");
		this.setSpecies(EntityDanmakuMob.SPECIES_HUMAN);

		setDanmakuPattern(NORMAL_ATTACK01);
		setMaxHP(74.0F);
		setHealth(74.0F);
		setSpeed(0.4F);
		setAttackDistance(50.0D);
		setDetectionDistance(0.0D);
		setFlyingHeight(0);
		isFlyingMode = false;

		isSpellCardMode = false;
		attackInterval = 0;
	}

	@Override
	public int getUsingSpellCardNo() {
		switch (getDanmakuPattern()) {
			case SPELLCARD_ATTACK01:
				return EntitySpellCard.SC_REIMU_MusouFuuin;
			default:
				return -1;
		}
	}

	@Override
	protected void onDeathUpdate() {
		switch (getDanmakuPattern()) {
			case NORMAL_ATTACK01:
				setFlyingHeight(2);
				moveDanmakuAttack(SPELLCARD_ATTACK01, 40, 60.0F, 160);
				break;
			case SPELLCARD_ATTACK01:
				moveDanmakuAttack(ATTACK_END, 90, 0.0F, 160);
				break;
			default:
				if (deathTime % 6 == 0) {
					THShotLib.explosionEffect(worldObj, posX, posY, posZ, 1.0F + deathTime * 0.1F);
				}
				super.onDeathUpdate();
				break;
		}
	}


	@Override
	public void danmakuPattern(int level) {
		Vec3 look = getLookVec();
		switch (getDanmakuPattern()) {
			case NORMAL_ATTACK01:
				danmaku01(look, level);
				break;
			case SPELLCARD_ATTACK01:
				spellcard01(look, level);
				break;
			default:
				break;
		}
	}

	private void danmaku01(Vec3 angle, int level) {
		if (attackCounter == 2 || attackCounter == 4 || attackCounter == 6) {
			THShotLib.createCircleShot(this, pos(), angle, 0.3D, ShotData.shot(DanmakuConstants.FORM_AMULET, DanmakuConstants.WHITE, 0.2f, 0.5f), 8 * level);
			THShotLib.createCircleShot(this, pos(), THShotLib.angle(rotationYaw + attackCounter, rotationPitch), 0.12D,
					ShotData.shot(DanmakuConstants.FORM_AMULET, DanmakuConstants.RED, 0.2f, 0.5f), 8 * level);
			for (int i = 4; i <= 6; i++) {
				THShotLib.createShot(this, pos(), THShotLib.angle(rotationYaw + attackCounter * 2, rotationPitch), 0.05D * i,
						ShotData.shot(DanmakuConstants.FORM_AMULET, DanmakuConstants.WHITE, 0.3f, 0.5f));
			}
		}

		if (attackCounter < 30 && attackCounter % 2 == 0) {
			if (!worldObj.isRemote) {
				worldObj.spawnEntityInWorld(new EntityOnmyoudama(worldObj, this, this,
						THShotLib.pos_Distance(pos(), THShotLib.angle(rotationYaw + attackCounter * 12, rotationPitch), 2.0D),
						THShotLib.angle(rotationYaw + attackCounter * 12, rotationPitch), 0F, THShotLib.rotate_Default(), 0F, 9999, 0.5D, 1.8D, 0.0D,
						THShotLib.gravity_Default(), DanmakuConstants.RED, 1.2F, 2.0F, 0, 180, DanmakuConstants.BOUND04));//陰陽玉を出現させる
			}
		}

		if (attackCounter == 30) {
			move(THShotLib.angle_Random(), 0.6D, 10);
		}

		if (attackCounter == 42 || attackCounter == 44 || attackCounter == 46) {
			THShotLib.createCircleShot(this, pos(), angle, 0.15D, ShotData.shot(DanmakuConstants.FORM_AMULET, DanmakuConstants.WHITE, 0.2f, 0.5f), 8 * level);
			THShotLib.createCircleShot(this, pos(), THShotLib.angle(rotationYaw + (attackCounter - 40), rotationPitch), 0.25D,
					ShotData.shot(DanmakuConstants.FORM_AMULET, DanmakuConstants.RED, 0.2f, 0.5f), 8 * level);
			for (int i = 4; i <= 6; i++) {
				THShotLib.createShot(this, pos(), THShotLib.angle(rotationYaw + (attackCounter - 40) * 2, rotationPitch), 0.05D * i,
						ShotData.shot(DanmakuConstants.FORM_AMULET, DanmakuConstants.RED, 0.3f, 0.5f));
			}
		}

		if (attackCounter == 48 || attackCounter == 50 || attackCounter == 52) {
			THShotLib.createCircleShot(this, pos(), angle, 0.3D, ShotData.shot(DanmakuConstants.FORM_AMULET, DanmakuConstants.WHITE, 0.15f, 0.5f), 8 * level);
			THShotLib.createCircleShot(this, pos(), THShotLib.angle(rotationYaw + (attackCounter - 48), rotationPitch), 0.12D,
					ShotData.shot(DanmakuConstants.FORM_AMULET, DanmakuConstants.RED, 0.2f, 0.5f), 8 * level);
		}

		if (attackCounter < 70 && attackCounter > 40 && attackCounter % 2 == 0) {
			if (!worldObj.isRemote) {
				worldObj.spawnEntityInWorld(new EntityOnmyoudama(worldObj, this, this,
						THShotLib.pos_Distance(pos(), THShotLib.angle(rotationYaw - attackCounter * 12, rotationPitch), 2.0D),
						THShotLib.angle(rotationYaw - attackCounter * 12, rotationPitch), 0F, THShotLib.rotate_Default(), 0F, 9999, 0.5D, 1.8D, 0.0D,
						THShotLib.gravity_Default(), DanmakuConstants.RED, 1.2F, 2.0F, 0, 180, DanmakuConstants.BOUND04));//陰陽玉を出現させる
			}
		}

		if (attackCounter == 70) {
			move(THShotLib.angle_Random(), 0.6D, 10);
		}

		if (attackCounter >= 80) {
			attackCounter = 0;
		}
	}

	private void spellcard01(Vec3 angle, int level) {

		if (attackCounter == 1) {
			useSpellCard(EntitySpellCard.SC_REIMU_MusouFuuin);
		}
		
		double shotGap = 0.06D;

		for (int k = 1; k <= 8; k++) {
			if (attackCounter == 20 + k * (level / 2)) {
				for (int i = 1; i <= 5; i++) {
					for (int j = -1; j <= 1; j++) {
						THShotLib.playShotSound(this);
						THShotLib.createShot(this, pos(), THShotLib.angle(rotationYaw + j * 6.0f + 360 / 8 * k, rotationPitch), shotGap * i,
								ShotData.shot(DanmakuConstants.FORM_AMULET, DanmakuConstants.WHITE, 0.2f, 0.5f, 0, 20, LibSpecialShotId.FANTASY_SEAL01));
					}
				}
			}
		}

		if (attackCounter >= 100) {
			attackCounter = 0;
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource damageSource, float amount) {
		if (!damageSource.isMagicDamage()) {
			amount *= 0.5F;
		}
		isFlyingMode = true;
		setFlyingHeight(4);
		return super.attackEntityFrom(damageSource, amount);
	}

	@Override
	protected boolean canFairyCall() {
		return false;
	}

	/**
	 * Reduces damage, depending on potions
	 */
	@Override
	protected float applyPotionDamageCalculations(DamageSource damageSource, float damage) {
		damage = super.applyPotionDamageCalculations(damageSource, damage);

		if (isEntityInvulnerable()) {
			damage = (float)(damage * 0.05D);
		}

		return damage;
	}

	@Override
	protected void dropFewItems(boolean hasBeenAttackedByPlayer, int lootingLevel) {
		super.dropFewItems(hasBeenAttackedByPlayer, lootingLevel);

		if (hasBeenAttackedByPlayer && isSpellCardAttack()) {
			int j = 40;//this.rand.nextInt(15) + this.rand.nextInt(1 + par2);
			int k;
			Vec3 vec3;
			float yaw, pitch;

			for (k = 0; k < j; k += 2) {
				yaw = 360F / j * k;
				pitch = (float)(MathHelperJTG.sin((float)(yaw / 180F * Math.PI * 4F)) * 20F - 60F);
				vec3 = THShotLib.getVecFromAngle(yaw, pitch, 1.0F);
				this.dropPointItem(this.pos(), vec3);
				yaw = 360F / j * (k + 1);
				pitch = (float)(MathHelperJTG.cos((float)(yaw / 180F * Math.PI * 4F)) * 20F - 60F);
				vec3 = THShotLib.getVecFromAngle(yaw, pitch, 1.0F);
				this.dropPowerUpItem(this.pos(), vec3);
			}

			dropShotItem(ItemTHShot.TALISMAN, 17 + rand.nextInt(2) + lootingLevel, 5, 32, DanmakuConstants.RED, 0, 0, 2);
		}
		if (hasBeenAttackedByPlayer && getDanmakuPattern() == SPELLCARD_ATTACK01) {
			this.dropItem(THKaguyaItems.hakurei_miko_stick, 1);
			this.dropItem(THKaguyaItems.yin_yang_orb, 1);
		}
	}

	@Override
	public boolean doesEntityNotTriggerPressurePlate() {
		return true;
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 1;
	}

	@Override
	public boolean getCanSpawnHere() {
		if (rand.nextInt(100) < THKaguyaConfig.fairySpawnRate && rand.nextInt(100) < 90 || super.getCanSpawnHere() == false)
			return false;

		int range = 64;
		@SuppressWarnings("unchecked")
		List<EntityReimuHostile> reimus = worldObj.getEntitiesWithinAABB(EntityReimuHostile.class,
				AxisAlignedBB.getBoundingBox(posX - range, posY - range, posZ - range, posX + range + 1, posY + range + 1, posZ + range + 1));
		if (reimus.size() >= 1)
			return false;

		return worldObj.difficultySetting != EnumDifficulty.PEACEFUL;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void entityInit() {
		super.entityInit();
        buyingList = new MerchantRecipeList();
        buyingList.add(new MerchantRecipe(new ItemStack(THKaguyaItems.shot_material, 32), THKaguyaItems.red_pearl, THKaguyaItems.homing_amulet));
        buyingList.add(new MerchantRecipe(new ItemStack(THKaguyaItems.shot_material, 32), THKaguyaItems.blue_pearl, THKaguyaItems.diffusion_amulet));
        Collections.shuffle(buyingList);
	}
	
    public boolean interact(EntityPlayer player)
    {
        ItemStack itemstack = player.inventory.getCurrentItem();
        boolean flag = itemstack != null && itemstack.getItem() == Items.spawn_egg;

        if (!flag && this.isEntityAlive() && !player.isSneaking() && !isFlyingMode)
        {
            if (!this.worldObj.isRemote)
            {
                this.setCustomer(player);
                player.displayGUIMerchant(this, StatCollector.translateToLocal("entity." + LibMod.MODID + "." + LibEntityName.REIMU_HOSTILE +  ".name"));
            }

            return true;
        }
        else
        {
            return super.interact(player);
        }
    }
    
    public void writeEntityToNBT(NBTTagCompound tag)
    {
        super.writeEntityToNBT(tag);
        if (this.buyingList != null)
        {
            tag.setTag("Offers", this.buyingList.getRecipiesAsTags());
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound tag)
    {
        super.readEntityFromNBT(tag);
        if (tag.hasKey("Offers", 10))
        {
            NBTTagCompound nbttagcompound1 = tag.getCompoundTag("Offers");
            this.buyingList = new MerchantRecipeList(nbttagcompound1);
        }
    }

	@Override
	public void setCustomer(EntityPlayer player) {
		this.buyingPlayer = player;		
	}

	@Override
	public EntityPlayer getCustomer() {
		return buyingPlayer;
	}

	@Override
	public MerchantRecipeList getRecipes(EntityPlayer player) {
		LogHelper.info(buyingList.get(1));
		return this.buyingList;
	}

	@Override
	public void setRecipes(MerchantRecipeList p_70930_1_) {}

	@Override
	public void useRecipe(MerchantRecipe recipe) {
        recipe.incrementToolUses();
	}

	@Override
	public void func_110297_a_(ItemStack stack) {}
	
	public static void postInit() {

		EntityRegistry.registerModEntity(EntityReimuHostile.class, LibEntityName.REIMU_HOSTILE, LibMobID.REIMU_HOSTILE, JourneyToGensokyo.instance, 80, 1, true);

		List<BiomeGenBase> spawnbiomes = new ArrayList<BiomeGenBase>(Arrays.asList(BiomeDictionary.getBiomesForType(Type.FOREST)));

		if (THKaguyaConfig.spawnBoss && ConfigHandler.newBossesSpawn) {
			EntityRegistry.addSpawn(EntityReimuHostile.class, 1, 1, 1, EnumCreatureType.monster, spawnbiomes.toArray(new BiomeGenBase[0]));
		}
	}
}