package com.gridgain.titanic.startup;

import org.apache.ignite.Ignition;

import com.gridgain.titanic.config.ServerConfigurationFactory;

/** This file was generated by Ignite Web Console (03/23/2020, 18:23) **/
public class ServerNodeCodeStartup {
    /**
     * Start up node with specified configuration.
     * 
     * @param args Command line arguments, none required.
     * @throws Exception If failed.
     **/
    public static void main(String[] args) throws Exception {
        Ignition.start(ServerConfigurationFactory.createConfiguration());
    }
}