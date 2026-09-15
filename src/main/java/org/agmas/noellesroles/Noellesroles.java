package org.agmas.noellesroles;

import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.api.WatheRoles;
import dev.doctor4t.wathe.api.event.AllowPlayerPunching;
import dev.doctor4t.wathe.cca.*;
import dev.doctor4t.wathe.client.gui.RoleAnnouncementTexts;
import dev.doctor4t.wathe.entity.PlayerBodyEntity;
import dev.doctor4t.wathe.api.event.AllowPlayerDeath;
import dev.doctor4t.wathe.api.event.CanSeePoison;
import dev.doctor4t.wathe.api.event.ShouldDropOnDeath;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.game.GameFunctions;
import dev.doctor4t.wathe.index.WatheBlocks;
import dev.doctor4t.wathe.index.WatheItems;
import dev.doctor4t.wathe.index.WatheParticles;
import dev.doctor4t.wathe.index.WatheSounds;
import dev.doctor4t.wathe.util.AnnounceWelcomePayload;
import dev.doctor4t.wathe.util.ShopEntry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import org.agmas.harpymodloader.Harpymodloader;
import org.agmas.harpymodloader.component.WorldModifierComponent;
import org.agmas.harpymodloader.config.HarpyModLoaderConfig;
import org.agmas.harpymodloader.events.ModdedRoleAssigned;
import org.agmas.harpymodloader.events.ModifierAssigned;
import org.agmas.harpymodloader.events.ResetPlayerEvent;
import org.agmas.harpymodloader.modifiers.HMLModifiers;
import org.agmas.harpymodloader.modifiers.Modifier;
import org.agmas.noellesroles.bartender.BartenderPlayerComponent;
import org.agmas.noellesroles.config.NoellesRolesConfig;
import org.agmas.noellesroles.coroner.BodyDeathReasonComponent;
import org.agmas.noellesroles.executioner.ExecutionerPlayerComponent;
import org.agmas.noellesroles.framing.ConspiratorShopEntry;
import org.agmas.noellesroles.framing.FramingShopEntry;
import org.agmas.noellesroles.jester.JesterPlayerComponent;
import org.agmas.noellesroles.morphling.MorphlingPlayerComponent;
import org.agmas.noellesroles.packet.*;
import org.agmas.noellesroles.phantom.PhantomPlayerComponent;
import org.agmas.noellesroles.recaller.RecallerPlayerComponent;
import org.agmas.noellesroles.swapper.SwapperPlayerComponent;
import org.agmas.noellesroles.voodoo.VoodooPlayerComponent;
import org.agmas.noellesroles.vulture.VulturePlayerComponent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Noellesroles implements ModInitializer {

    public static String MOD_ID = "noellesroles";


    public static Identifier JESTER_ID = Identifier.of(MOD_ID, "jester");
    public static Identifier MORPHLING_ID = Identifier.of(MOD_ID, "morphling");
    public static Identifier CONDUCTOR_ID = Identifier.of(MOD_ID, "conductor");
    public static Identifier BARTENDER_ID = Identifier.of(MOD_ID, "bartender");
    public static Identifier NOISEMAKER_ID = Identifier.of(MOD_ID, "noisemaker");
    public static Identifier PHANTOM_ID = Identifier.of(MOD_ID, "phantom");
    public static Identifier SWAPPER_ID = Identifier.of(MOD_ID, "swapper");
    public static Identifier GUESSER_ID = Identifier.of(MOD_ID, "guesser");
    public static Identifier VOODOO_ID = Identifier.of(MOD_ID, "voodoo");
    public static Identifier TRAPPER_ID = Identifier.of(MOD_ID, "trapper");
    public static Identifier CORONER_ID = Identifier.of(MOD_ID, "coroner");
    public static Identifier RECALLER_ID = Identifier.of(MOD_ID, "recaller");
    public static Identifier MIMIC_ID = Identifier.of(MOD_ID, "mimic");
    public static Identifier EXECUTIONER_ID = Identifier.of(MOD_ID, "executioner");
    public static Identifier VULTURE_ID = Identifier.of(MOD_ID, "vulture");
    public static Identifier BETTER_VIGILANTE_ID = Identifier.of(MOD_ID, "better_vigilante");
    public static Identifier TINY_ID = Identifier.of(MOD_ID, "tiny");
    public static Identifier CHAMELEON_ID = Identifier.of(MOD_ID, "chameleon");
    public static Identifier GRAVEROBBER_ID = Identifier.of(MOD_ID, "graverobber");
    public static Identifier FEATHER_ID = Identifier.of(MOD_ID, "feather");
    public static Identifier THE_INSANE_DAMNED_PARANOID_KILLER_OF_DOOM_DEATH_DESTRUCTION_AND_WAFFLES_ID = Identifier.of(MOD_ID, "the_insane_damned_paranoid_killer");
    public static Identifier CELEBRITY_ID = Identifier.of(MOD_ID, "celebrity");
    public static Identifier SIXTH_SENSE_ID = Identifier.of(MOD_ID, "sixth_sense");
    public static Identifier CONSPIRATOR_ID = Identifier.of(MOD_ID, "conspirator");
    public static Identifier IRON_WILLED_ID = Identifier.of(MOD_ID, "iron_willed");
    public static Identifier BELLRINGER_ID = Identifier.of(MOD_ID, "bellringer");
    public static Identifier HEALTHY_ID = Identifier.of(MOD_ID, "healthy");
    public static Identifier SERIAL_KILLER_ID = Identifier.of(MOD_ID, "serial_killer");

    public static HashMap<Role, RoleAnnouncementTexts.RoleAnnouncementText> roleRoleAnnouncementTextHashMap = new HashMap<>();
    public static Role JESTER = WatheRoles.registerRole(new Role(JESTER_ID,new Color(200, 13, 156).getRGB() ,false,false, Role.MoodType.FAKE,WatheRoles.CIVILIAN.getMaxSprintTime(),true));
    public static Role MORPHLING =WatheRoles.registerRole(new Role(MORPHLING_ID, new Color(170, 2, 86).getRGB(),false,true, Role.MoodType.FAKE,WatheRoles.CIVILIAN.getMaxSprintTime()*2,true));
    public static Role CONDUCTOR =WatheRoles.registerRole(new Role(CONDUCTOR_ID, new Color(255, 205, 84).getRGB(),true,false, Role.MoodType.REAL,WatheRoles.CIVILIAN.getMaxSprintTime(),false));

    public static Role BARTENDER =WatheRoles.registerRole(new Role(BARTENDER_ID, new Color(217,241,240).getRGB(),true,false, Role.MoodType.REAL,WatheRoles.CIVILIAN.getMaxSprintTime(),false));
    public static Role NOISEMAKER =WatheRoles.registerRole(new Role(NOISEMAKER_ID, new Color(200, 255, 0).getRGB(),true,false, Role.MoodType.REAL,WatheRoles.CIVILIAN.getMaxSprintTime(),false));
    public static Role SWAPPER = WatheRoles.registerRole(new Role(SWAPPER_ID, new Color(77, 12, 213).getRGB(),false,true, Role.MoodType.FAKE,WatheRoles.CIVILIAN.getMaxSprintTime()*2,true));
    public static Role PHANTOM =WatheRoles.registerRole(new Role(PHANTOM_ID, new Color(129, 3, 3, 192).getRGB(),false,true, Role.MoodType.FAKE,WatheRoles.CIVILIAN.getMaxSprintTime()*2,true));

    public static Role CONSPIRATOR =WatheRoles.registerRole(new Role(CONSPIRATOR_ID, new Color(64, 44, 36).getRGB(),false,false,Role.MoodType.FAKE, WatheRoles.CIVILIAN.getMaxSprintTime(),true));
    public static Role VOODOO =WatheRoles.registerRole(new Role(VOODOO_ID, new Color(171, 172, 241).getRGB(),true,false,Role.MoodType.REAL, WatheRoles.CIVILIAN.getMaxSprintTime(),false));
    public static Role THE_INSANE_DAMNED_PARANOID_KILLER_OF_DOOM_DEATH_DESTRUCTION_AND_WAFFLES =WatheRoles.registerRole(new Role(THE_INSANE_DAMNED_PARANOID_KILLER_OF_DOOM_DEATH_DESTRUCTION_AND_WAFFLES_ID, new Color(255, 0, 0, 192).getRGB(),false,true, Role.MoodType.FAKE,WatheRoles.CIVILIAN.getMaxSprintTime()*2,true));
    public static Role TRAPPER =WatheRoles.registerRole(new Role(TRAPPER_ID, new Color(155, 218, 197).getRGB(),true,false,Role.MoodType.REAL, WatheRoles.CIVILIAN.getMaxSprintTime(),false));
    public static Role CORONER =WatheRoles.registerRole(new Role(CORONER_ID, new Color(154, 154, 154).getRGB(),true,false,Role.MoodType.REAL, WatheRoles.CIVILIAN.getMaxSprintTime(),false));

    public static Role EXECUTIONER =WatheRoles.registerRole(new Role(EXECUTIONER_ID, new Color(177, 59, 28).getRGB(),false,false,Role.MoodType.FAKE, WatheRoles.CIVILIAN.getMaxSprintTime(),true));
    public static Role RECALLER = WatheRoles.registerRole(new Role(RECALLER_ID, new Color(158, 255, 255).getRGB(),true,false,Role.MoodType.REAL, WatheRoles.CIVILIAN.getMaxSprintTime(),false));

    public static Role VULTURE =WatheRoles.registerRole(new Role(VULTURE_ID, new Color(177, 102, 5).getRGB(),false,false,Role.MoodType.FAKE, WatheRoles.CIVILIAN.getMaxSprintTime(),true));
    public static Role BETTER_VIGILANTE =WatheRoles.registerRole(new Role(BETTER_VIGILANTE_ID, new Color(0, 255, 255).getRGB(),true,false,Role.MoodType.REAL, WatheRoles.CIVILIAN.getMaxSprintTime(),false));
    public static Role MIMIC = WatheRoles.registerRole(new Role(MIMIC_ID, new Color(255, 137, 155).getRGB(),true,false,Role.MoodType.REAL, WatheRoles.CIVILIAN.getMaxSprintTime(),false));


    public static Modifier TINY = HMLModifiers.registerModifier(new Modifier(TINY_ID, new Color(255, 166, 0).getRGB(), new ArrayList<>(List.of(MORPHLING)),null,false,false));
    public static Modifier CHAMELEON = HMLModifiers.registerModifier(new Modifier(CHAMELEON_ID, new Color(198, 255, 137, 255).getRGB(),null,null,false,false));
    public static Modifier FEATHER = HMLModifiers.registerModifier(new Modifier(FEATHER_ID, new Color(255, 236, 161, 255).getRGB(),null,null,false,false));
    public static Modifier GUESSER = HMLModifiers.registerModifier(new Modifier(GUESSER_ID, new Color(158, 43, 25, 255).getRGB(),new ArrayList<>(List.of(THE_INSANE_DAMNED_PARANOID_KILLER_OF_DOOM_DEATH_DESTRUCTION_AND_WAFFLES)),null,true,false));
    public static Modifier CELEBRITY = HMLModifiers.registerModifier(new Modifier(CELEBRITY_ID, new Color(174, 4, 109, 255).getRGB(), null, null, true, false));
    public static Modifier GRAVEROBBER = HMLModifiers.registerModifier(new Modifier(GRAVEROBBER_ID, new Color(174, 95, 95, 255).getRGB(),null,new ArrayList<>(List.of(VULTURE, EXECUTIONER, CONSPIRATOR, JESTER)),false,true));
    public static Modifier SIXTH_SENSE = HMLModifiers.registerModifier(new Modifier(SIXTH_SENSE_ID, new Color(244, 201, 152, 255).getRGB(), new ArrayList<>(List.of(WatheRoles.VIGILANTE, JESTER, VULTURE, EXECUTIONER, BARTENDER, CONSPIRATOR)), null, false, true));
    public static Modifier IRON_WILLED = HMLModifiers.registerModifier(new Modifier(IRON_WILLED_ID, new Color(197, 197, 197).getRGB(), new ArrayList<>(List.of(JESTER, VULTURE, EXECUTIONER, CONSPIRATOR)), null, false, true));
    public static Modifier BELLRINGER = HMLModifiers.registerModifier(new Modifier(BELLRINGER_ID, new Color(250, 220, 126).getRGB(), new ArrayList<>(List.of(JESTER, VULTURE, EXECUTIONER, CONSPIRATOR)), null, false, true));
    public static Modifier HEALTHY = HMLModifiers.registerModifier(new Modifier(HEALTHY_ID, new Color(106, 241, 108).getRGB(), new ArrayList<>(List.of(JESTER, VULTURE, EXECUTIONER, CONSPIRATOR)), null, false, true));
    public static Modifier SERIAL_KILLER = HMLModifiers.registerModifier(new Modifier(SERIAL_KILLER_ID, new Color(12, 74, 191, 255).getRGB(), null, null, true, false));

    public static final CustomPayload.Id<MorphC2SPacket> MORPH_PACKET = MorphC2SPacket.ID;
    public static final CustomPayload.Id<SwapperC2SPacket> SWAP_PACKET = SwapperC2SPacket.ID;
    public static final CustomPayload.Id<AbilityC2SPacket> ABILITY_PACKET = AbilityC2SPacket.ID;
    public static final CustomPayload.Id<VultureEatC2SPacket> VULTURE_PACKET = VultureEatC2SPacket.ID;
    public static final CustomPayload.Id<VoodooTrackC2SPacket> VOODOO_PACKET = VoodooTrackC2SPacket.ID;
    public static final CustomPayload.Id<GuessC2SPacket> GUESS_PACKET = GuessC2SPacket.ID;
    public static final ArrayList<Role> VANNILA_ROLES = new ArrayList<>();
    public static final ArrayList<Identifier> VANNILA_ROLE_IDS = new ArrayList<>();
    public static final ArrayList<Role> KILLER_SIDED_NEUTRALS = new ArrayList<>();
    public static final ArrayList<Modifier> KILLER_MODIFIERS = new ArrayList<>();

    public static ArrayList<ShopEntry> FRAMING_ROLES_SHOP = new ArrayList<>();
    public static ArrayList<ShopEntry> CONSPIRATOR_SHOP = new ArrayList<>();

    public static Identifier VOODOO_MAGIC_DEATH_REASON = Identifier.of(Noellesroles.MOD_ID, "voodoo");

    @Override
    public void onInitialize() {
        VANNILA_ROLES.add(WatheRoles.KILLER);
        VANNILA_ROLES.add(WatheRoles.VIGILANTE);
        VANNILA_ROLES.add(WatheRoles.CIVILIAN);
        VANNILA_ROLES.add(WatheRoles.LOOSE_END);

        KILLER_SIDED_NEUTRALS.add(VULTURE);
        KILLER_SIDED_NEUTRALS.add(JESTER);
        KILLER_SIDED_NEUTRALS.add(EXECUTIONER);
        KILLER_SIDED_NEUTRALS.add(CONSPIRATOR);

        KILLER_MODIFIERS.add(CELEBRITY);
        KILLER_MODIFIERS.add(GUESSER);
        KILLER_MODIFIERS.add(SERIAL_KILLER);

        VANNILA_ROLE_IDS.add(WatheRoles.LOOSE_END.identifier());
        VANNILA_ROLE_IDS.add(WatheRoles.VIGILANTE.identifier());
        VANNILA_ROLE_IDS.add(WatheRoles.CIVILIAN.identifier());
        VANNILA_ROLE_IDS.add(WatheRoles.KILLER.identifier());

        FRAMING_ROLES_SHOP.add(new FramingShopEntry(WatheItems.LOCKPICK.getDefaultStack(), 50, ShopEntry.Type.TOOL));
        FRAMING_ROLES_SHOP.add(new FramingShopEntry(WatheItems.CROWBAR.getDefaultStack(), 40, ShopEntry.Type.TOOL));
        FRAMING_ROLES_SHOP.add(new FramingShopEntry(ModItems.DELUSION_VIAL.getDefaultStack(), 30, ShopEntry.Type.POISON));
        FRAMING_ROLES_SHOP.add(new FramingShopEntry(WatheItems.NOTE.getDefaultStack(), 5, ShopEntry.Type.TOOL));
        FRAMING_ROLES_SHOP.add(new FramingShopEntry(WatheItems.FIRECRACKER.getDefaultStack(), 10, ShopEntry.Type.TOOL));
        FRAMING_ROLES_SHOP.add(new FramingShopEntry(ModItems.SHORTFUSE_FIRECRACKER.getDefaultStack(), 10, ShopEntry.Type.TOOL));
        FRAMING_ROLES_SHOP.add(new FramingShopEntry(WatheItems.MALICE_BOOSTER.getDefaultStack(), 20, ShopEntry.Type.TOOL));

        CONSPIRATOR_SHOP.add(new ConspiratorShopEntry(WatheItems.LOCKPICK.getDefaultStack(), 70, ShopEntry.Type.TOOL));
        CONSPIRATOR_SHOP.add(new ConspiratorShopEntry(ModItems.DELUSION_VIAL.getDefaultStack(), 30, ShopEntry.Type.POISON));
        CONSPIRATOR_SHOP.add(new ConspiratorShopEntry(WatheItems.NOTE.getDefaultStack(), 10, ShopEntry.Type.TOOL));
        CONSPIRATOR_SHOP.add(new ConspiratorShopEntry(WatheItems.FIRECRACKER.getDefaultStack(), 10, ShopEntry.Type.TOOL));
        CONSPIRATOR_SHOP.add(new ConspiratorShopEntry(ModItems.SHORTFUSE_FIRECRACKER.getDefaultStack(), 10, ShopEntry.Type.TOOL));
        CONSPIRATOR_SHOP.add(new ConspiratorShopEntry(WatheItems.BODY_BAG.getDefaultStack(), 100, ShopEntry.Type.TOOL));
        CONSPIRATOR_SHOP.add(new ConspiratorShopEntry(WatheItems.CROWBAR.getDefaultStack(), 50, ShopEntry.Type.TOOL));
        CONSPIRATOR_SHOP.add(new ConspiratorShopEntry(WatheItems.MALICE_BOOSTER.getDefaultStack(), 40, ShopEntry.Type.TOOL));
        CONSPIRATOR_SHOP.add(new ConspiratorShopEntry(WatheItems.BLACKOUT.getDefaultStack(), 200, ShopEntry.Type.TOOL) {
            @Override
            public boolean onBuy(@NotNull PlayerEntity player) {
                return PlayerShopComponent.useBlackout(player);
            }
        });

        NoellesRolesConfig.HANDLER.load();
        ModItems.init();
        NoellesRolesEntities.init();

        Harpymodloader.setRoleMaximum(CONDUCTOR_ID,1);
        Harpymodloader.setRoleMaximum(EXECUTIONER_ID,1);
        Harpymodloader.setRoleMaximum(VULTURE_ID,1);
        Harpymodloader.setRoleMaximum(JESTER_ID,1);
        Harpymodloader.setRoleMaximum(BETTER_VIGILANTE_ID,1);
        Harpymodloader.setRoleMaximum(BARTENDER_ID,1);
        Harpymodloader.setRoleMaximum(CONSPIRATOR_ID,1);

        Harpymodloader.MODIFIER_MAX.put(SIXTH_SENSE_ID, 1);
        Harpymodloader.MODIFIER_MAX.put(TINY_ID, 1);
        Harpymodloader.MODIFIER_MAX.put(IRON_WILLED_ID, 1);
        Harpymodloader.MODIFIER_MAX.put(BELLRINGER_ID, 1);

        PayloadTypeRegistry.playC2S().register(MorphC2SPacket.ID, MorphC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(AbilityC2SPacket.ID, AbilityC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(SwapperC2SPacket.ID, SwapperC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(VultureEatC2SPacket.ID, VultureEatC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(VoodooTrackC2SPacket.ID, VoodooTrackC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(GuessC2SPacket.ID, GuessC2SPacket.CODEC);

        registerEvents();

        registerPackets();

        if (NoellesRolesConfig.HANDLER.instance().allowCivillianGuessers) {
            GUESSER.killerOnly = false;
        }
        //NoellesRolesEntities.init();

    }

    EntityAttributeModifier tinyModifier = new EntityAttributeModifier(Identifier.of(MOD_ID, "tiny_modifier"), -0.15, EntityAttributeModifier.Operation.ADD_VALUE);



    public void registerEvents() {
        //
        // Bartender / Jester Psycho Invulnerability
        //
        AllowPlayerDeath.EVENT.register(((playerEntity, killer,identifier) -> {
            if (identifier == GameConstants.DeathReasons.FELL_OUT_OF_TRAIN) return true;

            BartenderPlayerComponent bartenderPlayerComponent = BartenderPlayerComponent.KEY.get(playerEntity);
            if (bartenderPlayerComponent.armor > 0) {
                playerEntity.getWorld().playSound(playerEntity, playerEntity.getBlockPos(), WatheSounds.ITEM_PSYCHO_ARMOUR, SoundCategory.MASTER, 5.0F, 1.0F);
                bartenderPlayerComponent.armor--;
                return false;
            }

            return true;
        }));
        //
        // Mimic & Executioner backfire
        //
        AllowPlayerDeath.EVENT.register(((playerEntity, killer,identifier) -> {
            GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(playerEntity.getWorld());
            if (identifier.equals(GameConstants.DeathReasons.FELL_OUT_OF_TRAIN) && killer != null) {
                if (gameWorldComponent.isRole(killer, MIMIC) && gameWorldComponent.isInnocent(playerEntity)) {
                    GameFunctions.killPlayer(killer, true, null, Identifier.of(MOD_ID, "modded_backfire"));
                }
            }
            if (identifier.equals(GameConstants.DeathReasons.GUN) && killer != null) {
                if (gameWorldComponent.isRole(killer, EXECUTIONER) && ExecutionerPlayerComponent.KEY.get(killer).target != playerEntity.getUuid()) {
                    GameFunctions.killPlayer(killer, true, null, Identifier.of(MOD_ID, "modded_backfire"));
                }
            }
            return true;
        }));
        AllowPlayerPunching.EVENT.register(((playerEntity, playerEntity1) -> {
            GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(playerEntity.getWorld());
            return playerEntity.getMainHandStack().isOf(ModItems.FAKE_KNIFE);
        }));
        ModifierAssigned.EVENT.register(((playerEntity, modifier) -> {
            if (modifier.equals(TINY)) {
                playerEntity.getAttributeInstance(EntityAttributes.GENERIC_SCALE).removeModifier(tinyModifier);
                playerEntity.getAttributeInstance(EntityAttributes.GENERIC_SCALE).addPersistentModifier(tinyModifier);
            }
            if (modifier.equals(FEATHER)) {
                playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, StatusEffectInstance.INFINITE, 0, true, false));
            }
            if (modifier.equals(CELEBRITY)) {
                PlayerShopComponent playerShopComponent = PlayerShopComponent.KEY.get(playerEntity);
                playerShopComponent.setBalance(playerShopComponent.balance - 25);
            }
            if (modifier.equals(GRAVEROBBER)) {
                playerEntity.giveItemStack(ModItems.MODIFIER_STEALER.getDefaultStack());
                playerEntity.giveItemStack(ModItems.MODIFIER_STEALER.getDefaultStack());
            }
        }));
        ResetPlayerEvent.EVENT.register(((playerEntity) -> {
            playerEntity.removeStatusEffect(StatusEffects.SLOW_FALLING);
            playerEntity.getAttributeInstance(EntityAttributes.GENERIC_SCALE).removeModifier(tinyModifier);
        }));
        CanSeePoison.EVENT.register((player)->{
            GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(player.getWorld());
            Role role = gameWorldComponent.getRole((PlayerEntity) player);
            if (gameWorldComponent.isRole((PlayerEntity) player, Noellesroles.BARTENDER) || Noellesroles.KILLER_SIDED_NEUTRALS.contains(role)) {
                return true;
            }
            return false;
        });
        ShouldDropOnDeath.EVENT.register(((itemStack,identifier) -> {
            return itemStack.isOf(ModItems.MASTER_KEY);
        }));
        ModdedRoleAssigned.EVENT.register((player,role)->{
            AbilityPlayerComponent abilityPlayerComponent = (AbilityPlayerComponent) AbilityPlayerComponent.KEY.get(player);
            GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(player.getWorld());
            abilityPlayerComponent.cooldown = NoellesRolesConfig.HANDLER.instance().generalCooldownTicks;
            if (role.equals(EXECUTIONER)) {
                ExecutionerPlayerComponent executionerPlayerComponent = (ExecutionerPlayerComponent) ExecutionerPlayerComponent.KEY.get(player);
                executionerPlayerComponent.won = false;
                executionerPlayerComponent.reset();
                executionerPlayerComponent.sync();
            }
            if (role.equals(VULTURE)) {
                // Give Vulture Body bag or Lock pick
                ArrayList<Item> VultureItems = new ArrayList<>(List.of(WatheItems.BODY_BAG, WatheItems.LOCKPICK));
                Collections.shuffle((VultureItems));
                player.giveItemStack(VultureItems.getFirst().getDefaultStack());

                VulturePlayerComponent vulturePlayerComponent = VulturePlayerComponent.KEY.get(player);
                vulturePlayerComponent.reset();
                if (player.getWorld().getPlayers().size() < 10){
                    // If player count is 9-
                    vulturePlayerComponent.bodiesRequired = 2;
                } else if (player.getWorld().getPlayers().size() >= 10 && player.getWorld().getPlayers().size() < 15){
                    // If player count is 10-14
                    vulturePlayerComponent.bodiesRequired = 3;
                } else {
                    // If player count is 15+
                    vulturePlayerComponent.bodiesRequired = 4;
                }
                vulturePlayerComponent.sync();
            }
            if (role.equals(BETTER_VIGILANTE)) {
                player.giveItemStack(WatheItems.GRENADE.getDefaultStack());
            }
            if (role.equals(MIMIC)) {
                player.giveItemStack(ModItems.FAKE_KNIFE.getDefaultStack());
            }
            if (role.equals(JESTER)) {
                player.giveItemStack(ModItems.FAKE_KNIFE.getDefaultStack());
                player.giveItemStack(ModItems.FAKE_REVOLVER.getDefaultStack());

                JesterPlayerComponent jesterPlayerComponent = JesterPlayerComponent.KEY.get(player);
                jesterPlayerComponent.reset();
                if (player.getWorld().getPlayers().size() < 10){
                    // If player count is 9-
                    jesterPlayerComponent.jestRequired = 15;
                } else if (player.getWorld().getPlayers().size() >= 10 && player.getWorld().getPlayers().size() < 15){
                    // If player count is 10-14
                    jesterPlayerComponent.jestRequired = 20;
                } else {
                    // If player count is 15+
                    jesterPlayerComponent.jestRequired = 25;
                }
                jesterPlayerComponent.sync();
            }
            if (role.equals(CONDUCTOR)) {
                player.giveItemStack(ModItems.MASTER_KEY.getDefaultStack());
            }
            if (role.equals(CONSPIRATOR)) {
                player.giveItemStack(WatheItems.LOCKPICK.getDefaultStack());
            }
            if (role.equals(BARTENDER)) {
                ArrayList<Item> BartenderItems = new ArrayList<>(List.of(WatheItems.OLD_FASHIONED, WatheItems.MARTINI, WatheItems.MOJITO, WatheItems.CHAMPAGNE, WatheItems.COSMOPOLITAN));
                Collections.shuffle((BartenderItems));
                player.giveItemStack(BartenderItems.getFirst().getDefaultStack());
                player.giveItemStack(BartenderItems.getLast().getDefaultStack());
            }
        });
        ServerTickEvents.END_SERVER_TICK.register(((server) -> {

            // If <8 players, add no accomplices.
            if (server.getPlayerManager().getCurrentPlayerCount() < 8) {
                for (Role role : KILLER_SIDED_NEUTRALS) {
                    if (!HarpyModLoaderConfig.HANDLER.instance().disabled.contains(role.identifier().toString())) {
                        Harpymodloader.setRoleMaximum(role, 0);
                    }
                }

            // If 8-12 players, add 1 accomplice.
            } else if (server.getPlayerManager().getCurrentPlayerCount() >= 8 && server.getPlayerManager().getCurrentPlayerCount() < 13) {
                ArrayList<Role> ENABLED_NEUTRALS = new ArrayList<>();
                for (Role role : KILLER_SIDED_NEUTRALS) {
                    if (!HarpyModLoaderConfig.HANDLER.instance().disabled.contains(role.identifier().toString())) {
                        Harpymodloader.setRoleMaximum(role, 0);
                        ENABLED_NEUTRALS.add(role);
                    }
                }
                // If only 1 killer, remove conspirator from spawning
                if (server.getPlayerManager().getCurrentPlayerCount() < 10) {
                    ENABLED_NEUTRALS.remove(Noellesroles.CONSPIRATOR);
                }
                Collections.shuffle(ENABLED_NEUTRALS);
                if (!ENABLED_NEUTRALS.isEmpty()) {
                    Harpymodloader.setRoleMaximum(ENABLED_NEUTRALS.getFirst(), 1);
                }

            // If 13-17 players, add 2 accomplices.
            } else if (server.getPlayerManager().getCurrentPlayerCount() >= 13 && server.getPlayerManager().getCurrentPlayerCount() < 17) {
                ArrayList<Role> ENABLED_NEUTRALS = new ArrayList<>();
                for (Role role : KILLER_SIDED_NEUTRALS) {
                    if (!HarpyModLoaderConfig.HANDLER.instance().disabled.contains(role.identifier().toString())) {
                        Harpymodloader.setRoleMaximum(role, 0);
                        ENABLED_NEUTRALS.add(role);
                    }
                }
                Collections.shuffle(ENABLED_NEUTRALS);
                if (!ENABLED_NEUTRALS.isEmpty()) {
                    Harpymodloader.setRoleMaximum(ENABLED_NEUTRALS.getFirst(), 1);
                    Harpymodloader.setRoleMaximum(ENABLED_NEUTRALS.getLast(), 1);
                }

            // If 17+ players, I don't care about accomplice count.
            } else {
                for (Role role : KILLER_SIDED_NEUTRALS) {
                    if (!HarpyModLoaderConfig.HANDLER.instance().disabled.contains(role.identifier().toString())) {
                        Harpymodloader.setRoleMaximum(role, 1);
                    }
                }
            }

            // If <10 players, don't always guarantee guesser.
            if (server.getPlayerManager().getCurrentPlayerCount() < 10) {
                ArrayList<Identifier> killerModifiers = new ArrayList<>();
                for (Modifier modifier : KILLER_MODIFIERS) {
                    if (!HarpyModLoaderConfig.HANDLER.instance().disabled.contains(modifier.identifier().toString())) {
                        Harpymodloader.MODIFIER_MAX.put(modifier.identifier, 0);
                        killerModifiers.add(modifier.identifier);
                    }
                }
                Collections.shuffle(killerModifiers);
                Harpymodloader.MODIFIER_MAX.put(killerModifiers.getFirst(), 1);
            } else {
                for (Modifier modifier : KILLER_MODIFIERS) {
                    if (!HarpyModLoaderConfig.HANDLER.instance().disabled.contains(modifier.identifier().toString())) {
                        Harpymodloader.MODIFIER_MAX.put(modifier.identifier, 1);
                    }
                }
            }
        }));
        ServerTickEvents.END_WORLD_TICK.register((world) -> {
            Integer baseBalanceToAdd = GameConstants.PASSIVE_MONEY_TICKER.apply(world.getTime());
            if (baseBalanceToAdd != null && baseBalanceToAdd > 0) {
                WorldModifierComponent worldModifierComponent = WorldModifierComponent.KEY.get(world);
                GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(world);
                for (ServerPlayerEntity player : world.getPlayers()) {
                    if (GameFunctions.isPlayerAliveAndSurvival(player) && worldModifierComponent.isModifier(player, CELEBRITY) || gameWorldComponent.isRole(player, CONSPIRATOR)) {
                        int nearbyPlayers = 0;
                        for (ServerPlayerEntity otherPlayer : world.getPlayers()) {
                            if (otherPlayer != player && GameFunctions.isPlayerAliveAndSurvival(otherPlayer)) {
                                Role otherRole = gameWorldComponent.getRole(otherPlayer);
                                boolean isActualKiller = otherRole != null && (otherRole.canUseKiller() || KILLER_SIDED_NEUTRALS.contains(otherRole));
                                if (!isActualKiller && player.squaredDistanceTo(otherPlayer) <= 5.0 * 5.0) {
                                    nearbyPlayers++;
                                }
                            }
                        }
                        if (nearbyPlayers > 2) {
                            if (worldModifierComponent.isModifier(player, CELEBRITY)) {
                                int extraGold = Math.min((nearbyPlayers - 2) * 5, 15);
                                PlayerShopComponent.KEY.get(player).addToBalance(extraGold);
                            }
                            if (gameWorldComponent.isRole(player, CONSPIRATOR)) {
                                int extraGold = Math.min((nearbyPlayers - 1) * 5, 20);
                                PlayerShopComponent.KEY.get(player).addToBalance(extraGold);
                            }
                        }
                    }
                }
            }
        });
        if (!NoellesRolesConfig.HANDLER.instance().shitpostRoles) {
            HarpyModLoaderConfig.HANDLER.load();
            if (!HarpyModLoaderConfig.HANDLER.instance().disabled.contains(BETTER_VIGILANTE_ID.toString())) {
                HarpyModLoaderConfig.HANDLER.instance().disabled.add(BETTER_VIGILANTE_ID.toString());
            }
            if (!HarpyModLoaderConfig.HANDLER.instance().disabled.contains(THE_INSANE_DAMNED_PARANOID_KILLER_OF_DOOM_DEATH_DESTRUCTION_AND_WAFFLES_ID.toString())) {
                HarpyModLoaderConfig.HANDLER.instance().disabled.add(THE_INSANE_DAMNED_PARANOID_KILLER_OF_DOOM_DEATH_DESTRUCTION_AND_WAFFLES_ID.toString());
            }
            HarpyModLoaderConfig.HANDLER.save();
        }


    }


    public void registerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(Noellesroles.MORPH_PACKET, (payload, context) -> {
            GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(context.player().getWorld());
            AbilityPlayerComponent abilityPlayerComponent = (AbilityPlayerComponent) AbilityPlayerComponent.KEY.get(context.player());

            if (payload.player() == null) return;
            if (context.player().getWorld().getPlayerByUuid(payload.player()) == null) return;

            if (gameWorldComponent.isRole(context.player(), MORPHLING)) {
                PlayerPsychoComponent playerPsychoComponent = PlayerPsychoComponent.KEY.get(context.player());
                PlayerShopComponent playerShopComponent = PlayerShopComponent.KEY.get(context.player());
                MorphlingPlayerComponent morphlingPlayerComponent = MorphlingPlayerComponent.KEY.get(context.player());
                if (playerPsychoComponent.psychoTicks != 0) {
                    context.player().sendMessage(Text.translatable("tip.noellesroles.psycho_ability_disable").formatted(Formatting.DARK_RED), true);
                } else {
                    if (playerShopComponent.balance < morphlingPlayerComponent.morphCost) {
                        context.player().sendMessage(Text.translatable("tip.noellesroles.ability_cannot_afford", morphlingPlayerComponent.morphCost).formatted(Formatting.DARK_RED), true);
                    } else {
                        morphlingPlayerComponent.startMorph(payload.player());
                        playerShopComponent.setBalance(playerShopComponent.balance - morphlingPlayerComponent.morphCost);
                    }
                }
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(Noellesroles.VULTURE_PACKET, (payload, context) -> {
            GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(context.player().getWorld());
            AbilityPlayerComponent abilityPlayerComponent = (AbilityPlayerComponent) AbilityPlayerComponent.KEY.get(context.player());

            if (gameWorldComponent.isRole(context.player(), VULTURE) && GameFunctions.isPlayerAliveAndSurvival(context.player())) {
                if (abilityPlayerComponent.cooldown > 0) return;
                abilityPlayerComponent.sync();
                List<PlayerBodyEntity> playerBodyEntities = context.player().getWorld().getEntitiesByType(TypeFilter.equals(PlayerBodyEntity.class), context.player().getBoundingBox().expand(10), (playerBodyEntity -> {
                    return playerBodyEntity.getUuid().equals(payload.playerBody());
                }));
                if (!playerBodyEntities.isEmpty()) {
                    BodyDeathReasonComponent bodyDeathReasonComponent = BodyDeathReasonComponent.KEY.get(playerBodyEntities.getFirst());
                    if (!bodyDeathReasonComponent.vultured) {
                        abilityPlayerComponent.cooldown = GameConstants.getInTicks(0, 20);
                        VulturePlayerComponent vulturePlayerComponent = VulturePlayerComponent.KEY.get(context.player());
                        vulturePlayerComponent.bodiesEaten++;
                        vulturePlayerComponent.sync();
                        context.player().getServerWorld().playSound(null, context.player().getBlockPos(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.MASTER, 1.0F, 0.5F);
                        context.player().addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 40, 2));
                        if (vulturePlayerComponent.bodiesEaten >= vulturePlayerComponent.bodiesRequired) {
                            ArrayList<Role> shuffledKillerRoles = new ArrayList<>(WatheRoles.ROLES);
                            shuffledKillerRoles.removeIf(role -> Harpymodloader.NON_MURDER_ROLES.contains(role) || Harpymodloader.VANNILA_ROLES.contains(role) || !role.canUseKiller() || HarpyModLoaderConfig.HANDLER.instance().disabled.contains(role.identifier().toString()));
                            if (shuffledKillerRoles.isEmpty()) shuffledKillerRoles.add(WatheRoles.KILLER);
                            Collections.shuffle(shuffledKillerRoles);

                            PlayerShopComponent playerShopComponent = (PlayerShopComponent) PlayerShopComponent.KEY.get(context.player());
                            gameWorldComponent.addRole(context.player(),shuffledKillerRoles.getFirst());
                            ModdedRoleAssigned.EVENT.invoker().assignModdedRole(context.player(),shuffledKillerRoles.getFirst());
                            playerShopComponent.setBalance(Math.clamp(playerShopComponent.balance, 125, 175));
                            PlayerPoisonComponent.KEY.get(context.player()).reset();
                            if (Harpymodloader.VANNILA_ROLES.contains(gameWorldComponent.getRole(context.player()))) {
                                ServerPlayNetworking.send((ServerPlayerEntity) context.player(), new AnnounceWelcomePayload(RoleAnnouncementTexts.ROLE_ANNOUNCEMENT_TEXTS.indexOf(WatheRoles.KILLER), gameWorldComponent.getAllKillerTeamPlayers().size(), 0));
                            } else {
                                ServerPlayNetworking.send((ServerPlayerEntity) context.player(), new AnnounceWelcomePayload(RoleAnnouncementTexts.ROLE_ANNOUNCEMENT_TEXTS.indexOf(Harpymodloader.autogeneratedAnnouncements.get(gameWorldComponent.getRole(context.player()))), gameWorldComponent.getAllKillerTeamPlayers().size(), 0));
                            }
                        }

                        bodyDeathReasonComponent.vultured = true;
                        bodyDeathReasonComponent.sync();
                    }
                }

            }
        });
        ServerPlayNetworking.registerGlobalReceiver(Noellesroles.VOODOO_PACKET, (payload, context) -> {
            GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(context.player().getWorld());
            if (gameWorldComponent.isRole(context.player(), VOODOO) && GameFunctions.isPlayerAliveAndSurvival(context.player())) {

                AbilityPlayerComponent abilityPlayerComponent = AbilityPlayerComponent.KEY.get(context.player());
                if (abilityPlayerComponent.cooldown <= 0) {
                    VoodooPlayerComponent voodooPlayerComponent = VoodooPlayerComponent.KEY.get(context.player());
                    PlayerShopComponent playerShopComponent = PlayerShopComponent.KEY.get(context.player());
                    if (!voodooPlayerComponent.hasTarget) {

                        if (playerShopComponent.balance >= voodooPlayerComponent.abilityCost) {
                            if (payload.player() != null && !payload.player().equals(context.player().getUuid())) {
                                playerShopComponent.setBalance(playerShopComponent.balance - voodooPlayerComponent.abilityCost);
                                voodooPlayerComponent.setTarget(payload.player());
                                context.player().sendMessage(Text.translatable("tip.voodoo.hint.target_chosen").withColor(Noellesroles.VOODOO.color()), true);
                                abilityPlayerComponent.cooldown = GameConstants.getInTicks(0, 10);
                            } else {
                                context.player().sendMessage(Text.translatable("tip.voodoo.hint.invalid_target_track").withColor(Noellesroles.VOODOO.color()), true);
                            }
                        } else {
                            context.player().sendMessage(Text.translatable("tip.noellesroles.ability_cannot_afford", voodooPlayerComponent.abilityCost).withColor(Noellesroles.VOODOO.color()), true);
                        }
                    } else {
                        PlayerEntity targetPlayer = context.player().getWorld().getPlayerByUuid(voodooPlayerComponent.target);
                        if (!targetPlayer.isSpectator() && context.player().squaredDistanceTo(targetPlayer) <= 50 * 50) {
                            if (gameWorldComponent.canUseKillerFeatures(targetPlayer) || KILLER_SIDED_NEUTRALS.contains(gameWorldComponent.getRole(targetPlayer))) {
                                targetPlayer.sendMessage(Text.translatable("tip.voodoo.hint.evil_tracked_warning").formatted(Formatting.RED), true);
                            }
                            voodooPlayerComponent.startGlow();
                            context.player().sendMessage(Text.translatable("tip.voodoo.hint.tracking_active").withColor(Noellesroles.VOODOO.color()), true);
                            abilityPlayerComponent.cooldown = GameConstants.getInTicks(0, 90);
                        } else {
                            voodooPlayerComponent.abilityFail();
                            playerShopComponent.addToBalance(voodooPlayerComponent.abilityCost/2);
                            context.player().sendMessage(Text.translatable("tip.voodoo.hint.tracking_failed").withColor(Noellesroles.VOODOO.color()), true);
                            abilityPlayerComponent.cooldown = GameConstants.getInTicks(0, 20);
                        }
                    }
                    voodooPlayerComponent.sync();
                }
                abilityPlayerComponent.sync();
            }
        });

        // Nested if statements is the only way I can think to do this. Holy does this look horrible though.
        ServerPlayNetworking.registerGlobalReceiver(Noellesroles.SWAP_PACKET, (payload, context) -> {
            GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(context.player().getWorld());
            SwapperPlayerComponent swapperPlayerComponent = SwapperPlayerComponent.KEY.get(context.player());
            if (gameWorldComponent.isRole(context.player(), SWAPPER)) {
                if (payload.player() != null) {
                    if (context.player().getWorld().getPlayerByUuid(payload.player()) != null) {
                        if (payload.player2() != null) {
                            if (context.player().getWorld().getPlayerByUuid(payload.player2()) != null) {
                                PlayerShopComponent playerShopComponent = PlayerShopComponent.KEY.get(context.player());
                                if (gameWorldComponent.isPsychoActive()) {
                                    context.player().sendMessage(Text.literal("You cannot swap players while psycho is active.").formatted(Formatting.DARK_RED), true);
                                } else {
                                    if (playerShopComponent.balance < swapperPlayerComponent.swapCost) {
                                        context.player().sendMessage(Text.translatable("tip.noellesroles.ability_cannot_afford", swapperPlayerComponent.swapCost).formatted(Formatting.DARK_RED), true);
                                    } else {
                                        AbilityPlayerComponent abilityPlayerComponent = AbilityPlayerComponent.KEY.get(context.player());
                                        PlayerEntity player1 = context.player().getWorld().getPlayerByUuid(payload.player2());
                                        PlayerEntity player2 = context.player().getWorld().getPlayerByUuid(payload.player());
                                        Block playerBlock1 = player1.getWorld().getBlockState(player1.getBlockPos().down()).getBlock();
                                        Block playerBlock2 = player2.getWorld().getBlockState(player2.getBlockPos().down()).getBlock();
                                        if (player1.getPose() != EntityPose.STANDING || player2.getPose() != EntityPose.STANDING ||
                                                player1.isClimbing() || player2.isClimbing() || player1.isCreative() || player2.isCreative() ||
                                                (playerBlock1 == WatheBlocks.GOLD_LEDGE) || playerBlock1 == Blocks.AIR ||
                                                (playerBlock2 == WatheBlocks.GOLD_LEDGE) || playerBlock2 == Blocks.AIR) {
                                            context.player().sendMessage(Text.literal("Ability failed. One or both of the players could not be teleported.").formatted(Formatting.DARK_RED), true);
                                            abilityPlayerComponent.cooldown = GameConstants.getInTicks(0, 20);
                                            abilityPlayerComponent.sync();
                                        } else {
                                            if (player1 != context.player() && player2 != context.player()) {
                                                context.player().sendMessage(Text.literal("Swapping " + player1.getDisplayName().getString() + " with " + player2.getDisplayName().getString() + ".").formatted(Formatting.DARK_RED), true);
                                            }
                                            playerShopComponent.setBalance(playerShopComponent.balance - swapperPlayerComponent.swapCost);
                                            player1.sendMessage(Text.literal("You feel the air around you warp. You're being swapped!").formatted(Formatting.RED), true);
                                            player2.sendMessage(Text.literal("You feel the air around you warp. You're being swapped!").formatted(Formatting.RED), true);
                                            swapperPlayerComponent.getPlayerLocations(player1, player2);
                                            swapperPlayerComponent.setSwapTime();
                                            abilityPlayerComponent.cooldown = GameConstants.getInTicks(0, 40);
                                            abilityPlayerComponent.sync();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });


        ServerPlayNetworking.registerGlobalReceiver(Noellesroles.GUESS_PACKET, (payload, context) -> {
            GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(context.player().getWorld());
            WorldModifierComponent worldModifierComponent = WorldModifierComponent.KEY.get(context.player().getWorld());
            if (worldModifierComponent.isRole(context.player(), GUESSER)) {
                if (payload.player() != null) {
                    if (context.player().getWorld().getPlayerByUuid(payload.player()) != null) {
                        ServerPlayerEntity target = (ServerPlayerEntity) context.player().getWorld().getPlayerByUuid(payload.player());
                        ServerPlayerEntity player = context.player();
                        if (target == null) return;
                        if (payload.guess() != null) {
                            boolean wrong = gameWorldComponent.getRole(target) == null;

                            if (!wrong) {
                                wrong = !gameWorldComponent.getRole(target).identifier().getPath().equalsIgnoreCase(payload.guess());

                                if (!gameWorldComponent.isInnocent(player)) {
                                    if (KILLER_SIDED_NEUTRALS.contains(gameWorldComponent.getRole(target))) wrong = true;
                                    if (gameWorldComponent.getRole(target).canUseKiller()) wrong = true;
                                }
                                if (Harpymodloader.SPECIAL_ROLES.contains(gameWorldComponent.getRole(target))) wrong = true;
                            }
                            if (!wrong) {
                                player.playSoundToPlayer(SoundEvents.ENTITY_PIG_DEATH, SoundCategory.MASTER, 1, 1);
                                GameFunctions.killPlayer(target, true, player, VOODOO_MAGIC_DEATH_REASON);
                            } else {
                                player.playSoundToPlayer(SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.MASTER, 1, 1);
                                if (NoellesRolesConfig.HANDLER.instance().guesserDiesAfterIncorrectGuess.equalsIgnoreCase("death")) {
                                    GameFunctions.killPlayer(player, true, null, VOODOO_MAGIC_DEATH_REASON);
                                }
                                if (NoellesRolesConfig.HANDLER.instance().guesserDiesAfterIncorrectGuess.equalsIgnoreCase("explode")) {
                                    player.getServerWorld().playSound(null, player.getBlockPos(), WatheSounds.ITEM_GRENADE_EXPLODE, SoundCategory.PLAYERS, 5.0F, 1.0F + player.getRandom().nextFloat() * 0.1F - 0.05F);
                                    player.getServerWorld().spawnParticles(WatheParticles.BIG_EXPLOSION, player.getX(), player.getY() + 0.1F, player.getZ(), 1, 0.0F, 0.0F, 0.0F, 0.0F);
                                    player.getServerWorld().spawnParticles(ParticleTypes.SMOKE, player.getX(), player.getY() + 0.1F, player.getZ(), 100, 0.0F, 0.0F, 0.0F, 0.2F);

                                    for(ServerPlayerEntity player2 : player.getServerWorld().getPlayers((serverPlayerEntity) -> player.getBoundingBox().expand(2.0F).contains(serverPlayerEntity.getPos()) && GameFunctions.isPlayerAliveAndSurvival(serverPlayerEntity))) {
                                        GameFunctions.killPlayer(player2, true, player, GameConstants.DeathReasons.GRENADE);
                                    }
                                }
                            }
                        }
                    }
                }
                AbilityPlayerComponent abilityPlayerComponent = AbilityPlayerComponent.KEY.get(context.player());
                abilityPlayerComponent.cooldown = GameConstants.getInTicks(2, 0);
                abilityPlayerComponent.sync();
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(Noellesroles.ABILITY_PACKET, (payload, context) -> {
            AbilityPlayerComponent abilityPlayerComponent = AbilityPlayerComponent.KEY.get(context.player());
            GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(context.player().getWorld());
            if (gameWorldComponent.isRole(context.player(), RECALLER) && abilityPlayerComponent.cooldown <= 0) {
                RecallerPlayerComponent recallerPlayerComponent = RecallerPlayerComponent.KEY.get(context.player());
                PlayerShopComponent playerShopComponent = PlayerShopComponent.KEY.get(context.player());
                if (!recallerPlayerComponent.placed) {
                    abilityPlayerComponent.cooldown = GameConstants.getInTicks(0,10);
                    recallerPlayerComponent.setPosition();
                }
                else if (playerShopComponent.balance >= recallerPlayerComponent.recallCost + (recallerPlayerComponent.recallIncrease * recallerPlayerComponent.recallCount)) {
                    playerShopComponent.balance -= recallerPlayerComponent.recallCost + (recallerPlayerComponent.recallIncrease * recallerPlayerComponent.recallCount);
                    playerShopComponent.sync();
                    abilityPlayerComponent.cooldown = GameConstants.getInTicks(0,60);
                    recallerPlayerComponent.teleport();
                }

            }
            //Phantom go invisible ability
            if (gameWorldComponent.isRole(context.player(), PHANTOM) && abilityPlayerComponent.cooldown <= 0) {
                PhantomPlayerComponent phantomPlayerComponent = PhantomPlayerComponent.KEY.get(context.player());
                PlayerPsychoComponent playerPsychoComponent = PlayerPsychoComponent.KEY.get(context.player());
                PlayerShopComponent playerShopComponent = PlayerShopComponent.KEY.get(context.player());
                if (playerShopComponent.balance < phantomPlayerComponent.invisCost) {
                    context.player().sendMessage(Text.translatable("tip.noellesroles.ability_cannot_afford", phantomPlayerComponent.invisCost).formatted(Formatting.DARK_RED), true);
                } else {
                    if (playerPsychoComponent.psychoTicks == 0) {
                        context.player().addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, phantomPlayerComponent.invisTimer * 20, 0, true, false, true));
                        // NOTE: The ability cooldown, uptime, and charges are all handled in the PhantomPlayerComponent.java file. See there to modify the phantom.
                        abilityPlayerComponent.cooldown = GameConstants.getInTicks(0, phantomPlayerComponent.invisCooldown);
                        playerShopComponent.setBalance(playerShopComponent.balance - phantomPlayerComponent.invisCost);
                        phantomPlayerComponent.sync();
                    }
                }
            }

            //Morphling force remove disguise ability
            if (gameWorldComponent.isRole(context.player(), MORPHLING)) {
                MorphlingPlayerComponent morphlingPlayerComponent = MorphlingPlayerComponent.KEY.get(context.player());
                if (morphlingPlayerComponent.getMorphTicks() > 0) {
                    morphlingPlayerComponent.reset();
                    abilityPlayerComponent.cooldown = morphlingPlayerComponent.getMorphTicks();
                }
            }

            // Executioner reroll ability
            if (gameWorldComponent.isRole(context.player(), EXECUTIONER)) {
                ExecutionerPlayerComponent executionerPlayerComponent = ExecutionerPlayerComponent.KEY.get(context.player());

                if (!executionerPlayerComponent.hasRerolled) {
                    List<UUID> innocentPlayers = new ArrayList<>();
                    gameWorldComponent.getRoles().forEach((uuid2, role1) -> {
                        PlayerEntity player2 = context.player().getWorld().getPlayerByUuid(uuid2);
                        if (uuid2 == null) return;
                        if (role1.isInnocent() && GameFunctions.isPlayerAliveAndSurvival(player2) && !role1.equals(WatheRoles.VIGILANTE) && !role1.equals(Noellesroles.MIMIC) && uuid2 != executionerPlayerComponent.target) {
                            innocentPlayers.add(uuid2);
                        }
                    });

                    Collections.shuffle(innocentPlayers);
                    if (!innocentPlayers.isEmpty()) {
                        executionerPlayerComponent.target = innocentPlayers.getFirst();
                    } else {
                        context.player().sendMessage(Text.literal("Target switch failed! No other valid targets.").formatted(Formatting.DARK_RED), true);
                    }
                    executionerPlayerComponent.hasRerolled = true;
                    executionerPlayerComponent.sync();
                }
            }

            // Voodoo activate tracking
            if (gameWorldComponent.isRole(context.player(), VOODOO)) {
                context.player().sendMessage(Text.literal("You should not be seeing this!!").formatted(Formatting.WHITE), true);

            }
        });
    }
}
