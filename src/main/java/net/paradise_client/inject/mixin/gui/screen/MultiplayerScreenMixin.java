package net.paradise_client.inject.mixin.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.EqualSpacingLayout;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.DirectJoinServerScreen;
import net.minecraft.client.gui.screens.EditServerScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.server.LanServerDetection;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.paradise_client.ParadiseClient;
import net.paradise_client.mod.BungeeSpoofMod;
import net.paradise_client.screen.UUIDSpoofScreen;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.*;

/**
 * Mixin for the MultiplayerScreen class to add custom GUI elements. This mixin injects additional buttons and text
 * fields into the multiplayer settings screen.
 *
 * @author SpigotRCE
 * @since 1.0
 */
@Mixin(JoinMultiplayerScreen.class)
public abstract class MultiplayerScreenMixin extends Screen {

  @Shadow @Final private static Logger LOGGER;

  /**
   * Reference to the BungeeSpoofMod instance for accessing mod data.
   */
  @Unique final BungeeSpoofMod bungeeSpoofMod = ParadiseClient.BUNGEE_SPOOF_MOD;

  @Shadow protected ServerSelectionList serverSelectionList;
  @Unique Button uuidSpoofButton;
  /**
   * Button for toggling BungeeCord spoofing.
   */
  @Unique Button bungeeToggleButton;
  /**
   * Text field for inputting BungeeCord IP.
   */
  @Unique EditBox bungeeClientIPField;
  /**
   * Button for toggling BungeeCord target hostname spoofing.
   */
  @Unique Button bungeeHostnameToggle;
  /**
   * Text field for inputting BungeeCord target hostname.
   */
  @Unique EditBox bungeeHostnameField;

  @Shadow private boolean initedOnce;
  @Shadow private ServerList servers;
  @Shadow private LanServerDetection.LanServerList lanServerList;
  @Shadow @Nullable private LanServerDetection.LanServerDetector lanServerDetector;
  @Shadow private Button selectButton;
  @Shadow private ServerData editingServer;
  @Shadow private Button editButton;
  @Shadow private Button deleteButton;

  /**
   * Constructor for MultiplayerScreenMixin.
   *
   * @param title The title of the screen.
   */
  protected MultiplayerScreenMixin(Component title) {
    super(title);
  }

