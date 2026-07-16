package me.hapyl.hariant.hero.alchemist;

import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayModel;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.Removable;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.attribute.AttributeType;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.attribute.modifier.AttributeModifier;
import me.hapyl.hariant.attribute.modifier.AttributeModifierType;
import me.hapyl.hariant.element.ElementSource;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantDisplayEntity;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.WarningType;
import me.hapyl.hariant.entity.damage.DamageInstance;
import me.hapyl.hariant.entity.player.HariantPlayer;
import me.hapyl.hariant.hero.HeroRegistry;
import me.hapyl.hariant.ui.ComponentDisplay;
import me.hapyl.hariant.util.Definition;
import me.hapyl.hariant.util.TickDuration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HariantEntityAlchemicalCauldron extends HariantDisplayEntity implements Removable, TickDuration, ComponentLike {
    
    private static final DisplayModel MODEL = BDEngine.parse(
            "{Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone_wall\",Properties:{up:\"true\"}},transformation:[0f,0.0847250993f,-0.7419739378f,0.5995331576f,-0.2625f,0f,0f,0.911875f,0f,0.0606572135f,1.03637823f,-0.2920050209f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone_wall\",Properties:{up:\"true\"}},transformation:[0f,0.0606572135f,1.0365408502f,-0.7935576107f,-0.2625f,0f,0f,0.911875f,0f,-0.0847250993f,0.7420903624f,0.0133981852f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone_wall\",Properties:{up:\"true\"}},transformation:[0f,0.0847250993f,-0.7636289123f,0.0139418859f,-0.2625f,0f,0f,0.911875f,0f,0.0606572135f,1.0666255783f,-0.744195202f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone_wall\",Properties:{up:\"true\"}},transformation:[0f,0.0606572135f,1.0436148268f,-0.3626143965f,-0.2625f,0f,0f,0.911875f,0f,-0.0847250993f,0.7471548322f,-0.585867096f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_brick_wall\",Properties:{up:\"true\"}},transformation:[0.139474697f,0.1471198913f,0.1842528528f,-0.6159456836f,-0.0865267126f,0.3353258097f,-0.0599581618f,0.0373636558f,-0.1935829974f,-0.0438836974f,0.1595522017f,0.1363039985f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_brick_wall\",Properties:{up:\"true\"}},transformation:[-0.1935829974f,-0.0438836974f,0.1595522017f,0.0606884797f,-0.0865267126f,0.3353258097f,-0.0599581618f,0.0373636558f,-0.139474697f,-0.1471198913f,-0.1842528528f,0.6275650236f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_brick_wall\",Properties:{up:\"true\"}},transformation:[-0.139474697f,-0.1471198913f,-0.1842528528f,0.5540749056f,-0.0865267126f,0.3353258097f,-0.0599581618f,0.0373636558f,0.1935829974f,0.0438836974f,-0.1595522017f,-0.0659954003f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_brick_fence\",Properties:{}},transformation:[0.2384824532f,0.0858430186f,0.1754255407f,-0.0868496315f,0f,-0.1720508782f,0.2582922868f,0.3916593824f,0.1707366671f,-0.1199042596f,-0.2450318025f,-0.2909771141f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_brick_fence\",Properties:{}},transformation:[0.2376693525f,0.0858430186f,0.1754255407f,0.0717050169f,0f,-0.1720508782f,0.2582922868f,0.3916593824f,0.1701545442f,-0.1199042596f,-0.2450318025f,-0.1774631349f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:stripped_dark_oak_log\",Properties:{axis:\"x\"}},transformation:[0.1245670366f,0.0307989294f,0.0304088617f,0.166824139f,0f,-0.0617287573f,0.0447732661f,0.4056269001f,0.089181239f,-0.0430194894f,-0.0424746486f,-0.3607859653f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:iron_chain\",Properties:{axis:\"x\"}},transformation:[7.1e-9f,0.2394271776f,-0.3049127854f,0.3019962171f,0.1469f,0f,2.24e-8f,0.2200000097f,5.1e-9f,-0.334428343f,-0.2182961138f,-0.0900674071f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_brick_fence\",Properties:{}},transformation:[-0.2384824532f,-0.0858430186f,-0.1754255407f,0.0289986272f,0f,-0.1720508782f,0.2582922868f,0.3916593824f,-0.1707366671f,0.1199042596f,0.2450318025f,0.3653123854f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_brick_fence\",Properties:{}},transformation:[-0.2376693525f,-0.0858430186f,-0.1754255407f,-0.1295560212f,0f,-0.1720508782f,0.2582922868f,0.3916593824f,-0.1701545442f,0.1199042596f,0.2450318025f,0.2517984062f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:stripped_dark_oak_log\",Properties:{axis:\"x\"}},transformation:[-0.1245670366f,-0.0307989294f,-0.0304088617f,-0.2246751433f,0f,-0.0617287573f,0.0447732661f,0.4056269001f,-0.089181239f,0.0430194894f,0.0424746486f,0.4351212366f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:iron_chain\",Properties:{axis:\"x\"}},transformation:[-7.1e-9f,-0.2394271776f,0.3049127854f,-0.3598472214f,0.1469f,0f,2.24e-8f,0.2200000097f,-5.1e-9f,0.334428343f,0.2182961138f,0.1644026784f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone\",Properties:{}},transformation:[0.6098255708f,0f,-0.445673346f,-0.116826654f,0f,0.4666f,0f,0.195625f,0.4365922276f,0f,0.6225099427f,-0.4936845594f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone\",Properties:{}},transformation:[0f,-0.5057486734f,-0.4941059771f,0.4691033065f,0.4038f,0f,0f,0.23664375f,0f,-0.3620804874f,0.690159926f,-0.1310809276f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone\",Properties:{}},transformation:[0.3581802635f,0f,-0.6785325851f,0.1314116609f,0f,-0.4082f,0f,0.63698675f,-0.5003008983f,0f,-0.4857816186f,0.5295652825f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone_slab\",Properties:{type:\"bottom\"}},transformation:[0f,0.7397618705f,0.5111964485f,-0.4748314149f,-0.6503f,0f,0f,0.744375f,0f,-1.0332884471f,0.3659807113f,0.1124484427f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-2143114146,1033649147,-1952004819,-842916762],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTVkOTJlODllNDczN2RjZmFlNWU1NzFjNzZmNzYwNGE3YWI3YzllMDNmNzhjMTgyMTcxZDEwOTU3MTQxNTZkMSJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.4878604566f,0f,-0.1091480569f,0.1968813843f,0f,0.6188f,0f,0.590625f,0.3492737821f,0f,0.1524563927f,-0.2790842744f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;2031211502,-1956866457,-1388946302,-1273854186],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTVkOTJlODllNDczN2RjZmFlNWU1NzFjNzZmNzYwNGE3YWI3YzllMDNmNzhjMTgyMTcxZDEwOTU3MTQxNTZkMSJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.3492737821f,0f,-0.1524563927f,0.2784044799f,0f,0.6188f,0f,0.583125f,0.4878604566f,0f,-0.1091480569f,0.2512392565f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1833786615,-1021793985,945741863,-694874363],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTVkOTJlODllNDczN2RjZmFlNWU1NzFjNzZmNzYwNGE3YWI3YzllMDNmNzhjMTgyMTcxZDEwOTU3MTQxNTZkMSJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.3492737821f,0f,0.1524563927f,-0.3364337142f,0f,0.6188f,0f,0.589375f,-0.4878604566f,0f,0.1091480569f,-0.1858670072f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1612118799,-1570992260,185877367,785941832],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTVkOTJlODllNDczN2RjZmFlNWU1NzFjNzZmNzYwNGE3YWI3YzllMDNmNzhjMTgyMTcxZDEwOTU3MTQxNTZkMSJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.4878604566f,0f,0.1091480569f,-0.2517171296f,0f,0.6188f,0f,0.5875f,-0.3492737821f,0f,-0.1524563927f,0.3475114996f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_brick_wall\",Properties:{up:\"true\"}},transformation:[0.1935829974f,0.0438836974f,-0.1595522017f,-0.1299979426f,-0.0865267126f,0.3353258097f,-0.0599581618f,0.0384825047f,0.139474697f,0.1471198913f,0.1842528528f,-0.551658481f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:sculk\",Properties:{}},transformation:[0.5137170608f,0f,-0.4026544585f,-0.0752861182f,0f,0.0688f,0f,0.724375f,0.3677852925f,0f,0.5624217964f,-0.437041272f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:frogspawn\",Properties:{}},transformation:[0.1056367489f,0f,-0.2547107888f,0.0344172344f,0f,1f,0f,0.78375f,0.2512998951f,0f,0.1070705565f,-0.231672086f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:fire_coral\",Properties:{}},transformation:[0.1618849363f,0f,-0.436211386f,0.1180831717f,0f,0.4063f,0f,0.48375f,0.3363720521f,0f,0.2099343628f,-0.1775668508f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone_stairs\",Properties:{facing:\"east\",half:\"bottom\",shape:\"inner_left\"}},transformation:[0.1455450362f,0f,-0.1043746485f,0.3453608607f,0f,0.1006f,0f,0.73125f,0.1042000117f,0f,0.1457889665f,-0.14741755f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone_stairs\",Properties:{facing:\"east\",half:\"bottom\",shape:\"inner_left\"}},transformation:[-0.1042000117f,0f,-0.1457889665f,0.1610754899f,0f,0.1006f,0f,0.729375f,0.1455450362f,0f,-0.1043746485f,0.4170552369f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone_stairs\",Properties:{facing:\"east\",half:\"bottom\",shape:\"inner_left\"}},transformation:[-0.1455450362f,0f,0.1043746485f,-0.4039054849f,0f,0.1006f,0f,0.729375f,-0.1042000117f,0f,-0.1457889665f,0.2324060392f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone_stairs\",Properties:{facing:\"east\",half:\"bottom\",shape:\"inner_left\"}},transformation:[0.1042000117f,0f,0.1457889665f,-0.2228887077f,0f,0.1006f,0f,0.729375f,-0.1455450362f,0f,0.1043746485f,-0.332869506f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:sculk\",Properties:{}},transformation:[-0.0363826856f,0f,-0.0008944108f,0.2460481479f,0f,0.1574f,0f,0.68875f,0.0508187976f,0f,-0.0006403353f,0.3810382199f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:sculk\",Properties:{}},transformation:[-0.0500043631f,0f,-0.0855382001f,0.2824308336f,0f,0.1141f,0f,0.731875f,0.0698453554f,0f,-0.0612393365f,0.3302194223f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:sculk\",Properties:{}},transformation:[-0.0363826856f,-0.0851316497f,0f,0.2460481479f,0f,0f,-0.0011f,0.846875f,0.0508187976f,-0.060948275f,0f,0.3810382199f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:sculk\",Properties:{}},transformation:[-0.0363826856f,-0.0851316497f,0f,0.2817031799f,0f,0f,-0.0011f,0.846875f,0.0508187976f,-0.060948275f,0f,0.3312357983f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:sculk\",Properties:{}},transformation:[-0.0163576555f,-0.0474850844f,0f,0.1979771389f,0f,0f,-0.0011f,0.846875f,0.0228481314f,-0.0339959815f,0f,0.4234889998f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:sculk\",Properties:{}},transformation:[-0.0195593318f,-0.0474850844f,0f,0.2647427515f,0f,0f,-0.0011f,0.846875f,0.0273201856f,-0.0339959815f,0f,0.2937274643f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:sculk\",Properties:{}},transformation:[-0.0122827947f,0f,-0.0008944108f,0.2145395726f,0f,0.0666f,0f,0.759375f,0.0171564261f,0f,-0.0006403353f,0.4261225886f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:cyan_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.4158912517f,0f,-0.1548103253f,-0.2736745811f,0f,0.2563f,0f,0.828125f,0.1563232444f,0f,0.4118661957f,-0.6667009774f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:cyan_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.4158912517f,0f,-0.1548103253f,-0.2252698056f,0f,0.378f,0f,0.828125f,0.1563232444f,0f,0.4118661957f,-0.7104501288f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:cyan_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.4158912517f,0f,-0.1548103253f,-0.209162963f,0f,0.3236f,0f,0.828125f,0.1563232444f,0f,0.4118661957f,-0.6571418909f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:nautilus_shell\",Count:1},item_display:\"none\",transformation:[-0.0431412641f,-0.1923607614f,0.1226551584f,-0.2905124744f,-0.0050316746f,0.0480281841f,0.531186881f,0.7775f,-0.1829139513f,0.0440481667f,-0.0435410098f,0.0514735755f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:armadillo_scute\",Count:1},item_display:\"none\",transformation:[0.286404027f,0.1194639104f,-0.0368423316f,0.1289432833f,-0.0171817081f,0.1288343436f,0.2841878794f,0.79625f,0.1238296095f,-0.2584305245f,0.1246440604f,0.0335481606f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0.1533208084f,0.0170656202f,-0.266223342f,0.120308094f,0.0247070526f,0.2168271241f,0.0340912124f,0.685f,0.3345081931f,-0.0238370061f,0.1195046503f,0.1572699216f,0f,0f,0f,1f],Tags: [\"animate\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:purple_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.4158912517f,0f,-0.1548103253f,0.2451881877f,0f,0.2563f,0f,0.105625f,0.1563232444f,0f,0.4118661957f,0.1476589167f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:purple_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.0107899483f,0.0015760094f,0.0221006785f,0.412893483f,-0.4608505598f,0.0000341537f,0.0017942927f,0.31125f,0.004619363f,-0.000273912f,0.1273845773f,0.3149580988f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:purple_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.4608505598f,-0.0000341537f,-0.0061058683f,0.133083986f,0.0099235544f,0.0015994949f,0.0057020207f,0.103125f,0.0062677402f,-0.0000212058f,0.4399206807f,0.17003737f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:purple_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.4481977018f,-0.0000286561f,-0.1026713773f,0.2215685729f,0.0099235544f,0.0015994949f,0.0057020207f,0.103125f,0.1074306434f,-0.0000281956f,0.4278154687f,0.1371372865f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:purple_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.4481977018f,-0.0000286561f,-0.1026713773f,0.2213999865f,0.0099235544f,0.0015994949f,0.0057020207f,0.103125f,0.1074306434f,-0.0000281956f,0.4278154687f,0.1662257625f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:purple_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.4601192706f,-0.0000355825f,0.0253629623f,0.1603449469f,0.0099235544f,0.0015994949f,0.0057020207f,0.103125f,-0.0266979376f,-0.0000187097f,0.4392313822f,0.1953053851f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:purple_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.3984225154f,-0.0000193452f,-0.2212782303f,0.2803986719f,0.0099235544f,0.0015994949f,0.0057020207f,0.103125f,0.2316938115f,-0.000035241f,0.3802675792f,0.1462030165f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:purple_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.0107899483f,0.0015760094f,0.0221006785f,0.4159279732f,-0.4608505598f,0.0000341537f,0.0017942927f,0.31125f,0.004619363f,-0.000273912f,0.1273845773f,0.3300453812f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:light_gray_stained_glass\",Properties:{}},transformation:[0.0764818227f,0f,-0.0471293705f,0.2717753647f,0f,0.1852f,0f,0f,0.0468681213f,0f,0.0769081428f,0.2860693573f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:light_gray_stained_glass\",Properties:{}},transformation:[0.079732232f,0f,-0.0755793053f,0.354625735f,0f,0.1753f,0f,0f,0.072550749f,0f,0.083060572f,0.1696304639f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:light_gray_stained_glass\",Properties:{}},transformation:[0f,0.031042955f,-0.1097378103f,0.4226530515f,-0.115f,0f,0f,0.110625f,0f,0.1725294901f,0.0197449485f,0.3456076992f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:fire_coral_block\",Properties:{}},transformation:[0.0711101897f,0f,-0.0427926325f,0.2718273724f,0f,0.1289f,0f,0f,0.0435763803f,0f,0.0698312295f,0.2919653662f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:bubble_coral_block\",Properties:{}},transformation:[0.0708566589f,0f,-0.0651476113f,0.3532762967f,0f,0.0997f,0f,0f,0.0644745988f,0f,0.07159629f,0.1785427597f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_slab\",Properties:{type:\"bottom\"}},transformation:[0.0605374517f,0f,-0.0375676468f,0.2743375421f,0f,0.0494f,0f,0.17625f,0.0370973981f,0f,0.0613048278f,0.2986347213f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_slab\",Properties:{type:\"bottom\"}},transformation:[0.0672324665f,0f,-0.0638688875f,0.3542841088f,0f,0.0494f,0f,0.1675f,0.0611768375f,0f,0.0701909909f,0.1811498299f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:tube_coral_block\",Properties:{}},transformation:[0f,0.0289002296f,-0.1097378103f,0.4234277972f,-0.006f,0f,0f,0.01125f,0f,0.1606207232f,0.0197449485f,0.349913555f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_slab\",Properties:{type:\"bottom\"}},transformation:[0.0672324665f,0f,-0.0638688875f,0.2147560878f,0f,0.0494f,0f,0f,0.0611768375f,0f,0.0701909909f,0.3907153662f,0f,0f,0f,1f]}]}"
    );
    
    private static final Component CAULDRON_PREFIX = Component.text("\uD83C\uDF75", Colors.WHITE, TextDecoration.BOLD);
    
    private static final ItemStack ITEM_STACK_STICK = new ItemStack(Material.STICK);
    private static final ItemStack ITEM_STACK_AIR = new ItemStack(Material.AIR);
    
    private static final BlockData[] DESTROY_BLOCK_DATA = new BlockData[] {
            Material.POLISHED_BLACKSTONE.createBlockData(),
            Material.SCULK_VEIN.createBlockData()
    };
    
    private static final Particle.DustOptions DUST_OPTIONS = new Particle.DustOptions(bukkitColorFromStyle(ElementType.TOXIC.getStyle()), 1);
    
    private final HariantPlayer player;
    private final TalentAlchemicalCauldron talent;
    private final ElementSource elementSource;
    private final ArmorStand stickAnimation;
    
    private boolean isBrewing;
    private int tick;
    
    HariantEntityAlchemicalCauldron(@NotNull HariantPlayer player, @NotNull Location location, @NotNull TalentAlchemicalCauldron talent) {
        super(MODEL, location, 2, Attributes.base(talent.cauldronHealth.doubleValue(), 100, 100));
        
        this.player = player;
        this.talent = talent;
        this.elementSource = ElementSource.create(ElementType.TOXIC, player, talent.cauldronElementalApplication.doubleValue());
        this.tick = talent.getDuration();
        this.stickAnimation = Entities.ARMOR_STAND.spawn(
                LocationHelper.copyOf(location).subtract(0.0, 0.75, 0.0), self -> {
                    self.setMarker(true);
                    self.setSmall(true);
                    self.setSilent(true);
                    self.setInvisible(true);
                    self.setSmall(false);
                    
                    final EntityEquipment equipment = self.getEquipment();
                    
                    equipment.setItemInMainHand(ITEM_STACK_AIR);
                    self.setRightArmPose(new EulerAngle(Math.toRadians(-85.0d), Math.toRadians(-90), 0));
                }
        );
        
        player.getPlayerTeam().addEntry(this);
    }
    
    @Override
    public void onDamageTaken(@NotNull DamageInstance damageInstance, @Nullable HariantEntity attacker) {
        this.playWorldSound(Sound.ENTITY_IRON_GOLEM_STEP, 0.0f);
        this.playWorldSound(Sound.BLOCK_METAL_BREAK, 0.75f);
    }
    
    @Override
    public void onInteract(@NotNull HariantPlayer player) {
        // Ignore non-owner interactions
        if (!this.player.equals(player)) {
            player.playSound(Sound.BLOCK_LAVA_POP, 0.0f);
            return;
        }
        
        // If brewing is done, return the stick and apply infusion
        if (this.isOver()) {
            // applyInfusion() implicitly calls `remove()`
            this.applyInfusion();
            return;
        }
        
        this.toggleBrewing();
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (!super.shouldActuallyTick()) {
            return;
        }
        
        if (isBrewing) {
            tick--;
            
            // Brewing done
            if (tick == 0) {
                isBrewing = false;
                
                // Fx
                player.sendTitleSubtitle(CAULDRON_PREFIX, Component.text("BREWING FINISHED!", Colors.SUCCESS, TextDecoration.BOLD), 5, 20, 5);
                player.sendMessage(
                        Component.empty()
                                 .append(CAULDRON_PREFIX)
                                 .appendSpace()
                                 .append(Component.text("The cauldron finished brewing! ", Colors.SUCCESS))
                                 .append(Component.text("RIGHT-CLICK", Colors.GOLD, TextDecoration.BOLD))
                                 .append(Component.text(" on it to gain ", Colors.SUCCESS))
                                 .append(Definition.ALCHEMICAL_MADNESS)
                                 .append(Component.text("!", Colors.SUCCESS))
                );
                
                player.playSound(Sound.BLOCK_BREWING_STAND_BREW, 2.0f);
                player.playSound(Sound.BLOCK_BREWING_STAND_BREW, 1.5f);
                player.playSound(Sound.BLOCK_BREWING_STAND_BREW, 1.25f);
            }
            
            // Apply toxic in radius
            this.collectNearbyEntities(talent.cauldronElementalApplicationRadius)
                .filter(player::canAffect)
                .forEach(entity -> {
                    entity.applyElement(elementSource);
                    entity.showWarning(WarningType.WARNING, 5);
                });
            
            spawnWorldParticle(getLocation(), Particle.DUST, 1, 1, 0.6, 1, 0.1f, DUST_OPTIONS);
            
            // Animate cauldron
            final float pitch = (float) (Math.toRadians(2) * Math.cos(Math.toRadians(tick) * 10));
            final float roll = (float) (Math.toRadians(2) * Math.sin(Math.toRadians(tick) * 10));
            
            displayEntity.editRotation(rotation -> {
                rotation.x = pitch;
                rotation.z = roll;
            }, "animate");
            
            // Animate stick
            final Location location = stickAnimation.getLocation();
            location.setYaw(location.getYaw() + 10);
            
            stickAnimation.teleport(location);
            
            // Play global sfx
            if (tick == duration() || tick % 100 == 0) {
                this.playWorldSound(Sound.BLOCK_LAVA_AMBIENT, 100, 2.0f);
            }
        }
    }
    
    public void applyInfusion() {
        // Apply infusion
        final HeroAlchemist alchemist = HeroRegistry.ALCHEMIST;
        final HeroDataAlchemist heroData = player.getHeroData(alchemist, HeroDataAlchemist::new);
        
        heroData.setAlchemicalMadness(talent.infusionDuration.intValue());
        player.getAttributes().addModifier(new AlchemicalMadnessModifier(player));
        
        alchemist.giveWeapon(player);
        
        // Fx
        this.spawnWorldParticle(this.getLocation(), Particle.EXPLOSION_EMITTER, 1, 0.0f);
        
        this.playWorldSound(Sound.ENTITY_GENERIC_EXPLODE, 1.25f);
        this.playWorldSound(Sound.ENTITY_WITCH_CELEBRATE, 0.75f);
        
        // Cleanup
        heroData.setAlchemicalCauldron(null);
    }
    
    @NotNull
    @Override
    public Component getName() {
        return talent.getName();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        
        this.stickAnimation.remove();
        this.player.setCooldown(talent);
        
        // Fx
        final Location fxLocation = this.getLocation().add(0, 1, 0);
        
        player.playWorldSound(fxLocation, Sound.ENTITY_IRON_GOLEM_DAMAGE, 0.75f);
        
        player.spawnWorldParticle(fxLocation, Particle.BLOCK, 10, 0.3, 0.3, 0.3, 1f, DESTROY_BLOCK_DATA[0]);
        player.spawnWorldParticle(fxLocation, Particle.BLOCK, 10, 0.3, 0.3, 0.3, 1f, DESTROY_BLOCK_DATA[1]);
    }
    
    public void toggleBrewing() {
        isBrewing = !isBrewing;
        
        // If stared brewing, replace
        if (isBrewing) {
            stickAnimation.getEquipment().setItemInMainHand(ITEM_STACK_STICK);
        }
        else {
            stickAnimation.getEquipment().setItemInMainHand(ITEM_STACK_AIR);
        }
        
        // Update player's weapon either way
        player.getHero().giveWeapon(player);
        
        // Fx
        player.playSound(Sound.ENTITY_CHICKEN_EGG, 1.0f);
        player.playSound(Sound.ENTITY_CHICKEN_EGG, 0.75f);
    }
    
    public boolean isBrewing() {
        return isBrewing;
    }
    
    @Override
    public int currentTick() {
        return tick;
    }
    
    @Override
    public int duration() {
        return talent.getDuration();
    }
    
    @NotNull
    @Override
    public Component asComponent() {
        final TextComponent.Builder builder = Component.text();
        builder.append(CAULDRON_PREFIX);
        builder.appendSpace();
        
        // If brewing is over, show that
        if (this.isOver()) {
            builder.append(Component.text("BREWING FINISHED!", Colors.SUCCESS, TextDecoration.BOLD));
        }
        else {
            builder.append(
                    isBrewing
                    ? Component.text(Tick.format(tick), Colors.SUCCESS)
                    : Component.text("NOT BREWING!", Colors.ERROR, TextDecoration.BOLD)
            );
        }
        
        return builder.build();
    }
    
    private static @NotNull Color bukkitColorFromStyle(@NotNull Style style) {
        final TextColor color = style.color();
        
        return Color.fromRGB(color != null ? color.value() : 0);
    }
    
    private class AlchemicalMadnessModifier extends AttributeModifier {
        private static final Key MODIFIER_KEY = Key.ofString("alchemical_madness");
        
        AlchemicalMadnessModifier(@NotNull HariantEntity applier) {
            super(MODIFIER_KEY, Component.text("Alchemical Madness"), applier, talent.infusionDuration.intValue());
            
            this.of(AttributeType.TOXIC_DAMAGE_BONUS, AttributeModifierType.FLAT, talent.toxicDamageIncrease.doubleValue());
        }
        
        @Override
        public void display(@NotNull Location location) {
            ComponentDisplay.ofAscend(Definition.ALCHEMICAL_MADNESS.asComponent(), location, 40, 1.0f);
        }
    }
}