package net.paradise_client.inject.mixin.gui.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.*;
import net.minecraft.network.chat.Component;
import net.paradise_client.util.IPUtil;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

@Mixin(ManageServerScreen.class) public abstract class AddServerScreenMixin extends Screen {
  @Shadow private EditBox ipEdit;

  @Unique private IPUtil.IPInfo ipInfo;
  @Unique private boolean loading = false;
  @Unique private String lastAddress = "";

  protected AddServerScreenMixin(Component title) {
    super(title);
  }

  @Inject(method = "init", at = @At("TAIL")) private void onInit(CallbackInfo ci) {
    if (ipEdit != null) {
      lastAddress = ipEdit.getValue();
      if (!lastAddress.isEmpty()) {
        updateIPInfo();
      }
    }
  }

  @Unique private void updateIPInfo() {
    if (lastAddress.isEmpty()) {
      ipInfo = null;
      return;
    }
    loading = true;
    CompletableFuture.runAsync(() -> {
      try {
        String host = lastAddress;
        if (host.contains(":")) {
          host = host.split(":")[0];
        }
        ipInfo = IPUtil.getIPInfo(host);
      } catch (Exception e) {
        ipInfo = null;
      } finally {
        loading = false;
      }
    });
  }

  @Inject(method = "extractRenderState", at = @At("TAIL"))
  private void onRender(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
    if (ipEdit != null) {
      String currentAddress = ipEdit.getValue();
      if (!currentAddress.equals(lastAddress)) {
        lastAddress = currentAddress;
        updateIPInfo();
      }
    }

    int x = 20;
    int y = this.height / 2 - 60;

    drawInfo(graphics, "Organization", ipInfo == null ? "Unknown" : ipInfo.organisation, x, y);
    drawInfo(graphics, "Country", ipInfo == null ? "Unknown" : ipInfo.country, x, y + 12);
    drawInfo(graphics, "City", ipInfo == null ? "Unknown" : ipInfo.city, x, y + 24);
    drawInfo(graphics, "Region", ipInfo == null ? "Unknown" : ipInfo.region, x, y + 36);
    drawInfo(graphics, "AS", ipInfo == null ? "Unknown" : ipInfo.as, x, y + 48);
    drawInfo(graphics, "ISP", ipInfo == null ? "Unknown" : ipInfo.isp, x, y + 60);
    drawInfo(graphics, "Timezone", ipInfo == null ? "Unknown" : ipInfo.timezone, x, y + 72);
    drawInfo(graphics, "IP", ipInfo == null ? "Unknown" : ipInfo.ip, x, y + 84);
    drawInfo(graphics, "Country Code", ipInfo == null ? "Unknown" : ipInfo.countryCode, x, y + 96);
  }

  @Unique private void drawInfo(GuiGraphicsExtractor graphics, String label, String value, int x, int y) {
    String displayValue = loading ? "§cLoading..." : "§c" + value;
    graphics.text(this.font, label + " » " + displayValue, x, y, 0xFFFFFF);
  }
}
