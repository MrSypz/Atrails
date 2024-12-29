package sypztep.atrails.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AtrailsClient implements ClientModInitializer {
    public static final String MOD_ID = "atrails";
    public static final Logger LOGGER = LogManager.getLogger();
    public static Identifier id (String name) {
        return Identifier.of(MOD_ID, name);
    }

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing Atrails!");
    }
}
