/**
 * This class was implemented by <JaSpr>. It is distributed as part
 * of the ChatAlerts Mod.
 * https://github.com/JaSpr/ChatAlerts
 *
 * ChatAlerts is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * This class was derived from works created by <Vazkii> which were distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 */
package net.jaspr.chatalerts.proxy;

import net.jaspr.chatalerts.network.GuiHandler;
import net.jaspr.chatalerts.network.MessageRegister;
import net.jaspr.chatalerts.ChatAlerts;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;


import net.jaspr.chatalerts.module.ModuleLoader;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		ModuleLoader.preInit(event);

		NetworkRegistry.INSTANCE.registerGuiHandler(ChatAlerts.instance, new GuiHandler());
		MessageRegister.init();
	}

	public void init(FMLInitializationEvent event) {
		ModuleLoader.init(event);
	}

	public void postInit(FMLPostInitializationEvent event) {
		ModuleLoader.postInit(event);
	}

	public void serverStarting(FMLServerStartingEvent event) {
		ModuleLoader.serverStarting(event);
	}

	
}
