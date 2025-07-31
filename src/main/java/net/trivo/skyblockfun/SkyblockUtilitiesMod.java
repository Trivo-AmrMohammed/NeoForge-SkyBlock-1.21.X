package net.trivo.skyblockfun;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import java.awt.*;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(SkyblockUtilitiesMod.MODID)
public class SkyblockUtilitiesMod {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "skyblockfun";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public SkyblockUtilitiesMod(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @SubscribeEvent
    public void witchNzombieDrop(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        if (event.getSource().getEntity() instanceof ServerPlayer serverPlayer) {
            Registry<Enchantment> enchantmentRegistry = serverPlayer.serverLevel().registryAccess().registryOrThrow(Registries.ENCHANTMENT);
            Holder<Enchantment> lootingHolder = enchantmentRegistry.getHolderOrThrow(Enchantments.LOOTING);
            int lootingLevel = serverPlayer.getMainHandItem().getEnchantmentLevel(lootingHolder);

            int witchBonus = lootingLevel * 5;
            int zombieBonus = lootingLevel * 40;

            int netherWartRange = Math.max(1, 25 - witchBonus);
            int blazeRodRange = Math.max(1, 25 - witchBonus);
            int diamondRange = Math.max(1, 200 - zombieBonus);

            int randomNetherWart = (int)(Math.random() * netherWartRange);
            int randomBlazeRod = (int)(Math.random() * blazeRodRange);
            int randomDiamond = (int)(Math.random() * diamondRange);

            if (entity instanceof Witch) {
                if (randomNetherWart == 1) {
                    event.getDrops().add(new ItemEntity(
                            event.getEntity().level(),
                            event.getEntity().getX(),
                            event.getEntity().getY(),
                            event.getEntity().getZ(),
                            Items.NETHER_WART.getDefaultInstance()));
                }
                if (randomBlazeRod == 1) {
                    event.getDrops().add(new ItemEntity(
                            event.getEntity().level(),
                            event.getEntity().getX(),
                            event.getEntity().getY(),
                            event.getEntity().getZ(),
                            Items.BLAZE_ROD.getDefaultInstance()));
                }
            }

            if (entity instanceof Zombie) {
                if (randomDiamond == 1) {
                    event.getDrops().add(new ItemEntity(
                            event.getEntity().level(),
                            event.getEntity().getX(),
                            event.getEntity().getY(),
                            event.getEntity().getZ(),
                            Items.DIAMOND.getDefaultInstance()));
                }
            }
        }
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }
}
