package me.hapyl.hariant.entity.type;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayModel;
import me.hapyl.eterna.module.component.ComponentList;
import me.hapyl.eterna.module.component.ProgressBar;
import me.hapyl.eterna.module.hologram.Hologram;
import me.hapyl.eterna.module.location.LocationHelper;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.HariantConstants;
import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.element.ElementType;
import me.hapyl.hariant.entity.HariantDisplayEntity;
import me.hapyl.hariant.entity.HariantEntity;
import me.hapyl.hariant.entity.damage.DamageInstance;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import me.hapyl.hariant.util.SoundFx;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public class HariantEntityDummy extends HariantDisplayEntity {
    
    private static final Map<ElementType, ProgressBar> PROGRESS_BARS = Map.ofEntries(
            createProgressBar(ElementType.PHYSICAL),
            createProgressBar(ElementType.FIRE),
            createProgressBar(ElementType.WATER),
            createProgressBar(ElementType.ICE),
            createProgressBar(ElementType.TOXIC),
            createProgressBar(ElementType.ELECTRIC),
            createProgressBar(ElementType.AETHER)
    );
    
    private static final DisplayModel MODEL = BDEngine.parse(
            "{Passengers:[{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1775407524,-1114428291,-151176402,2052143247],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTMzOWQ2OTViYjM3YjU5MzdhOTU5NjNkYWIyYmJjMmE2ZTFjZDhhMmEyNTY4MWUyOTkxNTQ5YzYxYzFkMzNkYyJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.9987771633f,0.0467293991f,0.0161412957f,0.0298f,0.0478690534f,0.9956878642f,0.0794621339f,2.1254f,-0.0123584744f,0.0801376333f,-0.9967071926f,0.0544f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:hay_block\",Properties:{axis:\"x\"}},transformation:[0.0387137003f,0.7242334136f,0.0040637537f,-0.3959f,0.7944327712f,-0.0353986103f,0.0466966376f,0.7848f,0.0492689708f,0.0017061596f,-0.7561485766f,0.3526f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{}},transformation:[-1f,0f,0f,0.5f,0f,1f,0f,0.1408f,0f,0f,-1f,0.5f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{}},transformation:[-0.7254302363f,0.3658903202f,-0.0056121114f,-0.2724f,0.3659324561f,0.7253154707f,-0.012928865f,0.594f,-0.000812302f,-0.0140709456f,-0.8123777438f,0.4449f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{}},transformation:[-0.7687185629f,-0.2630953145f,0.0029793501f,0.935f,-0.2631054702f,0.7685839438f,-0.0145080275f,0.7246f,0.0018795242f,-0.0146910441f,-0.8123649984f,0.4576f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:stone_sword\",Count:1},item_display:\"none\",transformation:[0.2806031252f,-0.1365363855f,-0.915528421f,-0.733f,0.2558329752f,-0.6866401974f,0.3033480618f,0.5198f,-0.6786963281f,-0.3152775765f,-0.2641736178f,0.1822f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:horn_coral_wall_fan\",Properties:{facing:\"east\"}},transformation:[0.005766184f,0.0200671472f,-0.6249662065f,0.2938f,-0.8748394493f,0.0186022899f,-0.0038803604f,0.9458f,0.0161722391f,0.9996343853f,0.0126237385f,-0.5336f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:horn_coral_wall_fan\",Properties:{facing:\"east\"}},transformation:[-0.0161824318f,-0.9996249573f,-0.0126229741f,0.5085f,-0.8748313388f,0.0186231904f,-0.0038856176f,0.9458f,0.0057669365f,0.0200778872f,-0.6248604344f,0.2801f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:horn_coral_wall_fan\",Properties:{facing:\"east\"}},transformation:[-0.0072943497f,-0.6926588813f,-0.4507620456f,0.5697f,-0.8748329427f,0.0185819312f,-0.0039305467f,0.9458f,0.0155389303f,0.7210280268f,-0.4329243579f,-0.1712f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:horn_coral_wall_fan\",Properties:{facing:\"east\"}},transformation:[-0.0155607545f,-0.7210103423f,0.4329344779f,0.1461f,-0.8748302647f,0.0186392744f,-0.0039167525f,0.9458f,-0.0073434153f,-0.6926726719f,-0.450751883f,0.556f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_andesite_slab\",Properties:{type:\"bottom\"}},transformation:[-0.5625f,0f,0f,0.2819f,0f,0.375f,0f,0f,0f,0f,-0.5625f,0.2762f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:horn_coral_wall_fan\",Properties:{facing:\"east\"}},transformation:[-0.7876094041f,-0.2564412426f,0.2200965817f,0.3989f,-0.1162654835f,0.931475145f,0.2116696382f,0.4491f,-0.3630134486f,0.2580541479f,-0.5453241779f,0.3401f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:horn_coral_wall_fan\",Properties:{facing:\"east\"}},transformation:[-0.8511428391f,-0.0570336343f,-0.1404991382f,0.195f,-0.1162751328f,0.9314655573f,0.2116834147f,0.4491f,0.166315847f,0.359331435f,-0.5710298802f,0.1846f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:feather\",Count:1},item_display:\"none\",transformation:[-0.0204226488f,0.0241090906f,0.625806317f,0.045f,-0.5290826509f,-0.060829648f,-0.0207140977f,2.245f,0.0594044329f,-0.5335007835f,0.0306406545f,-0.1325f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:horn_coral_wall_fan\",Properties:{facing:\"east\"}},transformation:[-0.6951938469f,-0.4904281935f,-0.2237791338f,0.5172f,-0.504652277f,0.793987636f,0.1201179208f,0.9203f,0.1663027191f,0.3592545347f,-0.5709593632f,0.1846f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:horn_coral_wall_fan\",Properties:{facing:\"east\"}},transformation:[-0.8083852001f,-0.2494313038f,-0.1813830406f,0.6402f,-0.2906595226f,0.8992794854f,0.1777962061f,0.8937f,0.1662991589f,0.3592776529f,-0.5709542115f,0.1083f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:stripped_dark_oak_wood\",Properties:{axis:\"x\"}},transformation:[-0.5660721875f,0.0037937442f,0.0032090603f,0.2918f,0.026631477f,0.080967361f,0.0407392868f,1.509f,-0.0013903282f,0.0062915788f,-0.526215595f,0.28f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:horn_coral_wall_fan\",Properties:{facing:\"east\"}},transformation:[-0.0938601952f,0.15961696f,-0.6133331968f,0.0863f,-0.5046357688f,0.7940085173f,0.1201181811f,0.9203f,0.7086304569f,0.5865770498f,0.0043018437f,-0.7316f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:horn_coral_wall_fan\",Properties:{facing:\"east\"}},transformation:[-0.7665659158f,-0.1793640284f,-0.2797324038f,0.6635f,-0.2508224368f,0.9401322422f,0.1152116414f,0.7228f,0.3392488791f,0.2897928788f,-0.5469013256f,-0.1229f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:horn_coral_wall_fan\",Properties:{facing:\"east\"}},transformation:[0.5354254785f,0.5195041244f,0.3727409576f,-0.7857f,-0.6490098287f,0.6387146248f,0.1279142831f,1.265f,-0.2402723446f,-0.5675906033f,0.4851052617f,0.0795f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:arrow\",Count:1},item_display:\"none\",transformation:[-0.1374443694f,-0.4567128179f,-0.3432111504f,0.1544f,0.4633731181f,0.072785544f,-0.253038312f,1.4181f,0.221103251f,-0.4364452711f,0.316950164f,0.5088f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:hay_block\",Properties:{axis:\"x\"}},transformation:[-0.1586967404f,-0.2107888806f,-0.0046857566f,-0.1285f,-0.195213268f,0.1714062079f,-0.0007733314f,1.3998f,0.0031120032f,0.0029748462f,-0.2874607724f,0.165f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:hay_block\",Properties:{axis:\"x\"}},transformation:[0.1257868915f,-0.2353055599f,-0.0009505103f,0.3547f,-0.2178768927f,-0.1358057646f,-0.0046953457f,1.5668f,0.0031428558f,0.0029962956f,-0.2874600846f,0.1721f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:barrel\",Properties:{facing:\"down\",open:\"false\"}},transformation:[-0.3801960519f,0.1489173115f,-0.0364311154f,0.7891f,-0.05658204f,0.9896574488f,0.0127827231f,0.2349f,0.4523388054f,0.2489607567f,-0.0290218163f,-0.1687f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:infested_stone\",Properties:{}},transformation:[-0.4264841114f,0.1620600594f,-0.0312248736f,0.8f,-0.0631520733f,1.0810633754f,0.0109182711f,0.1803f,0.5072273734f,0.2708596602f,-0.0248949518f,-0.2188f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone_slab\",Properties:{type:\"bottom\"}},transformation:[0.0658158665f,0.237655086f,-0.1102098482f,0.5366f,0.4169306489f,0.0310909065f,0.0385656599f,0.4636f,0.1004972874f,-0.2846189677f,-0.0878147459f,0.1895f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:arrow\",Count:1},item_display:\"none\",transformation:[-0.1374391181f,0.2417307862f,-0.4718185395f,-0.3482f,0.4633746984f,0.3101527686f,-0.0239512117f,1.2136f,0.2211020484f,-0.4997397231f,-0.2430903556f,0.5003f,0f,0f,0f,1f]}]}"
    );
    
    private static final double HOLOGRAM_OFFSET = 2.5;
    
    private static final Component HEAD_COMPONENT = createHeadComponent("5339d695bb37b5937a95963dab2bbc2a6e1cd8a2a25681e2991549c61c1d33dc");
    
    private final Hologram hologram;
    
    private final DamagePerSecond damagePerSecond;
    
    public HariantEntityDummy(@NotNull Location location) {
        super(MODEL, location, 4, Attributes.base(1_000_000, 100, 100));
        
        this.hologram = Hologram.ofTextDisplay(LocationHelper.getToTheRight(location, 2).add(0, 0.5, 0));
        this.hologram.showAll();
        
        this.damagePerSecond = new DamagePerSecond();
        
        this.setHurtSound(SoundFx.create(Sound.BLOCK_GRASS_BREAK, 0.0f));
    }
    
    @Override
    public void onDamageTaken(@NotNull DamageInstance damageInstance, @Nullable HariantEntity attacker) {
        this.damagePerSecond.increment(damageInstance);
        
        // Fx
        new HariantTickingTask(Scheduler.ofTimer(1)) {
            private double theta = 0;
            
            @Override
            public void run(int tick) {
                if (theta >= Math.PI * 2) {
                    displayEntity.resetRotation();
                    this.cancel();
                    return;
                }
                
                displayEntity.editRotation(quaternion -> {
                    quaternion.x = (float) Math.cos(theta) * 0.2f;
                    quaternion.y = (float) Math.sin(theta) * 0.1f;
                    quaternion.z = 0;
                });
                
                theta += Math.PI * 0.3;
            }
        };
    }
    
    @Override
    public @NotNull Component asHeadComponent() {
        return HEAD_COMPONENT;
    }
    
    @Override
    public boolean tick() {
        if (!super.tick()) {
            return false;
        }
        
        this.hologram.setLines(player -> {
            final ComponentList components = ComponentList.empty();
            
            // Append elemental anomaly
            for (ElementType elementType : ElementType.values()) {
                final ProgressBar progressBar = PROGRESS_BARS.get(elementType);
                final Style style = elementType.getStyle();
                final double units = elementData.getElementalUnit(elementType);
                
                components.append(
                        Component.empty()
                                 .append(elementType.getPrefix().style(style))
                                 .appendSpace()
                                 .append(progressBar.build(units, HariantConstants.ANOMALY_THRESHOLD))
                                 .appendSpace()
                                 .append(Component.text("%,.0f".formatted(units)))
                );
            }
            
            // Append health
            components.appendEmpty();
            components.append(this.getHealthFormatted());
            
            // Append dps
            components.appendEmpty();
            components.append(damagePerSecond.asComponent());
            
            return components;
        });
        
        // Sync display and hologram
        final double distanceToSquared = this.distanceToSquared(displayEntity);
        
        if (distanceToSquared >= 1) {
            final Location location = this.getLocation();
            
            displayEntity.teleport(location);
            hologram.teleport(location.add(0, HOLOGRAM_OFFSET, 0));
        }
        
        return true;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        
        this.hologram.dispose();
    }
    
    @Override
    public @NotNull Component getName() {
        return Component.text("Dummy");
    }
    
    @NotNull
    private static Map.Entry<ElementType, ProgressBar> createProgressBar(@NotNull ElementType elementType) {
        return Map.entry(elementType, new ProgressBar("|", 20, elementType.getStyle()));
    }
    
    private static class DamagePerSecond implements ComponentLike {
        
        private final Set<Entry> entries;
        
        private DamagePerSecond() {
            this.entries = Sets.newHashSet();
        }
        
        public void increment(@NotNull DamageInstance damageInstance) {
            this.entries.add(new Entry(damageInstance.getDamage(), System.currentTimeMillis()));
        }
        
        @NotNull
        @Override
        public Component asComponent() {
            this.entries.removeIf(Entry::isExpired);
            
            final double dps = this.entries.stream().mapToDouble(Entry::damage).sum();
            
            return Component.text("ᴅᴘꜱ ", Colors.RED).append(Component.text("%,.0f".formatted(dps), Colors.WHITE, TextDecoration.BOLD));
        }
        
        private record Entry(double damage, long timestamp) {
            
            public boolean isExpired() {
                return System.currentTimeMillis() - timestamp > 1000L;
            }
            
        }
    }
}
