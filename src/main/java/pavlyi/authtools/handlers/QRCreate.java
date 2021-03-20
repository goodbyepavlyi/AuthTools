package pavlyi.authtools.handlers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import pavlyi.authtools.AuthTools;

public class QRCreate {
	private static AuthTools instance = AuthTools.getInstance();

	@SuppressWarnings("deprecation")
	public static void create(Player p, String secretKey) {
		try {
			String qrCodeData = "otpauth://totp/"+p.getName()+"?secret="+secretKey+"&issuer="+instance.getConfigHandler().SETTINGS_HOOK_INTO_AUTHME;
			String filePath = instance.getDataFolder().toString()+"/tempFiles/temp-qrcode-"+p.getName()+".png";
			String charset = "UTF-8";
			Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
			hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
			BitMatrix matrix = new MultiFormatWriter().encode(new String(qrCodeData.getBytes(charset), charset), BarcodeFormat.QR_CODE, 200, 200, hintMap);
			MatrixToImageWriter.writeToFile(matrix, filePath.substring(filePath.lastIndexOf('.') + 1), new File(filePath));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
