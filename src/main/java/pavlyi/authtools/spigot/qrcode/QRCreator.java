package pavlyi.authtools.spigot.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.bukkit.entity.Player;
import pavlyi.authtools.spigot.AuthTools;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class QRCreator {
    private static final AuthTools instance = AuthTools.getInstance();

    @SuppressWarnings("deprecation")
    public static void create(Player p, String qrData) {
        try {
            String filePath = instance.getDataFolder().toString() + "/tempFiles/temp-qrcode-" + p.getName() + ".png";
            String charset = "UTF-8";
            Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            BitMatrix matrix = new MultiFormatWriter().encode(new String(qrData.getBytes(charset), charset), BarcodeFormat.QR_CODE, 200, 200, hintMap);
            MatrixToImageWriter.writeToFile(matrix, filePath.substring(filePath.lastIndexOf('.') + 1), new File(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
