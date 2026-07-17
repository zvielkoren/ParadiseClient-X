package net.paradise_client.inject.mixin.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.*;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.gui.screens.multiplayer.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.server.LanServerDetection;
import net.minecraft.network.chat.*;
import net.paradise_client.ParadiseClient;
import net.paradise_client.mod.BungeeSpoofMod;
import net.paradise_client.screen.UUIDSpoofScreen;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.*;

@Mixin(JoinMultiplayerScreen.class) public abstract class MultiplayerScreenMixin extends Screen {

  @Shadow @Final private static Logger LOGGER;
  @Shadow @Final private HeaderAndFooterLayout layout;

  @Unique final BungeeSpoofMod bungeeSpoofMod = ParadiseClient.BUNGEE_SPOOF_MOD;

  @Shadow protected ServerSelectionList serverSelectionList;
  @Unique private Button uuidSpoofButton;
  @Unique private Button bungeeToggleButton;
  @Unique private EditBox bungeeClientIPField;
  @Unique private Button bungeeHostnameToggle;
  @Unique private EditBox bungeeHostnameField;

  @Shadow private ServerList servers;
  @Shadow private LanServerDetection.LanServerList lanServerList;
  @Shadow @Nullable private LanServerDetection.LanServerDetector lanServerDetector;
  @Shadow private Button selectButton;
  @Shadow private ServerData editingServer;
  @Shadow private Button editButton;
  @Shadow private Button deleteButton;

  protected MultiplayerScreenMixin(Component title) {
    super(title);
  }

