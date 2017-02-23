/**
 * This class was implemented by <JaSpr>. It is distributed as part
 * of the ChatAlerts Mod.
 * https://github.com/JaSpr/ChatAlerts
 *
 * ChatAlerts is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 */
package net.jaspr.chatalerts;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import net.jaspr.chatalerts.ref.Ref;
import net.jaspr.chatalerts.proxy.CommonProxy;

@Mod(modid = Ref.MOD_ID, name = Ref.MOD_NAME, version = Ref.VERSION, guiFactory = Ref.GUI_FACTORY)
public class ChatAlerts {

	@Instance(Ref.MOD_ID)
	public static ChatAlerts instance;

	@SidedProxy(serverSide = Ref.PROXY_COMMON, clientSide = Ref.PROXY_CLIENT)
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		proxy.serverStarting(event);
	}

}
