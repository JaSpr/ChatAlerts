/**
 * This class was implemented by <JaSpr>. It is distributed as part of the ChatAlerts Mod.
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
package net.jaspr.chatalerts.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.jaspr.chatalerts.ref.Ref;
import net.jaspr.chatalerts.module.ModuleLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GuiFactory implements IModGuiFactory {

	@Override
	public void initialize(Minecraft minecraftInstance) {
		// NO-OP
	}


	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass() {
		return JSGuiConfig.class;
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}

	@Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
		return null;
	}

	public static class JSGuiConfig extends GuiConfig {

		public JSGuiConfig(GuiScreen parentScreen) {
			super(parentScreen, getAllElements(), Ref.MOD_ID, false, false, GuiConfig.getAbridgedConfigPath(ModuleLoader.config.toString()));
		}

		public static List<IConfigElement> getAllElements() {
			List<IConfigElement> list = new ArrayList<>();

			for(String catName : ModuleLoader.config.getCategoryNames()) {

                if (catName.equals(Ref.MOD_ID)) {
                    ConfigCategory category = ModuleLoader.config.getCategory(catName);

                    for (Map.Entry<String, Property> entry : category.entrySet()) {
                        list.add(new ConfigElement(entry.getValue()));
                    }

                }
            }

			return list;
		}

	}

}