  /**
   * @author SpigotRCE
   * @reason Overwriting to append custom bungee and UUID layout options to the multiplayer screen footer.
   */
  @Overwrite public void init() {
    this.layout.addTitleHeader(this.title, this.font);
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
      (ServerSelectionList) this.layout.addToContents(new ServerSelectionList((JoinMultiplayerScreen) (Object) this,
        this.minecraft,
        this.width,
        this.layout.getContentHeight(),
        this.layout.getHeaderHeight(),
        36));
    this.serverSelectionList.updateOnlineServers(this.servers);

    this.uuidSpoofButton = Button.builder(Component.literal("UUIDSpoof"),
      onPress -> Minecraft.getInstance().gui.setScreen(new UUIDSpoofScreen(this))).width(100).build();

    this.bungeeToggleButton = Button.builder(getBungeeButtonText(), onPress -> {
      this.bungeeSpoofMod.isIPForwarding = !bungeeSpoofMod.isIPForwarding;
      this.bungeeToggleButton.setMessage(getBungeeButtonText());
    }).width(100).build();

    this.bungeeHostnameToggle = Button.builder(getBungeeTargetButtonText(), onPress -> {
      this.bungeeSpoofMod.isHostnameForwarding = !bungeeSpoofMod.isHostnameForwarding;
      this.bungeeHostnameToggle.setMessage(getBungeeTargetButtonText());
    }).width(100).build();

    this.bungeeClientIPField = new EditBox(this.font, 74, 20, Component.literal("Bungee IP"));
    this.bungeeClientIPField.setMaxLength(128);
    this.bungeeClientIPField.setValue(bungeeSpoofMod.ip);
    this.bungeeClientIPField.setResponder((text) -> bungeeSpoofMod.ip = this.bungeeClientIPField.getValue());

    this.bungeeHostnameField = new EditBox(this.font, 74, 20, Component.literal("Hostname"));
    this.bungeeHostnameField.setMaxLength(128);
    this.bungeeHostnameField.setValue(bungeeSpoofMod.hostname);
    this.bungeeHostnameField.setResponder((text) -> bungeeSpoofMod.hostname = this.bungeeHostnameField.getValue());

    this.selectButton = Button.builder(Component.translatable("selectServer.select"), (button) -> {
      ServerSelectionList.Entry entry = (ServerSelectionList.Entry) this.serverSelectionList.getSelected();
      if (entry != null) {
        entry.join();
      }
    }).width(100).build();

    Button buttonWidget = Button.builder(Component.translatable("selectServer.direct"), (button) -> {
      this.editingServer = new ServerData(I18n.get("selectServer.defaultName"), "", ServerData.Type.OTHER);
      this.minecraft.gui.setScreen(new DirectJoinServerScreen(this, this::directJoinCallback, this.editingServer));
    }).width(100).build();

    Button buttonWidget2 = Button.builder(Component.translatable("selectServer.add"), (button) -> {
      this.editingServer = new ServerData("", "", ServerData.Type.OTHER);
      this.minecraft.gui.setScreen(new ManageServerScreen(this,
        Component.translatable("manageServer.add.title"),
        this::addServerCallback,
        this.editingServer));
    }).width(100).build();

    this.editButton = Button.builder(Component.translatable("selectServer.edit"), (button) -> {
      ServerSelectionList.Entry entry = this.serverSelectionList.getSelected();
      if (entry instanceof ServerSelectionList.OnlineServerEntry) {
        ServerData serverInfo = ((ServerSelectionList.OnlineServerEntry) entry).getServerData();
        this.editingServer = new ServerData(serverInfo.name, serverInfo.ip, ServerData.Type.OTHER);
        this.editingServer.copyFrom(serverInfo);
        this.minecraft.gui.setScreen(new ManageServerScreen(this,
          Component.translatable("manageServer.edit.title"),
          this::editServerCallback,
          this.editingServer));
      }
    }).width(74).build();

    this.deleteButton = Button.builder(Component.translatable("selectServer.delete"), (button) -> {
      ServerSelectionList.Entry entry = this.serverSelectionList.getSelected();
      if (entry instanceof ServerSelectionList.OnlineServerEntry) {
        String string = ((ServerSelectionList.OnlineServerEntry) entry).getServerData().name;
        if (string != null) {
          Component text = Component.translatable("selectServer.deleteQuestion");
          Component text2 = Component.translatable("selectServer.deleteWarning", string);
          Component text3 = Component.translatable("selectServer.deleteButton");
          Component text4 = CommonComponents.GUI_CANCEL;
          this.minecraft.gui.setScreen(new ConfirmScreen(this::deleteCallback, text, text2, text3, text4));
        }
      }
    }).width(74).build();

    Button buttonWidget3 =
      Button.builder(Component.translatable("selectServer.refresh"), (button) -> this.refreshServerList())
        .width(74)
        .build();
    Button buttonWidget4 = Button.builder(CommonComponents.GUI_BACK, (button) -> this.onClose()).width(74).build();

    LinearLayout directionalLayoutWidget = LinearLayout.vertical().spacing(4);
    directionalLayoutWidget.defaultCellSetting().alignHorizontallyCenter();

    EqualSpacingLayout axisGridWidget =
      directionalLayoutWidget.addChild(new EqualSpacingLayout(550, 20, EqualSpacingLayout.Orientation.HORIZONTAL));
    axisGridWidget.addChild(this.uuidSpoofButton);
    axisGridWidget.addChild(this.selectButton);
    axisGridWidget.addChild(buttonWidget);
    axisGridWidget.addChild(buttonWidget2);
    axisGridWidget.addChild(this.bungeeToggleButton);

    EqualSpacingLayout axisGridWidget2 =
      directionalLayoutWidget.addChild(new EqualSpacingLayout(550, 20, EqualSpacingLayout.Orientation.HORIZONTAL));
    axisGridWidget2.addChild(this.bungeeClientIPField);
    axisGridWidget2.addChild(this.editButton);
    axisGridWidget2.addChild(this.bungeeHostnameToggle);
    axisGridWidget2.addChild(this.deleteButton);
    axisGridWidget2.addChild(buttonWidget3);
    axisGridWidget2.addChild(buttonWidget4);
    axisGridWidget2.addChild(this.bungeeHostnameField);

    this.layout.addToFooter(directionalLayoutWidget);
    this.layout.visitWidgets(this::addRenderableWidget);

    this.repositionElements();
    this.onSelectedChange();
  }

  @Overwrite protected void repositionElements() {
    this.layout.arrangeElements();
    if (this.serverSelectionList != null) {
      this.serverSelectionList.updateSize(this.width, this.layout);
    }
  }

  @Unique private Component getBungeeButtonText() {
    return bungeeSpoofMod.isIPForwarding ? Component.literal("Bungee Enabled") : Component.literal("Bungee Disabled");
  }

  @Unique private Component getBungeeTargetButtonText() {
    return bungeeSpoofMod.isHostnameForwarding ?
      Component.literal("Hostname Enabled") :
      Component.literal("Hostname Disabled");
  }

  @Shadow protected abstract void directJoinCallback(boolean confirmedAction);

  @Shadow protected abstract void addServerCallback(boolean confirmedAction);

  @Shadow protected abstract void editServerCallback(boolean confirmedAction);

  @Shadow protected abstract void deleteCallback(boolean confirmedAction);

  @Shadow protected abstract void refreshServerList();

  @Shadow protected abstract void onSelectedChange();
}
