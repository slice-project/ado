package org.etri.ado.config;

import akka.actor.AbstractExtensionId;
import akka.actor.ExtendedActorSystem;
import akka.actor.ExtensionIdProvider;


public class Settings extends AbstractExtensionId<Configuration> implements ExtensionIdProvider {
  public static final Settings SettingsProvider = new Settings();

  private Settings() {}

  public Settings lookup() {
    return Settings.SettingsProvider;
  }

  public Configuration createExtension(ExtendedActorSystem system) {
    return new Configuration(system.settings().config());
  }
}
