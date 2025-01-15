package org.infernalstudios.nebs;

import net.minecraft.DetectedVersion;
import net.minecraft.SharedConstants;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * <h1>Neko's Enchanted Books</h1>
 * <p>
 * This is the main class for the Neko's Enchanted Books (shortened to NEBs) mod, loaded by Forge. The mod itself does
 * not interface much with Forge itself, but rather uses
 * {@link org.infernalstudios.nebs.mixin.BlockModelMixin BlockModelMixin} to inject the custom item overrides for the
 * enchanted books provided in {@link EnchantedBookOverrides}.
 */
@Mod(NekosEnchantedBooks.MOD_ID)
public class NekosEnchantedBooks {
    /** The Mod ID for this mod. Note that this variable is in-lined at compile time, so it is safe to reference. */
    public static final String MOD_ID = "nebs";
    /** The logger for this mod. Package-private since it doesn't need to be accessed in many places. */
    static final Logger LOGGER = LogManager.getLogger();

    /**
     * The constructor for the mod. This does two things:
     *
     * <ol>
     *     <li>Register the display test extension point, which tells the game to ignore this mod when polling servers
     *     for mod compatibility.</li>
     *     <li>Add our data generator as a listener to the {@link GatherDataEvent}. See
     *     {@link #gatherData(GatherDataEvent)}</li>
     * </ol>
     */
    public NekosEnchantedBooks() {
        // Newer Forge versions make this simpler, but here the old way is used for backwards compatibility with older Forge versions
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
            () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::gatherData);
    }

    /**
     * Adds our data generator, {@link EnchantedBookModelProvider}, to the {@link GatherDataEvent} event. This is used
     * to generate the item models for the enchanted books that NEBs natively supports.
     *
     * @param event The event to add our generator to
     */
    private void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();

        // native enchanted book models
        generator.addProvider(event.includeClient(), new EnchantedBookModelProvider(generator.getPackOutput(), NekosEnchantedBooks.MOD_ID, event.getExistingFileHelper()));

        // pack.mcmeta
        generator.addProvider(true, new PackMetadataGenerator(generator.getPackOutput()) {
            {
                // gets the pack versions
                int resourcePackFormat = SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES);
                int dataPackFormat = SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA);
                Map<@NotNull PackType, @NotNull Integer> packTypes = Map.of(
                    PackType.CLIENT_RESOURCES, resourcePackFormat,
                    PackType.SERVER_DATA, dataPackFormat
                );

                // adds to the generator with the correct info
                this.add(
                    PackMetadataSection.TYPE,
                    new PackMetadataSection(
                        Component.translatable("pack.nebs.description"),
                        SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES),
                        packTypes
                    )
                );
            }
        });
    }
}