  /**
   * @author SpigotRCE
   * @reason Overwriting to append custom bungee and UUID layout options to the multiplayer screen footer.
   */
  @Overwrite
  public void init() {
    if (this.minecraft == null) {
      return; // To shut Intellij up
    }

    if (this.initedOnce) {
      this.serverSelectionList.setRectangle(this.width, this.height - 64 - 32, 0, 32);
    } else {
      this.initedOnce = true;
      this.servers = new ServerList(this.minecraft);
      this.servers.load();
      this.lanServerList = new LanServerDetection.LanServerList();

      try {
        this.lanServerDetector = new LanServerDetection.LanServerDetector(this.lanServerList);
        this.lanServerDetector.start();
      } catch (Exception exception) {
        LOGGER.warn("Unable to start LAN server detection: {}", exception.getMessage());
      }

      this.serverSelectionList =
        new ServerSelectionList((JoinMultiplayerScreen) Minecraft.getInstance().screen,
          this.minecraft,
          this.width,
          this.height - 64 - 32,
          32,
          36);
      this.serverSelectionList.updateOnlineServers(this.servers);
    }

    this.addRenderableWidget(this.serverSelectionList);

    this.font = Minecraft.getInstance().font;

    this.uuidSpoofButton = this.addRenderableWidget(Button.builder(Component.literal("UUIDSpoof"),
      onPress -> Minecraft.getInstance().setScreen(new UUIDSpoofScreen(this))).width(100).build());

    this.bungeeToggleButton = this.addRenderableWidget(Button.builder(getBungeeButtonText(), onPress -> {
      this.bungeeSpoofMod.isIPForwarding = !bungeeSpoofMod.isIPForwarding;
      this.bungeeToggleButton.setMessage(getBungeeButtonText());
    }).width(100).build());

    this.bungeeHostnameToggle = this.addRenderableWidget(Button.builder(getBungeeTargetButtonText(), onPress -> {
      this.bungeeSpoofMod.isHostnameForwarding = !bungeeSpoofMod.isHostnameForwarding;
      this.bungeeHostnameToggle.setMessage(getBungeeTargetButtonText());
    }).width(100).build());

    this.bungeeClientIPField = new EditBox(this.font, 74, 20, Component.literal("Bungee IP"));
    this.bungeeClientIPField.setMaxLength(128);
    this.bungeeClientIPField.setValue(bungeeSpoofMod.ip);
    this.bungeeClientIPField.setResponder((text) -> bungeeSpoofMod.ip = this.bungeeClientIPField.getValue());
    this.addWidget(this.bungeeClientIPField);

    this.bungeeHostnameField = new EditBox(this.font, 74, 20, Component.literal("Hostname"));
    this.bungeeHostnameField.setMaxLength(128);
    this.bungeeHostnameField.setValue(bungeeSpoofMod.hostname);
    this.bungeeHostnameField.setResponder((text) -> bungeeSpoofMod.hostname = this.bungeeHostnameField.getValue());
    this.addWidget(this.bungeeHostnameField);

    this.selectButton =
      this.addRenderableWidget(Button.builder(Component.translatable("selectServer.select"), (button) -> this.joinSelectedServer())
        .width(100)
        .build());

    Button buttonWidget =
      this.addRenderableWidget(Button.builder(Component.translatable("selectServer.direct"), (button) -> {
        this.editingServer =
          new ServerData(I18n.get("selectServer.defaultName"), "", ServerData.Type.OTHER);
        this.minecraft.setScreen(new DirectJoinServerScreen(this, this::directJoinCallback, this.editingServer));
      }).width(100).build());

    Button buttonWidget2 =
      this.addRenderableWidget(Button.builder(Component.translatable("selectServer.add"), (button) -> {
        this.editingServer =
          new ServerData(I18n.get("selectServer.defaultName"), "", ServerData.Type.OTHER);
        this.minecraft.setScreen(new EditServerScreen(this, this::addServerCallback, this.editingServer));
      }).width(100).build());

    this.editButton = this.addRenderableWidget(Button.builder(Component.translatable("selectServer.edit"), (button) -> {
      ServerSelectionList.Entry entry = this.serverSelectionList.getSelected();
      if (entry instanceof ServerSelectionList.OnlineServerEntry) {
        ServerData serverInfo = ((ServerSelectionList.OnlineServerEntry) entry).getServerData();
        this.editingServer = new ServerData(serverInfo.name, serverInfo.ip, ServerData.Type.OTHER);
        this.editingServer.copyFrom(serverInfo);
        this.minecraft.setScreen(new EditServerScreen(this, this::editServerCallback, this.editingServer));
      }
    }).width(74).build());

    this.deleteButton =
      this.addRenderableWidget(Button.builder(Component.translatable("selectServer.delete"), (button) -> {
        ServerSelectionList.Entry entry = this.serverSelectionList.getSelected();
        if (entry instanceof ServerSelectionList.OnlineServerEntry) {
          String string = ((ServerSelectionList.OnlineServerEntry) entry).getServerData().name;
          if (string != null) {
            Component text = Component.translatable("selectServer.deleteQuestion");
            Component text2 = Component.translatable("selectServer.deleteWarning", string);
            Component text3 = Component.translatable("selectServer.deleteButton");
            Component text4 = CommonComponents.GUI_CANCEL;
            this.minecraft.setScreen(new ConfirmScreen(this::deleteCallback, text, text2, text3, text4));
          }
        }
      }).width(74).build());

    Button buttonWidget3 =
      this.addRenderableWidget(Button.builder(Component.translatable("selectServer.refresh"), (button) -> this.refreshServerList())
        .width(74)
        .build());

    Button buttonWidget4 =
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, (button) -> this.onClose()).width(74).build());

    LinearLayout directionalLayoutWidget = LinearLayout.vertical();

    EqualSpacingLayout axisGridWidget =
      directionalLayoutWidget.addChild(new EqualSpacingLayout(550, 20, EqualSpacingLayout.Orientation.HORIZONTAL));
    axisGridWidget.addChild(this.uuidSpoofButton);
    axisGridWidget.addChild(this.selectButton);
    axisGridWidget.addChild(buttonWidget);
    axisGridWidget.addChild(buttonWidget2);
    axisGridWidget.addChild(this.bungeeToggleButton);

    directionalLayoutWidget.addChild(SpacerElement.height(4));

    EqualSpacingLayout axisGridWidget2 =
      directionalLayoutWidget.addChild(new EqualSpacingLayout(550, 20, EqualSpacingLayout.Orientation.HORIZONTAL));
    axisGridWidget2.addChild(this.bungeeClientIPField);
    axisGridWidget2.addChild(this.editButton);
    axisGridWidget2.addChild(this.bungeeHostnameToggle);
    axisGridWidget2.addChild(this.deleteButton);
    axisGridWidget2.addChild(buttonWidget3);
    axisGridWidget2.addChild(buttonWidget4);
    axisGridWidget2.addChild(this.bungeeHostnameField);

    directionalLayoutWidget.arrangeElements();
    FrameLayout.centerInRectangle(directionalLayoutWidget, 0, this.height - 64, this.width, 64);

    this.addRenderableOnly(this.bungeeClientIPField);
    this.addRenderableOnly(this.bungeeHostnameField);

    this.onSelectedChange();
  }

  /**
   * Gets the text for the BungeeCord toggle button based on its current state.
   *
   * @return The text to display on the BungeeCord button.
   */
  @Unique private Component getBungeeButtonText() {
    return bungeeSpoofMod.isIPForwarding ? Component.literal("Bungee Enabled") : Component.literal("Bungee Disabled");
  }

  /**
   * Gets the text for the BungeeCord target hostname toggle button based on its current state.
   *
   * @return The text to display on the BungeeCord target hostname button.
   */
  @Unique private Component getBungeeTargetButtonText() {
    return bungeeSpoofMod.isHostnameForwarding ? Component.literal("Hostname Enabled") : Component.literal("Hostname Disabled");
  }

  @Shadow public abstract void joinSelectedServer();

  @Shadow protected abstract void directJoinCallback(boolean confirmedAction);

  @Shadow protected abstract void addServerCallback(boolean confirmedAction);

  @Shadow protected abstract void editServerCallback(boolean confirmedAction);

  @Shadow protected abstract void deleteCallback(boolean confirmedAction);

  @Shadow protected abstract void refreshServerList();

  @Shadow protected abstract void onSelectedChange();
}
