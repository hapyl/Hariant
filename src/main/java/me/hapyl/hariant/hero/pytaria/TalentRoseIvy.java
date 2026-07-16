package me.hapyl.hariant.hero.pytaria;

import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.block.display.DisplayModel;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.attribute.AttributeScaling;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.EntityCollector;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.WarningType;
import me.hapyl.hariant.entity.damage.DamageSourceIdentity;
import me.hapyl.hariant.entity.damage.DamageSourceImpl;
import me.hapyl.hariant.entity.damage.DamageType;
import me.hapyl.hariant.entity.damage.DeathMessage;
import me.hapyl.hariant.entity.effect.status.EnumStatusEffect;
import me.hapyl.hariant.entity.player.DelegateType;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.event.HariantProjectileHitEvent;
import me.hapyl.hariant.handler.HariantProjectile;
import me.hapyl.hariant.talent.Response;
import me.hapyl.hariant.talent.Talent;
import me.hapyl.hariant.talent.TalentContext;
import me.hapyl.hariant.talent.TalentType;
import me.hapyl.hariant.talent.field.DisplayField;
import me.hapyl.hariant.talent.target.TalentTarget;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.term.EnumTerminology;
import me.hapyl.hariant.util.Icon;
import me.hapyl.hariant.util.decimal.Decimal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public final class TalentRoseIvy extends Talent implements Listener {
    
    public final @DisplayField AttributeScaling damage = AttributeScaling.create(AttributeType.ATTACK, 54);
    public final @DisplayField Decimal elementalApplication = Decimal.ofElementalApplication(ElementType.PHYSICAL, 50);
    
    private final @DisplayField Decimal radius = Decimal.ofValue(3);
    private final @DisplayField Decimal affectPeriod = Decimal.ofSeconds(0.5f);
    private final @DisplayField Decimal effectDuration = Decimal.ofSeconds(0.5f);
    private final @DisplayField Decimal speedDecrease = Decimal.ofPercentage(50);
    
    private final Key modifierKey = Key.ofString("rose_ivy");
    
    private final DisplayModel model = BDEngine.parse(
            "{Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0.883f,0f,0f,-0.220625f,0f,0.9659258263f,-0.2171491788f,0.45125f,0f,0.2588190451f,0.8104117683f,-0.4225f,0f,0f,0f,1f],Tags: [\"1\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[1f,0f,0f,-0.25f,0f,0.9659258263f,0.2588190451f,-0.375f,0f,-0.2588190451f,0.9659258263f,-0.29125f,0f,0f,0f,1f],Tags: [\"1\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:hanging_roots\",Properties:{}},transformation:[0.5625f,0f,0f,-0.25f,0f,-0.75f,0f,0.6875f,0f,0f,-0.625f,0.338125f,0f,0f,0f,1f],Tags: [\"1\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1405435378,1142102506,496170367,-1519973330],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWUzNzBmNGEyYTAzNWMzYzJmNmU5ZWE0MDlhY2I2MTdhOTg0MTdiODAxOTRjYmNkMWI1NmZjMjAwMzQ2MWZjNCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.2014407672f,-0.0243539714f,-0.8075397151f,0.01625f,-0.3210037034f,0.3038319677f,-0.1371038997f,1.413125f,0.7434840211f,0.1377797863f,0.1596006866f,0.27375f,0f,0f,0f,1f],Tags: [\"1\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0.889f,0f,0f,-0.2225f,0f,0.6495190528f,0.425f,0.12375f,0f,-0.375f,0.7361215932f,-0.33125f,0f,0f,0f,1f],Tags: [\"1\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1758047450,-1623814837,135978019,1106893965],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWUzNzBmNGEyYTAzNWMzYzJmNmU5ZWE0MDlhY2I2MTdhOTg0MTdiODAxOTRjYmNkMWI1NmZjMjAwMzQ2MWZjNCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.2014407672f,-0.0243539714f,-0.8075397151f,0f,0.4833741979f,0.2712367789f,0.0696662992f,0.99125f,0.6497393725f,-0.1942363093f,0.1985358034f,-0.32625f,0f,0f,0f,1f],Tags: [\"1\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-131583789,641013219,77680637,1903995552],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWUzNzBmNGEyYTAzNWMzYzJmNmU5ZWE0MDlhY2I2MTdhOTg0MTdiODAxOTRjYmNkMWI1NmZjMjAwMzQ2MWZjNCJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.0699348175f,-0.1798100636f,-0.4345762352f,0.32375f,0.1685660421f,0.2577521387f,-0.2868429498f,0.543125f,0.4890589032f,-0.1145531581f,0.0367235336f,0.954375f,0f,0f,0f,1f],Tags: [\"5\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;718089027,-335138274,1615927611,-1545589797],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjEzMTI4MzcwY2MxYmFlZjk2ZTllMDBmMzI5MmM5OTJmNmZiYTNhYTZmYzEwNDA2YjY5Nzk0MmY1NGE5MDRkZSJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.5172395023f,-0.0056858976f,-0.0697751106f,0.145625f,-0.0088730599f,0.3344514536f,-0.0005957421f,0.520625f,0.0697751106f,0.0003817543f,-0.5173152608f,-1.004375f,0f,0f,0f,1f],Tags: [\"5\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1265204025,-1556906092,-745342108,-1129055256],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjEzMTI4MzcwY2MxYmFlZjk2ZTllMDBmMzI5MmM5OTJmNmZiYTNhYTZmYzEwNDA2YjY5Nzk0MmY1NGE5MDRkZSJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.2861575676f,0.1170045351f,0.3965586022f,-1.0325f,0.2437926663f,0.2948788835f,-0.0359575933f,0.510625f,-0.362164303f,0.1060501429f,-0.3375386296f,0.333125f,0f,0f,0f,1f],Tags: [\"5\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1799328320,-1898073212,-1747469361,-936363274],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjEzMTI4MzcwY2MxYmFlZjk2ZTllMDBmMzI5MmM5OTJmNmZiYTNhYTZmYzEwNDA2YjY5Nzk0MmY1NGE5MDRkZSJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.4994494461f,0.0650630204f,-0.1128061837f,-0.49375f,0.1177494111f,0.3229797342f,-0.0676809582f,0.5175f,0.0957566028f,-0.0578026356f,-0.5051515147f,0.649375f,0f,0f,0f,1f],Tags: [\"5\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1665261222,-212748741,141350,1832268583],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWUzNzBmNGEyYTAzNWMzYzJmNmU5ZWE0MDlhY2I2MTdhOTg0MTdiODAxOTRjYmNkMWI1NmZjMjAwMzQ2MWZjNCJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.4351278345f,-0.0735207368f,-0.264545565f,0.748125f,-0.2275676353f,0.2760248088f,0.1874934415f,0.49f,0.1770896355f,0.174055325f,-0.4090792752f,-0.051875f,0f,0f,0f,1f],Tags: [\"5\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0f,0.7071067812f,-0.7071067812f,0.32f,-1f,0f,0f,0.5625f,0f,0.7071067812f,0.7071067812f,-0.375f,0f,0f,0f,1f],Tags: [\"2\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0f,-0.1452196701f,-0.9893994378f,1.046875f,-1f,0f,0f,0.5625f,0f,0.9893994378f,-0.1452196701f,0.531875f,0f,0f,0f,1f],Tags: [\"2\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0f,-0.1452196701f,-0.9893994378f,0.625f,-1f,0f,0f,0.5625f,0f,0.9893994378f,-0.1452196701f,-1.0625f,0f,0f,0f,1f],Tags: [\"2\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0f,0.9945583221f,-0.1041813033f,0.19625f,-1f,0f,0f,0.5625f,0f,0.1041813033f,0.9945583221f,-0.538125f,0f,0f,0f,1f],Tags: [\"2\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0f,0.9134034241f,0.4070555058f,-1.185f,-1f,0f,0f,0.5625f,0f,-0.4070555058f,0.9134034241f,-0.103125f,0f,0f,0f,1f],Tags: [\"2\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0f,0.3580420467f,0.9337054636f,-1.185f,-1f,0f,0f,0.5625f,0f,-0.9337054636f,0.3580420467f,0.944375f,0f,0f,0f,1f],Tags: [\"2\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1773149513,-37208522,1751799758,-679199561],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWUzNzBmNGEyYTAzNWMzYzJmNmU5ZWE0MDlhY2I2MTdhOTg0MTdiODAxOTRjYmNkMWI1NmZjMjAwMzQ2MWZjNCJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.3659234915f,-0.150481906f,0.2888556349f,-0.09875f,-0.1490941272f,0.2970652865f,0.1880078352f,0.553125f,-0.3411083986f,0.03158578f,-0.3920452475f,1.623125f,0f,0f,0f,1f],Tags: [\"6\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1708642167,528495381,-1035815598,-1884368007],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWUzNzBmNGEyYTAzNWMzYzJmNmU5ZWE0MDlhY2I2MTdhOTg0MTdiODAxOTRjYmNkMWI1NmZjMjAwMzQ2MWZjNCJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.0498259718f,0.0743656204f,0.5064915405f,1.0725f,-0.1703280303f,0.3056986201f,-0.1260615341f,0.4975f,-0.4909070529f,-0.1136149558f,-0.0076687031f,-1.0925f,0f,0f,0f,1f],Tags: [\"6\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;808866665,1432973035,1021306152,1891917452],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjEzMTI4MzcwY2MxYmFlZjk2ZTllMDBmMzI5MmM5OTJmNmZiYTNhYTZmYzEwNDA2YjY5Nzk0MmY1NGE5MDRkZSJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.3101328812f,-0.0995609401f,-0.3900797112f,-0.945625f,0.0196266963f,0.3069802572f,-0.2064114384f,0.51375f,0.4194238772f,-0.0879829011f,-0.2787761414f,-0.6825f,0f,0f,0f,1f],Tags: [\"6\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;232161081,1983657957,1196143959,-492517795],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWUzNzBmNGEyYTAzNWMzYzJmNmU5ZWE0MDlhY2I2MTdhOTg0MTdiODAxOTRjYmNkMWI1NmZjMjAwMzQ2MWZjNCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.4844280537f,0.0338413499f,0.1871483074f,-0.2025f,-0.0768188368f,0.32904739f,0.0539431665f,0.56125f,-0.1786402168f,-0.0497275397f,0.4843032581f,-0.601875f,0f,0f,0f,1f],Tags: [\"6\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-179070586,-634414235,532372407,319248900],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWUzNzBmNGEyYTAzNWMzYzJmNmU5ZWE0MDlhY2I2MTdhOTg0MTdiODAxOTRjYmNkMWI1NmZjMjAwMzQ2MWZjNCJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.4351278345f,-0.0735207368f,-0.264545565f,-1.233125f,-0.2275676353f,0.2760248088f,0.1874934415f,0.49f,0.1770896355f,0.174055325f,-0.4090792752f,-0.1825f,0f,0f,0f,1f],Tags: [\"6\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1281267112,-453332212,-887403020,-363775382],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjEzMTI4MzcwY2MxYmFlZjk2ZTllMDBmMzI5MmM5OTJmNmZiYTNhYTZmYzEwNDA2YjY5Nzk0MmY1NGE5MDRkZSJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.4721068574f,0.1023951507f,0.1551314938f,1.405f,0.1720636582f,0.3156331713f,0.0162823063f,0.515625f,-0.1413973576f,0.0422040792f,-0.4981496825f,0.163125f,0f,0f,0f,1f],Tags: [\"6\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1494865000,532642057,1404294566,931952321],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjEzMTI4MzcwY2MxYmFlZjk2ZTllMDBmMzI5MmM5OTJmNmZiYTNhYTZmYzEwNDA2YjY5Nzk0MmY1NGE5MDRkZSJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.4939071268f,0.0654009011f,-0.134623119f,0.966875f,0.0976817082f,0.3280387974f,0.0297190309f,0.519375f,0.1378333557f,0.0018760413f,-0.5034653861f,0.994375f,0f,0f,0f,1f],Tags: [\"6\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;2022658568,49468766,239563043,-1159052116],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjEzMTI4MzcwY2MxYmFlZjk2ZTllMDBmMzI5MmM5OTJmNmZiYTNhYTZmYzEwNDA2YjY5Nzk0MmY1NGE5MDRkZSJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.3540839057f,-0.1670315331f,0.2813635279f,0.90875f,-0.1354887316f,0.2840345434f,0.240123174f,0.508125f,-0.3588194411f,0.0575768625f,-0.3683197341f,1.7925f,0f,0f,0f,1f],Tags: [\"6\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0f,-0.7104310798f,-0.7037667801f,0.766875f,-1f,0f,0f,0.5625f,0f,0.7037667801f,-0.7104310798f,1.600625f,0f,0f,0f,1f],Tags: [\"3\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0f,0.9337054636f,-0.3580420467f,1.13375f,-1f,0f,0f,0.5625f,0f,0.3580420467f,0.9337054636f,-0.538125f,0f,0f,0f,1f],Tags: [\"3\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0f,0.9945583221f,-0.1041813033f,-1.75f,-1f,0f,0f,0.5625f,0f,0.1041813033f,0.9945583221f,-0.265f,0f,0f,0f,1f],Tags: [\"3\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0f,0.5875028163f,0.8092221208f,0.33f,-1f,0f,0f,0.5625f,0f,-0.8092221208f,0.5875028163f,-0.26f,0f,0f,0f,1f],Tags: [\"3\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0f,0.5969252383f,-0.8022968652f,-0.255625f,-1f,0f,0f,0.5625f,0f,0.8022968652f,0.5969252383f,-1.60875f,0f,0f,0f,1f],Tags: [\"3\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0f,-0.5875028163f,-0.8092221208f,-0.284375f,-1f,0f,0f,0.5625f,0f,0.8092221208f,-0.5875028163f,-0.625625f,0f,0f,0f,1f],Tags: [\"3\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0f,-0.9610690305f,-0.2763083759f,1.139375f,-1f,0f,0f,0.5625f,0f,0.2763083759f,-0.9610690305f,-0.571875f,0f,0f,0f,1f],Tags: [\"3\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0f,0.5969252383f,-0.8022968652f,1.0325f,-1f,0f,0f,0.5625f,0f,0.8022968652f,0.5969252383f,0.31375f,0f,0f,0f,1f],Tags: [\"3\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0f,-0.8636596023f,0.5040754818f,-0.120625f,-1f,0f,0f,0.5625f,0f,-0.5040754818f,-0.8636596023f,1.9925f,0f,0f,0f,1f],Tags: [\"3\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0f,0.7802121089f,-0.6255150398f,0.696875f,-1f,0f,0f,0.5625f,0f,0.6255150398f,0.7802121089f,0.990625f,0f,0f,0f,1f],Tags: [\"3\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:hanging_roots\",Properties:{}},transformation:[0.5625f,0f,0f,-1.4075f,0f,-0.75f,0f,0.6875f,0f,0f,-0.625f,0.52625f,0f,0f,0f,1f],Tags: [\"3\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:hanging_roots\",Properties:{}},transformation:[0.5625f,0f,0f,-0.84f,0f,-0.75f,0f,0.6875f,0f,0f,-0.625f,1.36375f,0f,0f,0f,1f],Tags: [\"3\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:hanging_roots\",Properties:{}},transformation:[0.5625f,0f,0f,0.0475f,0f,-0.75f,0f,0.6875f,0f,0f,-0.625f,1.67125f,0f,0f,0f,1f],Tags: [\"3\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0f,0.3716919156f,-0.9283561385f,0.308125f,-0.8527313446f,-0.4849265903f,-0.1941531766f,0.70375f,-0.522349743f,0.7916383782f,0.316953347f,1.816875f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0f,0.5969252383f,-0.8022968652f,-0.02125f,-1f,0f,0f,0.5625f,0f,0.8022968652f,0.5969252383f,-2.07125f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0f,-0.1452196701f,-0.9893994378f,0.26375f,-0.849892693f,0.5213697678f,-0.0765243468f,0.213125f,0.5269557955f,0.8408833526f,-0.1234211365f,-2.624375f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[-0.479764159f,0.8014178698f,0.3571494782f,1.785625f,-0.8773974879f,-0.4382182256f,-0.1952906424f,0.66125f,0f,-0.4070555058f,0.9134034241f,-0.206875f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0.5254716511f,-0.7944069814f,0.304626151f,-1.969375f,-0.8508111094f,-0.4906357516f,0.1881409454f,0.495625f,0f,-0.3580420467f,-0.9337054636f,0.711875f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0f,0.9876336745f,0.1567792238f,1.095f,-1f,0f,0f,0.5625f,0f,-0.1567792238f,0.9876336745f,-1.243125f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0f,-0.3963468475f,-0.9181008531f,0.971875f,-1f,0f,0f,0.5625f,0f,0.9181008531f,-0.3963468475f,-1.776875f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[-0.3569010433f,-0.927857369f,-0.1081773917f,1.540625f,-0.9341421976f,0.3544998437f,0.0413305641f,0.25375f,0f,0.1158039879f,-0.9932720858f,-1.558125f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0f,0.9181008531f,-0.3963468475f,-1.266875f,-1f,0f,0f,0.5625f,0f,0.3963468475f,0.9181008531f,-2.111875f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0.4049820132f,0.849823512f,0.3373270927f,-2.478125f,-0.9143246519f,0.3764125095f,0.1494123612f,0.094375f,0f,-0.3689357955f,0.9294548826f,-1.724375f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0f,0.6295916278f,-0.7769262399f,-1.1875f,-1f,0f,0f,0.5625f,0f,0.7769262399f,0.6295916278f,-0.835f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0f,-0.1041813033f,-0.9945583221f,-0.926875f,-1f,0f,0f,0.5625f,0f,0.9945583221f,-0.1041813033f,-1.35375f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0.4022673787f,0.9042005854f,0.1435348648f,-2.6375f,-0.9155222313f,0.3972928094f,0.0630671674f,0.106875f,0f,-0.1567792238f,0.9876336745f,-1.135625f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0f,-0.6127691363f,-0.7902619728f,1.896875f,-1f,0f,0f,0.5625f,0f,0.7902619728f,-0.6127691363f,-1.438125f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0f,0.6927727649f,0.7211559445f,-2.35375f,-1f,0f,0f,0.5625f,0f,-0.7211559445f,0.6927727649f,0.711875f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[-0.4496726827f,0.8221423655f,-0.3491080195f,1.464375f,-0.8838592216f,-0.4659352743f,0.0411970449f,0.580625f,-0.1287919048f,0.3270875281f,0.936176476f,0.863125f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0f,0.4070555058f,-0.9134034241f,1.854375f,-1f,0f,0f,0.5625f,0f,0.9134034241f,0.4070555058f,-0.044375f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0.4183427103f,-0.7777381952f,-0.4691616742f,-0.1075f,-0.8953838802f,-0.4398872203f,-0.0691877195f,0.635f,-0.1525682926f,0.4490239783f,-0.8803978549f,2.3425f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0f,-0.9671347528f,-0.25426437f,-0.496875f,-1f,0f,0f,0.5625f,0f,0.25426437f,-0.9671347528f,1.551875f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0f,0.1093873466f,-0.9939991994f,1.609375f,-0.9124060741f,-0.4068301094f,-0.0447707264f,0.591875f,-0.409286154f,0.9069309072f,0.0998056795f,1.936875f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[-0.3821996377f,-0.8751943918f,0.2965774998f,2.245f,-0.9240797784f,0.3619806291f,-0.1226645314f,0.29625f,0f,-0.3209436098f,-0.947098305f,-0.885625f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0f,-0.1452196701f,-0.9893994378f,1.366875f,-0.910827806f,0.4084107469f,-0.0599447217f,0.266875f,0.4127865161f,0.9011725191f,-0.1322701135f,-2.901875f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0.5798527104f,0.7864070167f,0.2129197931f,-2.95875f,-0.8077770369f,0.5208813052f,0.2760052982f,-0.06125f,0.1061465634f,-0.3320341398f,0.9372759663f,0.8175f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0.1780150101f,-0.4799197725f,-0.8590620863f,-1.15875f,-0.8254442508f,-0.5480699766f,0.1351335994f,0.505625f,-0.5356794238f,0.6850520511f,-0.4937116994f,1.6725f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[-0.6406968229f,0.7043304531f,-0.3056569873f,2.14875f,-0.7496442141f,-0.6598923295f,0.0507510167f,0.66f,-0.1659552148f,0.2616500072f,0.950788168f,0.508125f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0.2512016626f,0.5799876167f,-0.7749271509f,-0.703125f,-0.8579632266f,0.5040545983f,0.0991365912f,0.085625f,0.448103589f,0.6399557222f,0.6242274002f,-2.9325f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0f,0.0047123715f,0.9999888967f,-1.743125f,-0.9347016637f,0.35542931f,-0.0016749336f,0.198125f,-0.3554332565f,-0.9346912855f,0.0044046615f,2.318125f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[-0.5170942264f,0.6490961794f,-0.5579316364f,2.53375f,-0.8415116275f,-0.5046698425f,0.192786231f,0.534375f,-0.156434465f,0.5691946063f,0.8071838442f,-1.32875f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:hanging_roots\",Properties:{}},transformation:[0.5625f,0f,0f,1.21875f,0f,-0.75f,0f,0.6875f,0f,0f,-0.625f,0.519375f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:hanging_roots\",Properties:{}},transformation:[0.5625f,0f,0f,-0.113125f,0f,-0.75f,0f,0.6875f,0f,0f,-0.625f,-0.665f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:hanging_roots\",Properties:{}},transformation:[0.5625f,0f,0f,-1.579375f,0f,-0.75f,0f,0.6875f,0f,0f,-0.625f,-1.18625f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:hanging_roots\",Properties:{}},transformation:[0.5625f,0f,0f,0.3125f,0f,-0.75f,0f,0.6875f,0f,0f,-0.625f,-1.545625f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:dead_bush\",Properties:{}},transformation:[0.1494170897f,-0.6542211527f,-0.1222503461f,-0.68125f,-0.4861359121f,0f,-0.3977475644f,0.76375f,0.4626042134f,0.2113076747f,-0.3784943565f,1.041875f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:hanging_roots\",Properties:{}},transformation:[0.5625f,0f,0f,1.715625f,0f,-0.75f,0f,0.6875f,0f,0f,-0.625f,-0.600625f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:hanging_roots\",Properties:{}},transformation:[0.5625f,0f,0f,0.73625f,0f,-0.75f,0f,0.6875f,0f,0f,-0.625f,2.20875f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:hanging_roots\",Properties:{}},transformation:[0.5625f,0f,0f,-0.7425f,0f,-0.75f,0f,0.6875f,0f,0f,-0.625f,-1.774375f,0f,0f,0f,1f],Tags: [\"4\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;818327373,392313063,-806647446,-971264286],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWUzNzBmNGEyYTAzNWMzYzJmNmU5ZWE0MDlhY2I2MTdhOTg0MTdiODAxOTRjYmNkMWI1NmZjMjAwMzQ2MWZjNCJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.0909920221f,0.1487313065f,0.4586214525f,0.38875f,0.1536848097f,0.2926755083f,-0.200652713f,0.494375f,-0.4904950878f,0.0641115849f,-0.1479488157f,-1.52f,0f,0f,0f,1f],Tags: [\"7\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1600298072,79672843,-45047800,1909454854],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWUzNzBmNGEyYTAzNWMzYzJmNmU5ZWE0MDlhY2I2MTdhOTg0MTdiODAxOTRjYmNkMWI1NmZjMjAwMzQ2MWZjNCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.2085682857f,0.1404277866f,-0.425393528f,1.970625f,0.1180912624f,0.2730597518f,0.2774167818f,0.4925f,0.4637216018f,-0.1326976213f,0.1206825403f,0.95f,0f,0f,0f,1f],Tags: [\"7\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1907281891,-940274107,744262770,-1989483643],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWUzNzBmNGEyYTAzNWMzYzJmNmU5ZWE0MDlhY2I2MTdhOTg0MTdiODAxOTRjYmNkMWI1NmZjMjAwMzQ2MWZjNCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.2055838816f,0.0000979521f,-0.4798116758f,-1.71125f,0.3064106343f,0.2573822624f,0.1314150674f,0.543125f,0.3692313513f,-0.2136459956f,0.1580975515f,0.566875f,0f,0f,0f,1f],Tags: [\"7\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1116355165,458400397,-2113767926,-1413346056],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWUzNzBmNGEyYTAzNWMzYzJmNmU5ZWE0MDlhY2I2MTdhOTg0MTdiODAxOTRjYmNkMWI1NmZjMjAwMzQ2MWZjNCJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.3626565398f,0.1067381688f,0.3364803245f,-0.895625f,0.1568734986f,0.3154354955f,-0.0746023801f,0.5575f,-0.3411083986f,0.03158578f,-0.3920452475f,1.161875f,0f,0f,0f,1f],Tags: [\"7\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1000728181,398199191,593322601,-1137192026],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWUzNzBmNGEyYTAzNWMzYzJmNmU5ZWE0MDlhY2I2MTdhOTg0MTdiODAxOTRjYmNkMWI1NmZjMjAwMzQ2MWZjNCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.3130935187f,-0.0616696018f,-0.4064415595f,-1.575625f,-0.0068497506f,0.3246979087f,-0.1252544671f,0.560625f,0.4176236697f,0.0515594635f,0.3026558725f,-0.77625f,0f,0f,0f,1f],Tags: [\"7\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;306939857,-264211810,1422638456,872055060],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWUzNzBmNGEyYTAzNWMzYzJmNmU5ZWE0MDlhY2I2MTdhOTg0MTdiODAxOTRjYmNkMWI1NmZjMjAwMzQ2MWZjNCJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.2425296019f,-0.0670948736f,0.4502226582f,-0.445f,0.0250000231f,0.3234246437f,0.1308444471f,0.5025f,-0.4615608205f,0.0527733627f,-0.2294848332f,-1.886875f,0f,0f,0f,1f],Tags: [\"7\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1257029799,1916598592,1939698399,-502703996],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjEzMTI4MzcwY2MxYmFlZjk2ZTllMDBmMzI5MmM5OTJmNmZiYTNhYTZmYzEwNDA2YjY5Nzk0MmY1NGE5MDRkZSJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.3846576068f,0.0274449107f,-0.3502687781f,-1.31625f,0.1177494111f,0.3229797342f,-0.0676809582f,0.5175f,0.3326523737f,-0.082590061f,-0.3810709527f,1.248125f,0f,0f,0f,1f],Tags: [\"7\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1179493457,1769083754,-465998910,-1863652820],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjEzMTI4MzcwY2MxYmFlZjk2ZTllMDBmMzI5MmM5OTJmNmZiYTNhYTZmYzEwNDA2YjY5Nzk0MmY1NGE5MDRkZSJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.4387732695f,-0.0530487951f,0.2703862366f,-1.33625f,-0.0345420456f,0.3275041623f,0.1004255524f,0.51875f,-0.2806579147f,0.0426274442f,-0.4350746965f,-1.57125f,0f,0f,0f,1f],Tags: [\"7\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;297105926,-1958547355,-40631042,-1114751128],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjEzMTI4MzcwY2MxYmFlZjk2ZTllMDBmMzI5MmM5OTJmNmZiYTNhYTZmYzEwNDA2YjY5Nzk0MmY1NGE5MDRkZSJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.2979411898f,0.1570397505f,0.3516496124f,-0.601875f,-0.0359863758f,0.2642974874f,-0.3179261357f,0.441875f,-0.4271065771f,-0.1318165578f,-0.2185166409f,-1.37125f,0f,0f,0f,1f],Tags: [\"7\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1232506448,1500025299,2090564007,-1408339902],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWUzNzBmNGEyYTAzNWMzYzJmNmU5ZWE0MDlhY2I2MTdhOTg0MTdiODAxOTRjYmNkMWI1NmZjMjAwMzQ2MWZjNCJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.4351278345f,-0.0735207368f,-0.264545565f,-0.494375f,-0.2275676353f,0.2760248088f,0.1874934415f,0.49f,0.1770896355f,0.174055325f,-0.4090792752f,2.10625f,0f,0f,0f,1f],Tags: [\"7\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1741017013,828738868,835239589,687693236],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjEzMTI4MzcwY2MxYmFlZjk2ZTllMDBmMzI5MmM5OTJmNmZiYTNhYTZmYzEwNDA2YjY5Nzk0MmY1NGE5MDRkZSJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.474558357f,-0.1379554242f,-0.0305100742f,1.848125f,-0.1924077224f,0.2882958021f,-0.1818139939f,0.509375f,0.101279979f,-0.0987121137f,-0.4883613488f,-0.934375f,0f,0f,0f,1f],Tags: [\"7\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1593188104,1951002974,321885043,1835125162],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjEzMTI4MzcwY2MxYmFlZjk2ZTllMDBmMzI5MmM5OTJmNmZiYTNhYTZmYzEwNDA2YjY5Nzk0MmY1NGE5MDRkZSJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.3030544739f,0.1516984311f,0.3529877227f,2.2875f,0.2277950713f,0.2925110954f,-0.11056329f,0.421875f,-0.3588194411f,0.0575768625f,-0.3683197341f,0.055625f,0f,0f,0f,1f],Tags: [\"7\"]}]}"
    );
    
    private final int modelParts = 7;
    
    private final DamageSourceIdentity damageSourceIdentity = DamageSourceIdentity.create(
            this,
            DeathMessage.create("{player} was spikes to death [by {killer}]")
    );
    
    public TalentRoseIvy(@NotNull Key key) {
        super(key, Component.text("Rose Ivy"), Icon.ofTexture("41cceb6ee1210e1725ce30a7da3d8e68fc38a7d8b6d30abc030a2601df951d2d"));
        
        this.setCooldownSeconds(16);
        this.setDurationSeconds(4);
        
        this.setTalentType(TalentType.IMPAIR);
        
        this.setDescription(
                Component.empty()
                         .append(Component.text("Throw a bag filled with "))
                         .append(Component.text("spiky rose", Colors.RED))
                         .append(Component.text(" seeds in front of you."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Upon hit, the seeds sprout to life, creating a "))
                         .append(this.getName().color(Colors.SUCCESS))
                         .append(Component.text(" in small "))
                         .append(EnumTerminology.AREA_OF_EFFECT)
                         .append(Component.text(" for "))
                         .append(this.getDurationFormatted())
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("Enemies who are caught in the ivy are "))
                         .append(Component.text("slowed", Colors.ATTRIBUTE_MOVEMENT_SPEED))
                         .append(Component.text(" and take "))
                         .append(ElementType.PHYSICAL.asComponentDamage())
                         .append(Component.text(" whenever they "))
                         .append(Component.text("move").decorate(TextDecoration.UNDERLINED))
                         .append(Component.text("."))
                         .appendNewline()
                         .appendNewline()
                         .append(Component.text("This talent cannot kill.", Colors.DARK_GRAY))
        );
    }
    
    @EventHandler
    public void handleHariantProjectileHitEvent(HariantProjectileHitEvent ev) {
        final HariantProjectile projectile = ev.getProjectile();
        
        if (!(projectile.getDamageSource() instanceof RoseIvyProjectileDamageSource damageSource)) {
            return;
        }
        
        if (!(damageSource.getSource() instanceof HariantPlayer player)) {
            return;
        }
        
        final Location origin = LocationHelper.anchor(projectile.getLocation());
        
        player.delegate(new RoseIvyTask(player, origin), DelegateType.PERSISTENT);
        
        player.playWorldSound(origin, Sound.ENTITY_CAMEL_SADDLE, 0.0f);
        player.playWorldSound(origin, Sound.ENTITY_PLAYER_HURT_SWEET_BERRY_BUSH, 0.0f);
    }
    
    @NotNull
    @Override
    public TalentTarget target(@NotNull HariantPlayer player) {
        return TalentTarget.none();
    }
    
    @NotNull
    @Override
    public Response execute(@NotNull HariantPlayer player, @NotNull TalentContext context) {
        player.launchProjectile(Snowball.class, new RoseIvyProjectileDamageSource(player), self -> {
            self.setItem(getIcon().createItem());
        });
        
        // Fx
        player.playWorldSound(Sound.ENTITY_SNOWBALL_THROW, 0.75f);
        
        return Response.ok();
    }
    
    private class RoseIvyProjectileDamageSource extends DamageSourceImpl {
        RoseIvyProjectileDamageSource(@Nullable HariantEntity attacker) {
            super(damageSourceIdentity, attacker, DamageType.TALENT, ElementType.PHYSICAL, List.of(), Set.of(), 1, 1);
        }
    }
    
    private class RoseIvyModifier extends AttributeModifier {
        RoseIvyModifier(@NotNull HariantEntity applier) {
            super(modifierKey, TalentRoseIvy.this.getName(), applier, effectDuration.intValue());
            
            this.of(AttributeType.MOVEMENT_SPEED, AttributeModifierType.ADDITIVE, -speedDecrease.doubleValue());
        }
        
        @Override
        public void display(@NotNull Location location) {
        }
    }
    
    private class RoseIvyTask extends HariantTickingTask implements EntityCollector {
        
        private static final BlockData PARTICLE_DATA = Material.SWEET_BERRY_BUSH.createBlockData();
        
        private final HariantPlayer player;
        private final Location origin;
        private final DisplayEntity vineEntity;
        
        RoseIvyTask(@NotNull HariantPlayer player, @NotNull Location origin) {
            super(Scheduler.ofTimer(1));
            
            this.player = player;
            this.origin = origin;
            this.vineEntity = model.spawn(origin, self -> {
                self.setVisibleByDefault(false);
            });
        }
        
        @Override
        public void run(int tick) {
            if (tick >= TalentRoseIvy.this.getDuration()) {
                this.cancel();
                return;
            }
            
            // Show the model
            if (tick <= modelParts) {
                vineEntity.forEach(String.valueOf(tick), display -> Hariant.showBukkitEntity(display.getDisplay()));
            }
            
            // If fully drawn, apply IVY effect
            if (modulo(affectPeriod)) {
                collectNearbyEntities(origin, radius, 1, radius)
                        .filter(player::canAffect)
                        .forEach(entity -> {
                            entity.addEffect(EnumStatusEffect.ROSE_IVY, effectDuration, player);
                            entity.getAttributes().addModifier(new RoseIvyModifier(player));
                            
                            // Fx
                            entity.spawnWorldParticle(entity.getMidpointLocation(), Particle.BLOCK, 3, 0.25, 0.25, 0.25, 0.1f, PARTICLE_DATA);
                            entity.showWarning(WarningType.WARNING, affectPeriod.intValue() + 1);
                        });
            }
        }
        
        @Override
        public void onCancel() {
            vineEntity.remove();
        }
        
        @NotNull
        @Override
        public Location getLocation() {
            return origin;
        }
    }
    
}