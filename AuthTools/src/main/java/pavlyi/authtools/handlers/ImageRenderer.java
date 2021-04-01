package pavlyi.authtools.handlers;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.URL;

import javax.imageio.ImageIO;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class ImageRenderer extends MapRenderer {

	// So fancy.
	private SoftReference<BufferedImage> cacheImage;
	private boolean hasRendered = false;

	public ImageRenderer(String url) throws IOException {
		this.cacheImage = new SoftReference<BufferedImage>(this.getImage(url));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void render(MapView view, MapCanvas canvas, Player player) {
		if (this.hasRendered) {
			return;
		}

		if (this.cacheImage.get() != null) {
			canvas.drawImage(0, 0, this.cacheImage.get());
			this.hasRendered = true;
		} else {
			player.sendMessage(ChatColor.RED + "Attempted to render the image, but the cached image was null!");
			this.hasRendered = true;
		}
	}

	public BufferedImage getImage(String url) throws IOException {
		boolean useCache = ImageIO.getUseCache();

		// Temporarily disable cache, if it isn't already,
		// so we can get the latest image.
		ImageIO.setUseCache(false);

		BufferedImage image = resize(new File(url), new Dimension(128, 128));
		// TODO find import for RenderUtils
		// RenderUtils.resizeImage(image);

		// Renable it with the old value.
		ImageIO.setUseCache(useCache);

		return image;
	}

	public BufferedImage resize(final File url, final Dimension size) throws IOException {
		final BufferedImage image = ImageIO.read(url);
		final BufferedImage resized = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g = resized.createGraphics();
		g.drawImage(image, 0, 0, size.width, size.height, null);
		g.dispose();
		return resized;
	}

}
