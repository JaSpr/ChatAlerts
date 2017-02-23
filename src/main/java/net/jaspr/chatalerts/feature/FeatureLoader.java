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
package net.jaspr.chatalerts.feature;

import net.jaspr.chatalerts.module.ChatAlerts;
import net.jaspr.chatalerts.module.Module;

public class FeatureLoader extends Module {

	@Override
	public void addFeatures() {
		registerFeature(new ChatAlerts(), "chatalerts");
	}

}
