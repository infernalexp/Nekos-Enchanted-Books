module org.infernalstudios.nebs {
    exports org.infernalstudios.nebs;

    // forge
    requires net.minecraftforge.javafmlmod;    // @Mod
    requires net.minecraftforge.forge;         // forge
    requires net.minecraftforge.eventbus;      // event bus

    // helper libraries
    requires org.apache.logging.log4j;         // logging
    requires com.google.gson;                  // overrides.json for the data generator
    requires com.google.common;                // immutable maps and sets

    // annotations
    requires static org.jetbrains.annotations; // @Nullable
}