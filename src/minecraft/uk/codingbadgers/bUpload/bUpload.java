/*
 *  bUpload - a minecraft mod which improves the existing screenshot functionality
 *  Copyright (C) 2013 TheCodingBadgers
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */
package uk.codingbadgers.bUpload;

import java.io.File;

import uk.codingbadgers.bUpload.handlers.ConfigHandler;
import uk.codingbadgers.bUpload.proxy.Proxy;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkMod;

/**
 * The main mod class for bUpload, entry point for the whole mod.
 */
@Mod(modid = "@MOD_ID@", name = "@MOD_NAME@", version = "@VERSION@")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class bUpload {
	
	@Instance("@MOD_ID@")
	public static bUpload INSTANCE = null;

    @SidedProxy(clientSide="uk.codingbadgers.bUpload.proxy.ClientProxy",
                    serverSide="uk.codingbadgers.bUpload.proxy.ServerProxy")
    public static Proxy proxy;
    
	public static String server;
	public static int port;

	public static File AUTH_DATABASE;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
	    proxy.preInit(event);
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
	    proxy.load(event);
	}

    @EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        ConfigHandler.save();
    }

    @EventHandler
    public void serverStop(FMLServerStoppingEvent event) {
        ConfigHandler.save();
    }

}
