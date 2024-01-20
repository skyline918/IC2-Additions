package ru.starshineproject.proxy;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import ru.starshineproject.IC2Additions;
import ru.starshineproject.Registration;
import ru.starshineproject.gui.GuiHandler;

public class CommonProxy {
    public void preInit(){

    }
    public void init(){
        NetworkRegistry.INSTANCE.registerGuiHandler(IC2Additions.instance, new GuiHandler());
    }
    public void postInit(){
        Registration.discoverOres();
    }
}
